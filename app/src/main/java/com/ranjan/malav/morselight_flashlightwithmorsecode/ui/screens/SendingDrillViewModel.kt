package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranjan.malav.morselight_flashlightwithmorsecode.data.SettingsRepository
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.charToMorse
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.unitMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Single characters first, then a few whole words. Words are keyed letter by letter.
private val ITEMS: List<String> =
    (('A'..'Z') + ('0'..'9')).map { it.toString() } +
        listOf("SOS", "OK", "HELLO", "HELP", "YES", "CQ", "MORSE", "LIGHT", "WATER", "NIGHT")
private const val DRILL_WPM = 12

enum class SdResult { None, Correct, Wrong }

data class SendingDrillUi(
    val target: String = ITEMS[0],
    val letterIndex: Int = 0, // which letter of [target] is being keyed (== length once complete)
    val buffer: String = "",
    val result: SdResult = SdResult.None,
    val keyOn: Boolean = false,
    val random: Boolean = false, // showing a random item that doesn't move saved progress
) {
    val currentChar: Char get() = target.getOrElse(letterIndex) { target.lastOrNull() ?: ' ' }
    /** Morse for the letter being keyed right now. */
    val code: String get() = charToMorse[currentChar]?.trim() ?: ""
    val isWord: Boolean get() = target.length > 1
}

class SendingDrillViewModel(private val settings: SettingsRepository) : ViewModel() {
    private val _ui = MutableStateFlow(SendingDrillUi())
    val ui: StateFlow<SendingDrillUi> = _ui.asStateFlow()

    private var progressIndex = 0   // saved sequential position (persisted)
    private var randomIndex: Int? = null // set while a random detour is shown
    private var downAt = 0L

    init {
        viewModelScope.launch {
            progressIndex = settings.sendingDrillIndex.first().coerceIn(0, ITEMS.lastIndex)
            show(progressIndex, random = false)
        }
    }

    private fun show(i: Int, random: Boolean) {
        _ui.value = SendingDrillUi(target = ITEMS[i], random = random)
    }

    fun keyDown() { downAt = System.currentTimeMillis(); _ui.update { it.copy(keyOn = true) } }

    fun keyUp() {
        val dur = (System.currentTimeMillis() - downAt) / unitMillis(DRILL_WPM)
        val element = if (dur < 2) '.' else '-'
        val buffer = _ui.value.buffer + element
        val code = _ui.value.code
        when {
            buffer == code -> onLetterComplete()
            !code.startsWith(buffer) -> onWrong(buffer)
            else -> _ui.update { it.copy(keyOn = false, buffer = buffer, result = SdResult.None) }
        }
    }

    private fun onLetterComplete() {
        val cur = _ui.value
        val next = cur.letterIndex + 1
        if (next < cur.target.length) {
            // More letters in the word — advance to the next one, keep the same item.
            _ui.update { it.copy(keyOn = false, buffer = "", letterIndex = next, result = SdResult.None) }
        } else {
            // Whole item complete: mark all letters done, then move on.
            _ui.update { it.copy(keyOn = false, buffer = "", letterIndex = cur.target.length, result = SdResult.Correct) }
            viewModelScope.launch { delay(900); advance() }
        }
    }

    private fun onWrong(buffer: String) {
        _ui.update { it.copy(keyOn = false, buffer = buffer, result = SdResult.Wrong) }
        // Show the miss briefly, then clear so the learner can retry this letter.
        viewModelScope.launch { delay(700); _ui.update { it.copy(buffer = "", result = SdResult.None) } }
    }

    /** Sequential move (from a correct item or Skip). Ends a random detour without moving progress. */
    private fun advance() {
        val detour = randomIndex
        if (detour != null) {
            randomIndex = null
            show(progressIndex, random = false)
        } else {
            progressIndex = (progressIndex + 1) % ITEMS.size
            viewModelScope.launch { settings.setSendingDrillIndex(progressIndex) }
            show(progressIndex, random = false)
        }
    }

    fun skip() = advance()

    /** Jump to a random item to practice; does not touch the saved progress position. */
    fun random() {
        if (ITEMS.size < 2) return
        val shown = randomIndex ?: progressIndex
        var r = shown
        while (r == shown) r = ITEMS.indices.random()
        randomIndex = r
        show(r, random = true)
    }

    /** Restart the current item from its first letter. */
    fun clear() = _ui.update { it.copy(buffer = "", result = SdResult.None, letterIndex = 0) }
}
