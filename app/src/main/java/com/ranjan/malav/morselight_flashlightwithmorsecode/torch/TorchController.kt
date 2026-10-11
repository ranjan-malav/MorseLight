package com.ranjan.malav.morselight_flashlightwithmorsecode.torch

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
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
 * Drives the flashlight and, separately, the camera-decode luminance feed.
 *
 * The torch runs through [CameraManager.setTorchMode], which needs **no CAMERA permission** — so the
 * Send screen works immediately at launch with no prompt. The live preview + mean-luminance analysis
 * (Receive-camera only) use CameraX and *do* need CAMERA, so they are bound lazily via [bindPreview]
 * once that tab has permission, and released with [unbindCamera] on leave.
 */
class TorchController(context: Context) {

    private val appContext = context.applicationContext
    private val cameraManager = appContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val torchCameraId: String? = findTorchCamera()

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var provider: ProcessCameraProvider? = null
    private var analyzer: ImageAnalysis? = null

    private val _luminosity = MutableSharedFlow<Double>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val luminosity: SharedFlow<Double> = _luminosity

    private val lumaAnalyzer = LuminosityAnalyzer(
        listener = { luma -> _luminosity.tryEmit(luma) },
        considerableArea = 50,
    )

    private fun findTorchCamera(): String? = runCatching {
        val ids = cameraManager.cameraIdList
        ids.firstOrNull { id ->
            val ch = cameraManager.getCameraCharacteristics(id)
            ch.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true &&
                ch.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
        } ?: ids.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }.getOrNull()

    fun hasFlash(): Boolean = torchCameraId != null

    /** Toggle the flashlight. Needs no CAMERA permission. No-op if the device has no flash. */
    fun setTorch(on: Boolean) {
        val id = torchCameraId ?: return
        runCatching { cameraManager.setTorchMode(id, on) }
    }

    /** Bind a live preview + luminance analysis for the Receive-camera screen (needs CAMERA). */
    fun bindPreview(owner: LifecycleOwner, previewView: PreviewView) {
        val future = ProcessCameraProvider.getInstance(appContext)
        future.addListener({
            val p = future.get()
            provider = p
            p.unbindAll()
            val analysis = analyzer ?: ImageAnalysis.Builder().build().also {
                it.setAnalyzer(executor, lumaAnalyzer)
            }.also { analyzer = it }
            val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
            p.bindToLifecycle(owner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
        }, ContextCompat.getMainExecutor(appContext))
    }

    /** Release the camera feed when the camera screen leaves; the torch is unaffected. */
    fun unbindCamera() { provider?.unbindAll() }

    fun setDetectionArea(percentage: Int) = lumaAnalyzer.updateConsiderableArea(percentage)

    /** Called when the host Activity is destroyed: torch off + release the camera. */
    fun release() {
        setTorch(false)
        provider?.unbindAll()
    }

    fun shutdown() {
        release()
        executor.shutdown()
    }
}
