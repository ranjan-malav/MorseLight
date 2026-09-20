package com.ranjan.malav.morselight_flashlightwithmorsecode.app

import android.content.Context
import com.ranjan.malav.morselight_flashlightwithmorsecode.data.SettingsRepository
import com.ranjan.malav.morselight_flashlightwithmorsecode.torch.TorchController

/** Tiny manual DI container (replaces Koin). One TorchController + SettingsRepository per process. */
class AppContainer(context: Context) {
    val settings = SettingsRepository(context)
    val torch = TorchController(context.applicationContext)
}
