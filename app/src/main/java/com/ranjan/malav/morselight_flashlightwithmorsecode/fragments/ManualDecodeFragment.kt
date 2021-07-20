package com.ranjan.malav.morselight_flashlightwithmorsecode.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ranjan.malav.morselight_flashlightwithmorsecode.MainViewModel
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.*
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.DecoderUtils.decryptMorse
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.DecoderUtils.findMorseFromTimings
import kotlinx.android.synthetic.main.fragment_manual_decode.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


@KoinApiExtension
class ManualDecodeFragment : Fragment(R.layout.fragment_manual_decode), KoinComponent {

    private val sharedPref: SharedPreferenceUtils by inject()
    private var isFlashOn = false
    private var ignoreClicks = false
    private var transmissionSpeed: Int = 3
    private var callback: FragmentCallbacks? = null
    private val viewModel: MainViewModel by activityViewModels()
    private val timings = arrayListOf<Long>()
    private val diffTimings = arrayListOf<Long>()

    companion object {
        private const val TAG = "ManualDecode"
        private const val SPEED = "speed"
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transmissionSpeed = sharedPref.getInt(SPEED, 3)

        tap_and_hold_button.setOnTouchListener { _, event ->
            if (ignoreClicks) {
                runCleanUp()
                callback?.removeHandlers()
            } else {
                report_button.gone()
                if (event.action == MotionEvent.ACTION_DOWN
                    || event.action == MotionEvent.ACTION_UP
                ) {
                    timings.add(System.currentTimeMillis())
                    if (timings.size > 1) {
                        diffTimings.add(timings[timings.size - 1] - timings[timings.size - 2])
                    }
                    updateTimingViews()
                }
            }
            return@setOnTouchListener true
        }

        incoming_message.movementMethod = ScrollingMovementMethod()

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
            timings.clear()
            diffTimings.clear()
            report_button.gone()
            incoming_message.text = ""
            decoded_message.text = ""
        }

        decode_button.setOnClickListener {
            val timingsCopy = arrayListOf<Long>()
            timingsCopy.addAll(timings)
            val morseMessage = findMorseFromTimings(timings, diffTimings)
            if (morseMessage.isNotBlank()) {
                val decryptedMessage = if (!morseMessage.contains("-")) {
                    // All the units are of same size, it could be . or -
                    val dashedMessage = morseMessage.replace(".", "-")
                    incoming_message.text = getString(
                        R.string.dot_message_or_dash_message, morseMessage, dashedMessage
                    )
                    getString(
                        R.string.dot_message_or_dash_message,
                        decryptMorse(morseMessage), decryptMorse(dashedMessage)
                    )
                } else {
                    incoming_message.text = morseMessage
                    decryptMorse(morseMessage)
                }
                incoming_message.text = morseMessage
                decoded_message.text = decryptedMessage
                report_button.visible()
                report_button.setOnClickListener {
                    activity?.askIfDecryptedCorrectly(decryptedMessage, timingsCopy)
                }
            }
        }

        viewModel.isFlashOn.observe(viewLifecycleOwner, {
            if (it) {
                setTorchOnImageView()
            } else {
                setTorchOffImageView()
            }
        })

        viewModel.cleanRunFlag.observe(viewLifecycleOwner, {
            runCleanUp()
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as FragmentCallbacks
            callback?.setCurrentFragment("Manual")
        } catch (castException: ClassCastException) {
            throw ClassCastException("Context does not implement $TAG callback")
        }
    }

    private fun updateTimingViews() {
        val sb = StringBuilder()
        if (timings.size > 1) {
            timings.forEachIndexed { index, _ ->
                if (index == 0) return@forEachIndexed
                val diff = timings[index] - timings[index - 1]
                if (index % 2 == 0) {
                    sb.append("${String.format("%.1f", (diff / 1000f))}s(off)  ")
                } else {
                    sb.append("${String.format("%.1f", (diff / 1000f))}s(on)  ")
                }
            }
        }
        if (timings.size == 1) {
            decoded_message.text = ""
        }
        incoming_message.text = sb.toString().trim()
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

    private fun runCleanUp() {
        ignoreClicks = false
        report_button.gone()
        sos_button.text = getString(R.string.sos)
        tap_and_hold_button.text = getString(R.string.press_hold)
        signal_button.isEnabled = true
    }

    private fun playWithFlash(charMessage: ArrayList<Char>, speed: Int) {
        // Setup, remove click listeners
        ignoreClicks = true
        report_button.gone()
        sos_button.text = getString(R.string.stop)
        tap_and_hold_button.text = getString(R.string.stop)
        signal_button.isEnabled = false

        // Speed can be from 1 to 10, 3 means 1 unit = 3/3 sec, 10 means 1 unit = 3/10 sec
        // 1 means 1 unit = 3/1 sec. Default speed is 3 which means 1 sec = 1 unit.
        val transmissionSpeed: Float = 3f / speed
        val timeUnits = StringBuilder()
        val morseCode = StringBuilder()
        val charUnits = arrayListOf<Int>()
        val characters = arrayListOf<Char>()
        // Add character morse timings to string builder
        var index = 0
        for (char in charMessage) {
            if (char == ' ') {
                timeUnits.replace(timeUnits.length - 1, timeUnits.length, "")
            }
            timeUnits.append(charToUnits[char])
            morseCode.append(charToMorse[char])
            if (charUnits.isNotEmpty()) {
                if (char == ' ') {
                    charUnits.add(charToTotalUnits[char]!!)
                } else {
                    charUnits[index - 1] = charUnits[index - 1] + 3
                    charUnits.add(charToTotalUnits[char]!!)
                }
            } else {
                charUnits.add(charToTotalUnits[char]!!)
            }
            index++
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