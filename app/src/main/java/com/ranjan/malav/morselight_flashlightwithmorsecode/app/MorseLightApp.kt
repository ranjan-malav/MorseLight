package com.ranjan.malav.morselight_flashlightwithmorsecode.app

import android.app.Application
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.SharedPreferenceUtils
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MorseLightApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MorseLightApp)
            modules(
                appModule
            )
        }
    }
}

val appModule = module {
    single { SharedPreferenceUtils(androidContext()) }
}