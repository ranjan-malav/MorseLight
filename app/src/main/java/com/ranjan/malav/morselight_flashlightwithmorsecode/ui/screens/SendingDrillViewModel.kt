package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.charToMorse
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.unitMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val ORDER = (('A'..'Z') + ('0'..'9')).toList()
private const val DRILL_WPM = 12

enum class SdResult { None, Correct, Wrong }

data class SendingDrillUi(
    val target: Char = ORDER[0],
    val buffer: String = "",
    val result: SdResult = SdResult.None,
    val showHint: Boolean = false,
    val keyOn: Boolean = false,
) {
    val code: String get() = charToMorse[target]?.trim() ?: ""
}

class SendingDrillViewModel : ViewModel() {
    private val _ui = MutableStateFlow(SendingDrillUi())
    val ui: StateFlow<SendingDrillUi> = _ui.asStateFlow()

    private var index = 0
    private var downAt = 0L

    fun keyDown() { downAt = System.currentTimeMillis(); _ui.update { it.copy(keyOn = true) } }

    fun keyUp() {
        val dur = (System.currentTimeMillis() - downAt) / unitMillis(DRILL_WPM)
        val element = if (dur < 2) '.' else '-'
        val buffer = _ui.value.buffer + element
        val target = _ui.value.code
        val result = when {
            buffer == target -> SdResult.Correct
            !target.startsWith(buffer) -> SdResult.Wrong
            else -> SdResult.None
        }
        _ui.update { it.copy(keyOn = false, buffer = buffer, result = result) }
        if (result == SdResult.Correct) viewModelScope.launch { delay(900); next() }
    }

    fun next() {
        index = (index + 1) % ORDER.size
        _ui.value = SendingDrillUi(target = ORDER[index])
    }

    fun clear() = _ui.update { it.copy(buffer = "", result = SdResult.None) }
    fun toggleHint() = _ui.update { it.copy(showHint = !it.showHint) }
    fun skip() = next()
}
