package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranjan.malav.morselight_flashlightwithmorsecode.data.SettingsRepository
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.KeyClassifier
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.MorseCode
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.unitMillis
import com.ranjan.malav.morselight_flashlightwithmorsecode.torch.TorchController
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class RxMode { Manual, Camera }

data class ReceiveUiState(
    val mode: RxMode = RxMode.Manual,
    val buffer: String = "",
    val decoded: String = "",
    val keyOn: Boolean = false,
    // camera
    val sensitivity: Int = 30,
    val detectionArea: Int = 50,
    val luminance: Double = 0.0,
    val reading: Boolean = false,
)

/**
 * Decoding via the reworked logic (§7.3): manual keying through [KeyClassifier], camera through a
 * mean-luminance threshold that feeds the same classifier. WPM comes from settings so the unit
 * length matches the sender.
 */
class ReceiveViewModel(
    private val torch: TorchController,
    private val settings: SettingsRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(ReceiveUiState())
    val ui: StateFlow<ReceiveUiState> = _ui.asStateFlow()

    private var wpm = 12
    private var classifier = KeyClassifier(unitMillis(wpm))
    private var idleJob: Job? = null

    // camera pulse tracking
    private var camLightOn = false
    private var lowAvg = 0.0
    private var lowCount = 0

    init {
        viewModelScope.launch {
            settings.settings.collect { s ->
                wpm = s.wpm
                classifier = KeyClassifier(unitMillis(wpm))
                _ui.update { it.copy(sensitivity = s.perceptibility, detectionArea = s.reactSize) }
                torch.setDetectionArea(s.reactSize)
            }
        }
        viewModelScope.launch {
            torch.luminosity.collect { onLuminance(it) }
        }
    }

    fun setMode(mode: RxMode) = _ui.update { it.copy(mode = mode) }

    // ---- manual keying ----
    fun keyDown() {
        idleJob?.cancel()
        classifier.onDown(System.currentTimeMillis())
        _ui.update { it.copy(keyOn = true, buffer = classifier.buffer) }
    }

    fun keyUp() {
        classifier.onUp(System.currentTimeMillis())
        _ui.update { it.copy(keyOn = false, buffer = classifier.buffer, decoded = MorseCode.decode(classifier.buffer)) }
        scheduleIdleCommits()
    }

    private fun scheduleIdleCommits() {
        idleJob?.cancel()
        val u = unitMillis(wpm)
        idleJob = viewModelScope.launch {
            kotlinx.coroutines.delay((u * 3.5).toLong())
            classifier.commitCharacterGap()
            _ui.update { it.copy(buffer = classifier.buffer, decoded = MorseCode.decode(classifier.buffer)) }
            kotlinx.coroutines.delay((u * 4.5).toLong())
            classifier.commitWordGap()
            _ui.update { it.copy(buffer = classifier.buffer, decoded = MorseCode.decode(classifier.buffer)) }
        }
    }

    fun reset() {
        classifier.reset()
        camLightOn = false; lowAvg = 0.0; lowCount = 0
        _ui.update { it.copy(buffer = "", decoded = "", reading = false) }
    }

    fun setSensitivity(v: Int) { _ui.update { it.copy(sensitivity = v) }; viewModelScope.launch { settings.setPerceptibility(v) } }
    fun setDetectionArea(v: Int) { torch.setDetectionArea(v); _ui.update { it.copy(detectionArea = v) }; viewModelScope.launch { settings.setReactSize(v) } }

    // ---- camera decode: threshold crossings become key events ----
    private fun onLuminance(luma: Double) {
        if (_ui.value.mode != RxMode.Camera) return
        // establish a rolling baseline of the "dark" level
        if (!camLightOn) { lowAvg = (lowAvg * lowCount + luma) / (lowCount + 1); lowCount++ }
        val threshold = lowAvg * (1 + _ui.value.sensitivity / 100.0)
        _ui.update { it.copy(luminance = luma) }
        if (luma > threshold && !camLightOn) {
            camLightOn = true
            classifier.onDown(System.currentTimeMillis())
            _ui.update { it.copy(reading = true, buffer = classifier.buffer) }
        } else if (luma <= threshold && camLightOn) {
            camLightOn = false
            classifier.onUp(System.currentTimeMillis())
            _ui.update { it.copy(reading = false, buffer = classifier.buffer, decoded = MorseCode.decode(classifier.buffer)) }
            scheduleIdleCommits()
        }
    }
}
