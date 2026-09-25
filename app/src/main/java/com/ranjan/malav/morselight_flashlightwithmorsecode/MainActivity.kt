package com.ranjan.malav.morselight_flashlightwithmorsecode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ranjan.malav.morselight_flashlightwithmorsecode.app.MorseLightApp
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.MorseApp
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseLightTheme

class MainActivity : ComponentActivity() {

    private val container by lazy { (application as MorseLightApp).container }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // No camera permission is requested here: the torch runs through CameraManager (permission
        // free). CAMERA is asked for only when the Receive-camera tab is opened (see ReceiveScreen).
        // "Keep screen awake" is applied per-screen (Send while transmitting, Receive throughout)
        // via KeepScreenOn — not app-wide — so the screen isn't held on while idle on other tabs.
        setContent {
            MorseLightTheme {
                MorseApp(container)
            }
        }
    }

    override fun onDestroy() {
        container.torch.release()
        super.onDestroy()
    }
}
