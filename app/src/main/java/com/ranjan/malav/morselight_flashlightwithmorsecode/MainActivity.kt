package com.ranjan.malav.morselight_flashlightwithmorsecode

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ranjan.malav.morselight_flashlightwithmorsecode.fragments.FragmentCallbacks
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.LuminosityAnalyzer
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.showSettingsOpenDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.core.component.KoinApiExtension
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@KoinApiExtension
class MainActivity : AppCompatActivity(R.layout.activity_main), FragmentCallbacks {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var controller: NavController
    private var navListener = NavController.OnDestinationChangedListener { _, _, _ ->
        removeHandlers()
    }
    private var cam: Camera? = null
    private var isFlashOn = false
    private var ignoreClicks = false
    private val viewModel: MainViewModel by viewModels()

    private val handler = Handler(Looper.getMainLooper())
    private val handler2 = Handler(Looper.getMainLooper())
    private val handler3 = Handler(Looper.getMainLooper())

    private val offRunnable = Runnable {
        cam?.let { switchFlashOff(it) }
    }

    private val onRunnable = Runnable {
        cam?.let { switchFlashOn(it) }
    }

    private val cleanUpRunnable = Runnable {
        ignoreClicks = false
        cam?.let { switchFlashOff(it) }
        invalidateOptionsMenu()
        viewModel.runCleanUps()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQ_CODE_W_IMMEDIATE_ACTION = 10
        private const val REQ_CODE_WO_IMMEDIATE_ACTION = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        controller = navHostFragment.navController
        AppBarConfiguration(
            setOf(
                R.id.navigation_send, R.id.navigation_receive, R.id.navigation_learn,
            )
        )
        navView.setupWithNavController(controller)

        setSupportActionBar(toolbar)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Start camera if we have the permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (allPermissionsGranted()) {
                startCamera(false)
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    showSettingsOpenDialog()
                } else {
                    requestPermissions(
                        REQUIRED_PERMISSIONS, REQ_CODE_WO_IMMEDIATE_ACTION
                    )
                }
            }
        } else {
            startCamera(false)
        }
    }

    override fun onResume() {
        super.onResume()
        controller.addOnDestinationChangedListener(navListener)
    }

    override fun onPause() {
        controller.removeOnDestinationChangedListener(navListener)
        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CODE_W_IMMEDIATE_ACTION
            || requestCode == REQ_CODE_WO_IMMEDIATE_ACTION
        ) {
            if (allPermissionsGranted()) {
                startCamera(requestCode == REQ_CODE_W_IMMEDIATE_ACTION)
            } else {
                Toast.makeText(
                    this,
                    R.string.camera_permission_no_granted,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    // Do not call this before checking permissions and getting camera initiated
    // We don't wanna perform those operations here because it messes up the timings then.
    private fun switchFlashOn(cam: Camera) {
        if (cam.cameraInfo.hasFlashUnit()) {
            Log.d(TAG, "Switch flash on")
            cam.cameraControl.enableTorch(true)
            isFlashOn = true
            viewModel.updateFlashStatus(true)
        }
    }

    private fun switchFlashOff(cam: Camera) {
        if (cam.cameraInfo.hasFlashUnit()) {
            Log.d(TAG, "Switch flash off")
            cam.cameraControl.enableTorch(false)
            isFlashOn = false
            viewModel.updateFlashStatus(false)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.action_flash)
        if (ignoreClicks) {
            item.isEnabled = false
            item.icon.alpha = 130
        } else {
            item.isEnabled = true
            item.icon.alpha = 255
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_flash -> {
                if (ignoreClicks) return false
                removeHandlerCallbacks()
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
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

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
                    switchFlashOn(cam!!)
                }
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
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

    override fun playWithFlash(
        onOffDelays: ArrayList<Long>,
        charUnits: ArrayList<Int>,
        characters: ArrayList<Char>,
        speed: Int, shouldSetCharChangeHandler: Boolean, finalOffDelay: Long
    ) {
        // Setup, remove click listeners
        removeHandlerCallbacks()
        ignoreClicks = true
        isFlashOn = false
        invalidateOptionsMenu()

        // Speed can be from 1 to 10, 3 means 1 unit = 3/3 sec, 10 means 1 unit = 3/10 sec
        // 1 means 1 unit = 3/1 sec. Default speed is 3 which means 1 sec = 1 unit.
        val transmissionSpeed: Float = 3f / speed
        for (i in onOffDelays.indices) {
            if (!isFlashOn) {
                Log.d(TAG, "Delay on: ${onOffDelays[i]}")
                isFlashOn = true
                handler.postDelayed(
                    onRunnable,
                    onOffDelays[i]
                )
            } else {
                isFlashOn = false
                Log.d(TAG, "Delay off: ${onOffDelays[i]}")
                handler2.postDelayed(
                    offRunnable,
                    onOffDelays[i]
                )
            }
        }
        handler2.postDelayed(cleanUpRunnable, finalOffDelay)
        isFlashOn = false

        if (shouldSetCharChangeHandler) {
            var charDelay = 0
            for (i in charUnits.indices) {
                val unit = charUnits[i]
                handler3.postDelayed(
                    CharSetRunnable(characters[i]),
                    (charDelay * 1000 * transmissionSpeed).toLong()
                )
                charDelay += unit
            }
        }
    }

    inner class CharSetRunnable(private val char: Char) : Runnable {
        override fun run() {
            viewModel.updateCharacter(char)
        }
    }

    override fun switchTorch(torchOn: Boolean) {
        if (torchOn) {
            cam?.let { switchFlashOn(it) }
        } else {
            cam?.let { switchFlashOff(it) }
        }
    }

    override fun removeHandlers() {
        removeHandlerCallbacks()
        cleanUpRunnable.run()
    }
}