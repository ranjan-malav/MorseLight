package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranjan.malav.morselight_flashlightwithmorsecode.data.SettingsRepository
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.MorseCode
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.TransmitEngine
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.TransmitState
import com.ranjan.malav.morselight_flashlightwithmorsecode.torch.Sidetone
import com.ranjan.malav.morselight_flashlightwithmorsecode.torch.TorchController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val ATTENTION = "-.-.-"           // Signal prosign
private const val SOS = "...---..."             // one unbroken group

data class SendUiState(
    val message: String = "HELLO",
    val wpm: Int = 12,
    val morse: String = MorseCode.encode("HELLO"),
    val txMorse: String = "",   // what is actually being transmitted (message / Signal / SOS)
    val tx: TransmitState = TransmitState(),
    val keyTone: Boolean = true,
    val loop: Boolean = false,
) {
    val transmitting get() = tx.running
    val torchOn get() = tx.torchOn
    /** The string shown in the morse card: the transmitted morse while sending, else the message's. */
    val displayMorse get() = if (transmitting) txMorse else morse
    val symbolCount get() = morse.count { it == '.' || it == '-' }
}

class SendViewModel(
    private val torch: TorchController,
    private val settings: SettingsRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(SendUiState())
    val ui: StateFlow<SendUiState> = _ui.asStateFlow()

    private val sidetone = Sidetone()

    private val engine = TransmitEngine(
        setTorch = { on -> torch.setTorch(on); if (on) sidetone.on() else sidetone.off() },
        onState = { st -> _ui.update { it.copy(tx = st) } },
    )
    private var job: Job? = null

    init {
        viewModelScope.launch {
            settings.settings.collect { s ->
                sidetone.setEnabled(s.keyTone)
                _ui.update { it.copy(wpm = s.wpm, keyTone = s.keyTone, loop = s.loop) }
            }
        }
    }

    fun onMessageChange(text: String) =
        _ui.update { it.copy(message = text, morse = MorseCode.encode(text)) }

    fun onWpmChange(wpm: Int) {
        _ui.update { it.copy(wpm = wpm) }
        viewModelScope.launch { settings.setWpm(wpm) }
    }

    /** Momentary manual key from holding the disc. */
    fun setManualTorch(on: Boolean) {
        if (_ui.value.transmitting) return
        torch.setTorch(on)
        if (on) sidetone.on() else sidetone.off()
        _ui.update { it.copy(tx = it.tx.copy(torchOn = on)) }
    }

    fun toggleSend() {
        if (_ui.value.transmitting) { stop(); return }
        play(_ui.value.morse)
    }

    fun sendSignal() { if (!_ui.value.transmitting) play(ATTENTION) }
    fun sendSos() { if (!_ui.value.transmitting) play(SOS) }

    private fun play(morse: String) {
        if (morse.isBlank()) return
        val wpm = _ui.value.wpm
        _ui.update { it.copy(txMorse = morse) }
        job?.cancel()
        job = viewModelScope.launch {
            do {
                engine.transmit(morse, wpm)
                if (_ui.value.loop && isActive) delay(800)
            } while (_ui.value.loop && isActive)
        }
    }

    private fun stop() {
        job?.cancel()
        job = null
        torch.setTorch(false)
        _ui.update { it.copy(tx = TransmitState()) }
    }

    /** Stop any transmission — called when the Send screen leaves composition (e.g. tab change),
     *  so the torch can't keep flashing on a screen with no Stop button. */
    fun stopTransmit() {
        if (_ui.value.transmitting) stop()
    }

    override fun onCleared() {
        job?.cancel()
        torch.setTorch(false)
        sidetone.release()
    }
}
