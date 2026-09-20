package com.ranjan.malav.morselight_flashlightwithmorsecode.torch

import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.LuminosityAnalyzer
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Owns the CameraX back-camera binding and drives the flashlight torch. Also surfaces a
 * mean-luminance stream (for camera decoding). Extracted from the old MainActivity so both the
 * Send (torch) and Receive-camera (luminance) Compose screens can share one binding.
 */
class TorchController(private val context: Context) {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var provider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var analyzer: ImageAnalysis? = null

    private val _luminosity = MutableSharedFlow<Double>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val luminosity: SharedFlow<Double> = _luminosity

    private val lumaAnalyzer = LuminosityAnalyzer(
        listener = { luma -> _luminosity.tryEmit(luma) },
        considerableArea = 50,
    )

    private var lifecycleOwner: LifecycleOwner? = null

    /** Bind the back camera (torch + luminance only). */
    fun bind(owner: LifecycleOwner, onReady: () -> Unit = {}) =
        withProvider(owner) { p, analysis ->
            camera = p.bindToLifecycle(owner, CameraSelector.DEFAULT_BACK_CAMERA, analysis)
            onReady()
        }

    /** Bind the back camera with a live preview surface (Receive-camera screen). */
    fun bindPreview(owner: LifecycleOwner, previewView: PreviewView) =
        withProvider(owner) { p, analysis ->
            val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
            camera = p.bindToLifecycle(owner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
        }

    private fun withProvider(owner: LifecycleOwner, bind: (ProcessCameraProvider, ImageAnalysis) -> Unit) {
        lifecycleOwner = owner
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            val p = future.get()
            provider = p
            p.unbindAll()
            val analysis = analyzer ?: ImageAnalysis.Builder().build().also {
                it.setAnalyzer(executor, lumaAnalyzer)
            }.also { analyzer = it }
            bind(p, analysis)
        }, ContextCompat.getMainExecutor(context))
    }

    /** Restore the torch-only binding (called when the camera preview leaves composition). */
    fun restoreTorchOnly() {
        lifecycleOwner?.let { bind(it) }
    }

    fun hasFlash(): Boolean = camera?.cameraInfo?.hasFlashUnit() == true

    fun setTorch(on: Boolean) {
        camera?.let { if (it.cameraInfo.hasFlashUnit()) it.cameraControl.enableTorch(on) }
    }

    fun setDetectionArea(percentage: Int) = lumaAnalyzer.updateConsiderableArea(percentage)

    fun unbind() {
        provider?.unbindAll()
        camera = null
    }

    fun shutdown() {
        unbind()
        executor.shutdown()
    }
}
