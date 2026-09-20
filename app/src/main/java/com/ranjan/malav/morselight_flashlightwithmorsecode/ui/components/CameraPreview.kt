package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ranjan.malav.morselight_flashlightwithmorsecode.torch.TorchController

/**
 * Live back-camera preview for the Receive-camera screen. Binds the preview on enter and restores
 * the torch-only binding on leave, so the shared TorchController stays consistent across screens.
 */
@Composable
fun CameraPreview(torch: TorchController, modifier: Modifier = Modifier) {
    val owner = LocalLifecycleOwner.current
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                torch.bindPreview(owner, this)
            }
        },
    )
    DisposableEffect(Unit) {
        onDispose { torch.restoreTorchOnly() }
    }
}
