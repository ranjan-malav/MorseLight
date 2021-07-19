package com.ranjan.malav.morselight_flashlightwithmorsecode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MainViewModel : ViewModel() {
    private val _cleanRunFlag = MutableLiveData<Boolean>()
    val cleanRunFlag: LiveData<Boolean> = _cleanRunFlag

    private val _isFlashOn = MutableLiveData<Boolean>()
    val isFlashOn: LiveData<Boolean> = _isFlashOn

    private val _currentlyTransmittingChar = MutableLiveData<Char>()
    val currentlyTransmittingChar: LiveData<Char> = _currentlyTransmittingChar

    fun updateFlashStatus(isFlashOn: Boolean) {
        _isFlashOn.apply { value = isFlashOn }
    }

    fun updateCharacter(char: Char) {
        _currentlyTransmittingChar.apply { value = char }
    }

    fun runCleanUps() {
        val newFlag = if (_cleanRunFlag.value != null) !_cleanRunFlag.value!! else true
        _cleanRunFlag.apply { value = newFlag }
    }
}