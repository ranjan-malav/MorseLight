package com.ranjan.malav.morselight_flashlightwithmorsecode.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.LuminosityAnalyzer
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.SharedPreferenceUtils
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.doesHaveCameraPermission
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.showSettingsOpenDialog
import kotlinx.android.synthetic.main.fragment_send.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@KoinApiExtension
class SendFragment : Fragment(R.layout.fragment_send), KoinComponent {

    private val sharedPref: SharedPreferenceUtils by inject()
    private lateinit var cameraExecutor: ExecutorService
    private var cam: Camera? = null
    private var isFlashOn = false
    private var transmissionSpeed: Int = 6

    companion object {
        private const val TAG = "SendFragment"
        private const val SPEED = "speed"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(send_toolbar)
        setHasOptionsMenu(true)
        cameraExecutor = Executors.newSingleThreadExecutor()
        transmissionSpeed = sharedPref.getInt(SPEED, 6)
        speed_slider.value = transmissionSpeed.toFloat()

        speed_slider.addOnChangeListener { _, value, _ ->
            transmissionSpeed = value.toInt()
            sharedPref.setInt(SPEED, transmissionSpeed)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera(true)
            } else {
                Toast.makeText(
                    context,
                    R.string.camera_permission_no_granted,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_flash -> {
                // Check if we have the permission
                if (requireContext().doesHaveCameraPermission()) {
                    if (cam == null) {
                        startCamera(true)
                    } else {
                        if (cam!!.cameraInfo.hasFlashUnit()) {
                            isFlashOn = !isFlashOn
                            cam!!.cameraControl.enableTorch(isFlashOn)
                        }
                    }
                } else {
                    // See if we can show rationale message
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        requireContext().showSettingsOpenDialog()
                    } else {
                        requestPermissions(
                            REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                        )
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startCamera(immediatelySwitchTorch: Boolean = false) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val imageAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                            Log.d(TAG, "Average luminosity: $luma")
                        })
                    }
                cam = cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalyzer)
                cam!!.cameraInfo.torchState.observe(viewLifecycleOwner) { torchState ->
                    if (torchState == TorchState.OFF) {
                        val typedValue = TypedValue()
                        val theme = requireActivity().theme
                        theme.resolveAttribute(R.attr.colorOnBackground, typedValue, true)
                        @ColorInt val color = typedValue.data
                        flash_status_view.setColorFilter(
                            color,
                            android.graphics.PorterDuff.Mode.SRC_IN
                        )
                    } else {
                        flash_status_view.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.colorAccent),
                            android.graphics.PorterDuff.Mode.SRC_IN
                        )
                    }
                }
                if (immediatelySwitchTorch && cam!!.cameraInfo.hasFlashUnit()) {
                    isFlashOn = true
                    cam!!.cameraControl.enableTorch(true)
                }
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }
}