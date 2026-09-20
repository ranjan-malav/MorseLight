package com.ranjan.malav.morselight_flashlightwithmorsecode.app

import android.app.Application

class MorseLightApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
