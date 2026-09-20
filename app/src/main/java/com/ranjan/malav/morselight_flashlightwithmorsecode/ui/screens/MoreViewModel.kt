package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranjan.malav.morselight_flashlightwithmorsecode.data.Settings
import com.ranjan.malav.morselight_flashlightwithmorsecode.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MoreViewModel(private val settings: SettingsRepository) : ViewModel() {
    val state: StateFlow<Settings> =
        settings.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Settings())

    fun setKeyTone(v: Boolean) = viewModelScope.launch { settings.setKeyTone(v) }.let {}
    fun setLoop(v: Boolean) = viewModelScope.launch { settings.setLoop(v) }.let {}
    fun setKeepAwake(v: Boolean) = viewModelScope.launch { settings.setKeepAwake(v) }.let {}
}
