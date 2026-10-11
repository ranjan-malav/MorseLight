package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranjan.malav.morselight_flashlightwithmorsecode.data.SettingsRepository
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.AdaptiveDecoder
import com.ranjan.malav.morselight_flashlightwithmorsecode.torch.TorchController
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
    val average: Double = 0.0,
    val reading: Boolean = false,
    val calibrating: Boolean = false,
    val armed: Boolean = false, // camera detects only after a manual Calibrate
    val keepAwake: Boolean = true,
)

/**
 * Decoding through the speed-agnostic [AdaptiveDecoder]: manual keying and camera pulses both feed
 * mark/gap timings that are clustered into dots/dashes from their own distribution — the receiver
 * never sets a WPM. The camera side turns luminance threshold crossings into the same key events.
 */
class ReceiveViewModel(
    private val torch: TorchController,
    private val settings: SettingsRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(ReceiveUiState())
    val ui: StateFlow<ReceiveUiState> = _ui.asStateFlow()

    private val decoder = AdaptiveDecoder()

    // camera pulse tracking: EMA of the ambient "dark" level (forgets old samples).
    // On entering camera mode we spend a short window averaging ambient luminosity so the
    // baseline is stable before we start detecting flashes (mirrors the old app's warm-up).
    private var camLightOn = false
    private var baseline = -1.0
    private var calibrationEndAt = 0L
    private var calSum = 0.0
    private var calCount = 0

    init {
        viewModelScope.launch {
            settings.settings.collect { s ->
                _ui.update { it.copy(sensitivity = s.perceptibility, detectionArea = s.reactSize, keepAwake = s.keepAwake) }
                torch.setDetectionArea(s.reactSize)
            }
        }
        viewModelScope.launch {
            torch.luminosity.collect { onLuminance(it) }
        }
    }

    fun setMode(mode: RxMode) {
        // Entering the camera we sit idle (preview + live brightness) but do NOT detect until the
        // user aims and taps Calibrate.
        if (mode == RxMode.Camera && _ui.value.mode != RxMode.Camera) {
            decoder.reset()
            camLightOn = false; baseline = -1.0
            _ui.update { it.copy(armed = false, calibrating = false, reading = false, buffer = "", decoded = "", average = 0.0) }
        }
        _ui.update { it.copy(mode = mode) }
    }

    /** User-triggered: aim at the source, then measure the ambient baseline and start detecting. */
    fun recalibrate() {
        if (_ui.value.mode == RxMode.Camera) startCalibration()
    }

    /** Short ambient-luminosity warm-up; arms detection when it completes. */
    private fun startCalibration() {
        decoder.reset()
        camLightOn = false
        baseline = -1.0
        calSum = 0.0; calCount = 0
        calibrationEndAt = System.currentTimeMillis() + CALIBRATION_MS
        _ui.update { it.copy(calibrating = true, armed = false, reading = false, buffer = "", decoded = "") }
    }

    // ---- manual keying ----
    fun keyDown() {
        decoder.onDown(System.currentTimeMillis())
        _ui.update { it.copy(keyOn = true) }
    }

    fun keyUp() {
        decoder.onUp(System.currentTimeMillis())
        _ui.update { it.copy(keyOn = false, buffer = decoder.morse, decoded = decoder.text) }
    }

    fun reset() {
        decoder.reset()
        camLightOn = false
        // In camera mode keep the calibrated baseline + armed state; Reset only clears the copy.
        if (_ui.value.mode != RxMode.Camera) baseline = -1.0
        _ui.update { it.copy(buffer = "", decoded = "", reading = false) }
    }

    fun setSensitivity(v: Int) { _ui.update { it.copy(sensitivity = v) }; viewModelScope.launch { settings.setPerceptibility(v) } }
    fun setDetectionArea(v: Int) { torch.setDetectionArea(v); _ui.update { it.copy(detectionArea = v) }; viewModelScope.launch { settings.setReactSize(v) } }

    // ---- camera decode: threshold crossings become key events ----
    private fun onLuminance(luma: Double) {
        if (_ui.value.mode != RxMode.Camera) return

        // Warm-up: average ambient luminosity into a stable baseline, then arm detection.
        if (_ui.value.calibrating) {
            calSum += luma; calCount++
            val avg = calSum / calCount
            if (System.currentTimeMillis() >= calibrationEndAt) {
                baseline = avg
                _ui.update { it.copy(luminance = luma, average = avg, calibrating = false, armed = true) }
            } else {
                _ui.update { it.copy(luminance = luma, average = avg) }
            }
            return
        }

        // Idle until calibrated: show live brightness but never trip "reading".
        if (!_ui.value.armed) {
            _ui.update { it.copy(luminance = luma) }
            return
        }

        // The baseline is FIXED at the calibrated ambient average — it must not drift. Letting it
        // track the live reading would move the on/off threshold and blur the pulse edges. A flash
        // reads above this fixed average; releasing brings the reading back down near it.
        if (baseline < 0) { _ui.update { it.copy(luminance = luma) }; return }
        val threshold = baseline * (1 + _ui.value.sensitivity / 100.0)
        _ui.update { it.copy(luminance = luma) }
        if (luma > threshold && !camLightOn) {
            camLightOn = true
            decoder.onDown(System.currentTimeMillis())
            _ui.update { it.copy(reading = true) }
        } else if (luma <= threshold && camLightOn) {
            camLightOn = false
            decoder.onUp(System.currentTimeMillis())
            _ui.update { it.copy(reading = false, buffer = decoder.morse, decoded = decoder.text) }
        }
    }

    private companion object {
        const val CALIBRATION_MS = 2500L
    }
}
