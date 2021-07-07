package com.ranjan.malav.morselight_flashlightwithmorsecode.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.*
import kotlinx.android.synthetic.main.fragment_send.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@KoinApiExtension
class SendFragment : Fragment(R.layout.fragment_send), KoinComponent {

    private val sharedPref: SharedPreferenceUtils by inject()
    private lateinit var cameraExecutor: ExecutorService
    private var cam: Camera? = null
    private var isFlashOn = false
    private var ignoreClicks = false
    private var transmissionSpeed: Int = 3

    private val handler = Handler()
    private val handler2 = Handler()
    private val handler3 = Handler()

    private val offRunnable = Runnable {
        cam?.let { switchFlashOff(it) }
    }

    private val onRunnable = Runnable {
        cam?.let { switchFlashOn(it) }
    }

    private val cleanUpRunnable = Runnable {
        ignoreClicks = false
        cam?.let { switchFlashOff(it) }
        start_stop_button.text = getString(R.string.start)
        sos_button.isEnabled = true
        signal_button.isEnabled = true
        current_char.text = ""
        current_char_morse.text = ""
        activity?.invalidateOptionsMenu()
    }

    companion object {
        private const val TAG = "SendFragment"
        private const val SPEED = "speed"
        private const val REQ_CODE_W_IMMEDIATE_ACTION = 10
        private const val REQ_CODE_WO_IMMEDIATE_ACTION = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(send_toolbar)
        setHasOptionsMenu(true)
        cameraExecutor = Executors.newSingleThreadExecutor()
        transmissionSpeed = sharedPref.getInt(SPEED, 3)
        speed_slider.value = transmissionSpeed.toFloat()

        // Start camera if we have the permission
        if (allPermissionsGranted()) {
            startCamera(false)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                requireContext().showSettingsOpenDialog()
            } else {
                requestPermissions(
                    REQUIRED_PERMISSIONS, REQ_CODE_WO_IMMEDIATE_ACTION
                )
            }
        }

        speed_slider.addOnChangeListener { _, value, _ ->
            removeHandlerCallbacks()
            transmissionSpeed = value.toInt()
            sharedPref.setInt(SPEED, transmissionSpeed)
            ignoreClicks = false
        }

        flash_status_view.setOnTouchListener { _, event ->
            if (ignoreClicks) return@setOnTouchListener false
            removeHandlerCallbacks()
            cam?.let {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    switchFlashOn(it)
                }
                if (event.action == MotionEvent.ACTION_UP) {
                    switchFlashOff(it)
                }
            }
            return@setOnTouchListener true
        }

        sos_button.setOnClickListener {
            if (ignoreClicks) return@setOnClickListener
            removeHandlerCallbacks()
            val charMessage = arrayListOf('S', 'O', 'S')
            playWithFlash(charMessage, transmissionSpeed, "SOS", true)
        }

        signal_button.setOnClickListener {
            if (ignoreClicks) return@setOnClickListener
            removeHandlerCallbacks()
            val charMessage = arrayListOf('E', 'E', 'E')
            playWithFlash(charMessage, 3, "EEE", false)
        }

        start_stop_button.setOnClickListener {
            if (ignoreClicks) {
                removeHandlerCallbacks()
                cleanUpRunnable.run()
            } else {
                val charMessage = message_input.editText?.text.toString().trim()
                if (charMessage.isNullOrBlank()) {
                    Toast.makeText(
                        requireContext(), R.string.no_message_to_transmit, Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                val charArray = arrayListOf<Char>()
                charMessage.toCharArray().forEach {
                    charArray.add(it)
                }
                playWithFlash(
                    charArray, transmissionSpeed, charMessage, true
                )
            }
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
        if (requestCode == REQ_CODE_W_IMMEDIATE_ACTION
            || requestCode == REQ_CODE_WO_IMMEDIATE_ACTION
        ) {
            if (allPermissionsGranted()) {
                startCamera(requestCode == REQ_CODE_W_IMMEDIATE_ACTION)
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

    // Do not call this before checking permissions and getting camera initiated
    // We don't wanna perform those operations here because it messes up the timings then.
    private fun switchFlashOn(cam: Camera) {
        if (cam.cameraInfo.hasFlashUnit()) {
             Log.d(TAG, "Switch flash on")
            cam.cameraControl.enableTorch(true)
            isFlashOn = true
            setTorchOnImageView()
        }
    }

    private fun switchFlashOff(cam: Camera) {
        if (cam.cameraInfo.hasFlashUnit()) {
             Log.d(TAG, "Switch flash off")
            cam.cameraControl.enableTorch(false)
            isFlashOn = false
            setTorchOffImageView()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.action_flash)
        if (ignoreClicks) {
            item.isEnabled = false
            item.icon.alpha = 130
        } else {
            item.isEnabled = true
            item.icon.alpha = 255
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_flash -> {
                if (ignoreClicks) return false
                cam?.let {
                    if (isFlashOn) {
                        switchFlashOff(it)
                    } else {
                        switchFlashOn(it)
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
                            // Log.d(TAG, "Average luminosity: $luma")
                        })
                    }
                cam = cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalyzer)
                if (immediatelySwitchTorch && cam!!.cameraInfo.hasFlashUnit()) {
                    isFlashOn = true
                    // Log.d(TAG, "Switch flash on")
                    cam!!.cameraControl.enableTorch(true)
                    setTorchOnImageView()
                }
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun setTorchOffImageView() {
        val typedValue = TypedValue()
        val theme = requireActivity().theme
        theme.resolveAttribute(R.attr.colorOnBackground, typedValue, true)
        @ColorInt val color = typedValue.data
        flash_status_text.text = getString(R.string.off)
        flash_status_view.setColorFilter(
            color,
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    private fun setTorchOnImageView() {
        flash_status_text.text = getString(R.string.on)
        flash_status_view.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.colorAccent),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    private fun removeHandlerCallbacks() {
        try {
            handler.removeCallbacksAndMessages(
                null
            )
            handler2.removeCallbacksAndMessages(
                null
            )
            handler3.removeCallbacksAndMessages(
                null
            )
        } catch (npe: NullPointerException) {
            Log.d(TAG, "Error: ${npe.localizedMessage}")
        }
    }

    private fun playWithFlash(
        charMessage: ArrayList<Char>, speed: Int, message: String, shouldUpdateCurrentChar: Boolean
    ) {
        // Setup, remove click listeners
        ignoreClicks = true
        start_stop_button.text = getString(R.string.stop)
        sos_button.isEnabled = false
        signal_button.isEnabled = false
        activity?.invalidateOptionsMenu()

        // Speed can be from 1 to 10, 3 means 1 unit = 3/3 sec, 10 means 1 unit = 3/10 sec
        // 1 means 1 unit = 3/1 sec. Default speed is 3 which means 1 sec = 1 unit.
        val transmissionSpeed: Float = 3f / speed
        val timeUnits = StringBuilder()
        val morseCode = StringBuilder()
        val charUnits = arrayListOf<Int>()
        // Add character morse timings to string builder
        for (char in charMessage) {
            timeUnits.append(charToUnits[char])
            morseCode.append(charToMorse[char])
            if (charUnits.isNotEmpty()) {
                charUnits.add(charToTotalUnits[char]!! + 3)
            } else {
                charUnits.add(charToTotalUnits[char]!!)
            }
        }
        // Remove last character because we have added 3 units for space after every character
        timeUnits.replace(timeUnits.length - 1, timeUnits.length, "")
        var delay = 0
        encoded_morse_code.text = morseCode.toString()
        message_input.editText?.setText(message)
        for (i in timeUnits.indices) {
            val unit = timeUnits[i].toString().toInt()
            if (!isFlashOn) {
                Log.d(TAG, "Delay on: ${(delay * 1000 * transmissionSpeed).toLong()}")
                isFlashOn = true
                handler.postDelayed(
                    onRunnable,
                    (delay * 1000 * transmissionSpeed).toLong()
                )
            } else {
                isFlashOn = false
                Log.d(TAG, "Delay off: ${(delay * 1000 * transmissionSpeed).toLong()}")
                handler2.postDelayed(
                    offRunnable,
                    (delay * 1000 * transmissionSpeed).toLong()
                )
            }
            delay += unit
        }

        var charDelay = 0
        for (i in charUnits.indices) {
            val unit = charUnits[i].toString().toInt()
            if (shouldUpdateCurrentChar) {
                handler3.postDelayed(
                    CharSetRunnable(charMessage[i]),
                    (charDelay * 1000 * transmissionSpeed).toLong()
                )
            }
            charDelay += unit
        }
        handler2.postDelayed(
            cleanUpRunnable, (delay * 1000 * transmissionSpeed).toLong()
        )
        isFlashOn = false
    }

    inner class CharSetRunnable(private val char: Char) : Runnable {
        override fun run() {
            current_char.text = "$char = "
            current_char_morse.text = charToMorse[char]
        }
    }
}