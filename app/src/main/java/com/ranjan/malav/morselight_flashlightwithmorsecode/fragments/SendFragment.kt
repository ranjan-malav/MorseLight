package com.ranjan.malav.morselight_flashlightwithmorsecode.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.ranjan.malav.morselight_flashlightwithmorsecode.MainViewModel
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.*
import kotlinx.android.synthetic.main.fragment_send.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


@KoinApiExtension
class SendFragment : Fragment(R.layout.fragment_send), KoinComponent {

    private val sharedPref: SharedPreferenceUtils by inject()
    private var isFlashOn = false
    private var ignoreClicks = false
    private var transmissionSpeed: Int = 3
    private var callback: FragmentCallbacks? = null
    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        private const val TAG = "SendFragment"
        private const val SPEED = "speed"
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transmissionSpeed = sharedPref.getInt(SPEED, 3)
        speed_slider.value = transmissionSpeed.toFloat()

        speed_slider.addOnChangeListener { _, value, _ ->
            transmissionSpeed = value.toInt()
            sharedPref.setInt(SPEED, transmissionSpeed)
            ignoreClicks = false
        }

        flash_status_view.setOnTouchListener { _, event ->
            if (ignoreClicks) return@setOnTouchListener false
            if (event.action == MotionEvent.ACTION_DOWN) {
                callback?.switchTorch(true)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                callback?.switchTorch(false)
            }
            return@setOnTouchListener true
        }

        sos_button.setOnClickListener {
            if (ignoreClicks) {
                runCleanUp()
                callback?.removeHandlers()
            } else {
                val charMessage = arrayListOf('S', 'O', 'S')
                playWithFlash(charMessage, transmissionSpeed, "SOS", true)
            }
        }

        signal_button.setOnClickListener {
            if (ignoreClicks) return@setOnClickListener
            val charMessage = arrayListOf('E', 'E', 'E')
            playWithFlash(charMessage, 20, "EEE", false)
        }

        start_stop_button.setOnClickListener {
            if (ignoreClicks) {
                runCleanUp()
                callback?.removeHandlers()
            } else {
                val charMessage = message_input.editText?.text.toString().trim()
                if (charMessage.isBlank()) {
                    Toast.makeText(
                        requireContext(), R.string.no_message_to_transmit, Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                val param = Bundle().apply { putString("message", charMessage) }
                Firebase.analytics.logEvent("entered_message", param)
                val charArray = arrayListOf<Char>()
                charMessage.toCharArray().forEach {
                    charArray.add(it)
                }
                playWithFlash(
                    charArray, transmissionSpeed, charMessage, true
                )
            }
        }

        viewModel.currentlyTransmittingChar.observe(viewLifecycleOwner, {
            current_char.text = "$it = "
            current_char_morse.text = charToMorse[it]
        })

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
        } catch (castException: ClassCastException) {
            throw ClassCastException("Context does not implement $TAG callback")
        }
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
        start_stop_button.text = getString(R.string.start)
        sos_button.text = getString(R.string.sos)
        signal_button.isEnabled = true
        current_char.text = ""
        current_char_morse.text = ""
    }

    private fun playWithFlash(
        charMessage: ArrayList<Char>, speed: Int, message: String, shouldUpdateCurrentChar: Boolean
    ) {
        // Setup, remove click listeners
        ignoreClicks = true
        start_stop_button.text = getString(R.string.stop)
        sos_button.text = getString(R.string.stop)
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
        encoded_morse_code.text = morseCode.toString()
        message_input.editText?.setText(message)
        for (i in timeUnits.indices) {
            onOffDelays.add((delay * 1000 * transmissionSpeed).toLong())
            val unit = timeUnits[i].toString().toInt()
            delay += unit
        }

        var charDelay = 0
        if (shouldUpdateCurrentChar) {
            for (i in charUnits.indices) {
                val unit = charUnits[i]
                characters.add(charMessage[i])
                charDelay += unit
            }
        }
        isFlashOn = false
        callback?.playWithFlash(
            onOffDelays, charUnits, characters, speed,
            shouldUpdateCurrentChar, (delay * 1000 * transmissionSpeed).toLong()
        )
    }
}