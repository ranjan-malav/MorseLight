package com.ranjan.malav.morselight_flashlightwithmorsecode.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ranjan.malav.morselight_flashlightwithmorsecode.MainViewModel
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.*
import kotlinx.android.synthetic.main.fragment_auto_decode.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

typealias LumaListener = (luma: Double) -> Unit

@KoinApiExtension
class AutoDecodeFragment : Fragment(R.layout.fragment_auto_decode), KoinComponent,
    ImageAnalysisListener {

    private val sharedPref: SharedPreferenceUtils by inject()
    private var isFlashOn = false
    private var ignoreClicks = false
    private var transmissionSpeed: Int = 3
    private var callback: FragmentCallbacks? = null
    private var startCapturing = false
    private var stopCapturingLowLuminosity = false
    private var avgLowLuminosity = 0.0
    private var avgHighLuminosity = 0.0
    private var avgCounter = 0
    private val handler = Handler(Looper.getMainLooper())
    private val viewModel: MainViewModel by activityViewModels()
    private val timings = arrayListOf<Long>()
    private val diffTimings = arrayListOf<Long>()

    companion object {
        private const val TAG = "AutoDecode"
        private const val SPEED = "speed"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transmissionSpeed = sharedPref.getInt(SPEED, 3)

        start_stop_button.setOnClickListener {
            if (ignoreClicks) {
                runCleanUp()
                callback?.removeHandlers()
            } else {
                if (!startCapturing) {
                    // Start capturing high luminosity to on flash timings and lows to off flash timings
                    startCapturing = true
                    start_stop_button.text = getString(R.string.stop)
                    sos_button.isEnabled = false
                    signal_button.isEnabled = false
                    handler.postDelayed(timer3Sec, 0)
                    handler.postDelayed(timer2Sec, 1000)
                    handler.postDelayed(timer1Sec, 2000)
                    handler.postDelayed(timer0Sec, 2950)
                } else {
                    startCapturing = false
                    stopCapturingLowLuminosity = false
                    sos_button.isEnabled = true
                    signal_button.isEnabled = true
                    start_stop_button.text = getString(R.string.start)
                    isFlashOn = false
                    flash_status_view.gone()
                    avgLowLuminosity = 0.0
                    avgHighLuminosity = 0.0
                    avgCounter = 0
                    decodeNotedTimings()
                    removeHandlerCallbacks()
                }
            }
        }

        signal_button.setOnClickListener {
            if (ignoreClicks) return@setOnClickListener
            val charMessage = arrayListOf('E', 'E', 'E')
            playWithFlash(charMessage, 20)
        }

        sos_button.setOnClickListener {
            if (ignoreClicks) {
                runCleanUp()
                callback?.removeHandlers()
            } else {
                val charMessage = arrayListOf('S', 'O', 'S')
                playWithFlash(charMessage, transmissionSpeed)
            }
        }

        reset_button.setOnClickListener {
            runCleanUp()
        }

        viewModel.cleanRunFlag.observe(viewLifecycleOwner, {
            runCleanUp()
        })

        callback?.bindPreview(camera_preview, this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as FragmentCallbacks
        } catch (castException: ClassCastException) {
            throw ClassCastException("Context does not implement $TAG callback")
        }
    }

    private fun decodeNotedTimings() {
        if (timings.isNotEmpty()) {
            val onTimings = arrayListOf<Long>()
            val offTimings = arrayListOf<Long>()
            for (i in diffTimings.indices) {
                if (i % 2 == 0) {
                    onTimings.add(diffTimings[i])
                } else {
                    offTimings.add(diffTimings[i])
                }
            }
            val onTimingsString = StringBuilder()
            onTimings.forEach {
                onTimingsString.append(it).append(" * ")
            }
            Log.d(TAG, "On timing Diffs: $onTimingsString")
            val onDecodedMap = DecoderUtils.findNaturalBreakOnTimings(onTimings)
            val smallOnTimings = onDecodedMap[DecoderUtils.SMALL_UNITS]
            val bigOnTimings = onDecodedMap[DecoderUtils.BIG_UNITS]

            val offTimingsString = StringBuilder()
            offTimings.forEach {
                offTimingsString.append(it).append(" * ")
            }
            Log.d(TAG, "Off timing Diffs: $offTimingsString")
            val offDecodedMap = DecoderUtils.findNaturalBreakOffTimings(offTimings)
            val mediumOffTimings = offDecodedMap[DecoderUtils.MEDIUM_UNITS]
            val bigOffTimings = offDecodedMap[DecoderUtils.BIG_UNITS]

            val message = StringBuilder()
            timings.forEachIndexed { index, _ ->
                if (index == 0) return@forEachIndexed
                val diff = timings[index] - timings[index - 1]
                when {
                    smallOnTimings!!.contains(diff) -> {
                        message.append(".")
                    }
                    bigOnTimings!!.contains(diff) -> {
                        message.append("-")
                    }
                    mediumOffTimings!!.contains(diff) -> {
                        message.append(" ")
                    }
                    bigOffTimings!!.contains(diff) -> {
                        message.append(" / ")
                    }
                }
            }
            incoming_message.text = message.toString()
            decoded_message.text = DecoderUtils.decryptMorse(message.toString())
            timings.clear()
            diffTimings.clear()
        }
    }

    private fun removeHandlerCallbacks() {
        try {
            handler.removeCallbacksAndMessages(
                null
            )
        } catch (npe: NullPointerException) {
            Log.d(TAG, "Error: ${npe.localizedMessage}")
        }
    }

    private fun runCleanUp() {
        ignoreClicks = false
        sos_button.text = getString(R.string.sos)
        start_stop_button.text = getString(R.string.start)
        signal_button.isEnabled = true
        timings.clear()
        diffTimings.clear()
        incoming_message.text = ""
        decoded_message.text = ""
        avgLowLuminosity = 0.0
        avgHighLuminosity = 0.0
        avgCounter = 0
    }

    private fun playWithFlash(charMessage: ArrayList<Char>, speed: Int) {
        // Setup, remove click listeners
        ignoreClicks = true
        sos_button.text = getString(R.string.stop)
        start_stop_button.text = getString(R.string.stop)
        signal_button.isEnabled = false

        // Speed can be from 1 to 10, 3 means 1 unit = 3/3 sec, 10 means 1 unit = 3/10 sec
        // 1 means 1 unit = 3/1 sec. Default speed is 3 which means 1 sec = 1 unit.
        val transmissionSpeed: Float = 3f / speed
        val timeUnits = StringBuilder()
        val morseCode = StringBuilder()
        val charUnits = arrayListOf<Int>()
        val characters = arrayListOf<Char>()
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

        var delay = 0L
        val onOffDelays = arrayListOf<Long>()
        for (i in timeUnits.indices) {
            onOffDelays.add((delay * 1000 * transmissionSpeed).toLong())
            val unit = timeUnits[i].toString().toInt()
            delay += unit
        }

        isFlashOn = false
        callback?.playWithFlash(
            onOffDelays, charUnits, characters, speed,
            false, (delay * 1000 * transmissionSpeed).toLong()
        )
    }

    private val timer3Sec = Runnable {
        start_timer.text = "3"
    }
    private val timer2Sec = Runnable {
        start_timer.text = "2"
    }
    private val timer1Sec = Runnable {
        start_timer.text = "1"
    }
    private val timer0Sec = Runnable {
        start_timer.text = ""
        stopCapturingLowLuminosity = true
    }

    override fun listenLuminosity(luminosity: Double) {
        if (startCapturing) {
            if (!stopCapturingLowLuminosity) {
                avgLowLuminosity = (avgLowLuminosity * avgCounter + luminosity) / (avgCounter + 1)
                avgCounter++
            } else {
                avgCounter = 0
                if (luminosity > avgLowLuminosity * 5 && !isFlashOn) {
                    activity?.let {
                        it.runOnUiThread { flash_status_view.visible() }
                    }
                    isFlashOn = true
                    avgHighLuminosity =
                        (avgHighLuminosity * avgCounter + luminosity) / (avgCounter + 1)
                    avgCounter++
                    timings.add(System.currentTimeMillis())
                    if (timings.size > 1) {
                        diffTimings.add(timings[timings.size - 1] - timings[timings.size - 2])
                    }
                } else if (luminosity * 5 < avgHighLuminosity && isFlashOn) {
                    activity?.let {
                        it.runOnUiThread { flash_status_view.gone() }
                    }
                    isFlashOn = false
                    timings.add(System.currentTimeMillis())
                    if (timings.size > 1) {
                        diffTimings.add(timings[timings.size - 1] - timings[timings.size - 2])
                    }
                }
            }
        }
    }
}