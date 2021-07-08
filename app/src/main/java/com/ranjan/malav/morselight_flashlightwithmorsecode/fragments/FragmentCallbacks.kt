package com.ranjan.malav.morselight_flashlightwithmorsecode.fragments

interface FragmentCallbacks {
    fun switchTorch(torchOn: Boolean)
    fun removeHandlers()
    fun playWithFlash(
        onOffDelays: ArrayList<Long>, charUnits: ArrayList<Int>, characters: ArrayList<Char>,
        speed: Int, shouldSetCharChangeHandler: Boolean, finalOffDelay: Long
    )
}