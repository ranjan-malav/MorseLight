package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.KeyClassifier
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.MorseCode
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.TransmitEngine
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.unitMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val TARGETS = listOf("LETS GO", "ON MY WAY", "ALL CLEAR", "NEED WATER", "MEET AT NINE")
private const val DRILL_WPM = 12

data class DecodingDrillUi(
    val target: String = TARGETS[0],
    val playing: Boolean = false,
    val senderOn: Boolean = false,
    val reveal: Boolean = false,
    val buffer: String = "",
    val copied: String = "",
) {
    val matched get() = copied.isNotBlank() && copied == target
    val masked get() = target.map { if (it == ' ') ' ' else '•' }.joinToString("")
}

class DecodingDrillViewModel : ViewModel() {
    private val _ui = MutableStateFlow(DecodingDrillUi())
    val ui: StateFlow<DecodingDrillUi> = _ui.asStateFlow()

    private var index = 0
    private var classifier = KeyClassifier(unitMillis(DRILL_WPM))
    private var playJob: Job? = null
    private var idleJob: Job? = null

    // Sender disc flashes the message; the real torch is untouched during drills.
    private val engine = TransmitEngine(
        setTorch = { on -> _ui.update { it.copy(senderOn = on) } },
        onState = { st -> if (!st.running) _ui.update { it.copy(playing = false, senderOn = false) } },
    )

    fun play() {
        if (_ui.value.playing) { stop(); return }
        classifier = KeyClassifier(unitMillis(DRILL_WPM))
        _ui.update { it.copy(playing = true, buffer = "", copied = "") }
        playJob = viewModelScope.launch { engine.transmit(MorseCode.encode(_ui.value.target), DRILL_WPM) }
    }

    private fun stop() { playJob?.cancel(); _ui.update { it.copy(playing = false, senderOn = false) } }

    fun next() {
        stop()
        index = (index + 1) % TARGETS.size
        classifier.reset()
        _ui.value = DecodingDrillUi(target = TARGETS[index])
    }

    fun toggleReveal() = _ui.update { it.copy(reveal = !it.reveal) }

    fun copyDown() { idleJob?.cancel(); classifier.onDown(System.currentTimeMillis()); _ui.update { it.copy(buffer = classifier.buffer) } }
    fun copyUp() {
        classifier.onUp(System.currentTimeMillis())
        _ui.update { it.copy(buffer = classifier.buffer, copied = MorseCode.decode(classifier.buffer)) }
        val u = unitMillis(DRILL_WPM)
        idleJob = viewModelScope.launch {
            delay((u * 3.5).toLong()); classifier.commitCharacterGap()
            _ui.update { it.copy(buffer = classifier.buffer, copied = MorseCode.decode(classifier.buffer)) }
            delay((u * 4.5).toLong()); classifier.commitWordGap()
            _ui.update { it.copy(buffer = classifier.buffer, copied = MorseCode.decode(classifier.buffer)) }
        }
    }

    fun resetCopy() { classifier.reset(); _ui.update { it.copy(buffer = "", copied = "") } }
}
