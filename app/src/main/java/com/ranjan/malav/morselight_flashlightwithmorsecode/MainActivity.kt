package com.ranjan.malav.morselight_flashlightwithmorsecode

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.ranjan.malav.morselight_flashlightwithmorsecode.app.MorseLightApp
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.MorseApp
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseLightTheme

class MainActivity : ComponentActivity() {

    private val container by lazy { (application as MorseLightApp).container }

    private val requestCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) bindTorch()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            bindTorch()
        } else {
            requestCamera.launch(Manifest.permission.CAMERA)
        }

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

    private fun bindTorch() {
        container.torch.bind(this)
    }

    override fun onDestroy() {
        container.torch.unbind()
        super.onDestroy()
    }
}
