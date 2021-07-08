package com.ranjan.malav.morselight_flashlightwithmorsecode.fragments

import android.content.Context
import android.os.Bundle
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

typealias LumaListener = (luma: Double) -> Unit

@KoinApiExtension
class AutoDecodeFragment : Fragment(R.layout.fragment_auto_decode), KoinComponent {

    private val sharedPref: SharedPreferenceUtils by inject()
    private var isFlashOn = false
    private var ignoreClicks = false
    private var transmissionSpeed: Int = 3
    private var callback: FragmentCallbacks? = null
    private val viewModel: MainViewModel by activityViewModels()

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

        viewModel.cleanRunFlag.observe(viewLifecycleOwner, {
            runCleanUp()
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as FragmentCallbacks
        } catch (castException: ClassCastException) {
            throw ClassCastException("Context does not implement $TAG callback")
        }
    }

    private fun runCleanUp() {
        ignoreClicks = false
        sos_button.text = getString(R.string.sos)
        start_stop_button.text = getString(R.string.start)
        signal_button.isEnabled = true
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
}