package com.ranjan.malav.morselight_flashlightwithmorsecode

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
    }

    private fun bindTorch() {
        container.torch.bind(this)
    }

    override fun onDestroy() {
        container.torch.unbind()
        super.onDestroy()
    }
}
