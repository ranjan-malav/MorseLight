package com.ranjan.malav.morselight_flashlightwithmorsecode

import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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
        setContent {
            MorseLightTheme {
                MorseApp(container)
            }
        }

        // Honour the "Keep screen awake" preference.
        lifecycleScope.launch {
            container.settings.settings.map { it.keepAwake }.distinctUntilChanged().collect { awake ->
                if (awake) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    override fun onDestroy() {
        container.torch.release()
        super.onDestroy()
    }
}
