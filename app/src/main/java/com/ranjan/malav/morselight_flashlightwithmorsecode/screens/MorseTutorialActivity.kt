package com.ranjan.malav.morselight_flashlightwithmorsecode.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.util.TypedValue
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.DecoderUtils
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.DecoderUtils.getMorseForMessage
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.charToMorse
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.charToUnits
import kotlinx.android.synthetic.main.activity_morse_tutorial.*
import java.util.*

class MorseTutorialActivity : AppCompatActivity() {

    private var ignoreClicks = false
    private var isFlashOn = false
    private val timings = arrayListOf<Long>()
    private val diffTimings = arrayListOf<Long>()
    private val handler = Handler(Looper.getMainLooper())
    private val handler2 = Handler(Looper.getMainLooper())
    private val offRunnable = Runnable {
        setTorchOffImageView()
    }

    private val onRunnable = Runnable {
        setTorchOnImageView()
    }
    private val cleanUpRunnable = Runnable {
        runCleanUp()
        start_timer.text = getString(R.string.finished)
    }
    private var messages = arrayOf(
        "NICE", "GOOD JOB", "OK", "SOS", "HELLO",
        "LETS GO", "NEED HELP", "HOW ARE YOU", "I AM FINE", "THIS IS AWESOME"
    )
    private var currentMessageIndex = 5
    private var currentMessage = messages[currentMessageIndex]

    companion object {
        private const val TAG = "MorseTutorial"
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
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_morse_tutorial)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tap_and_hold_button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN
                || event.action == MotionEvent.ACTION_UP
            ) {
                timings.add(System.currentTimeMillis())
                if (timings.size > 1) {
                    diffTimings.add(timings[timings.size - 1] - timings[timings.size - 2])
                }
                updateTimingViews()
            }
            return@setOnTouchListener true
        }

        morse_message.setText(currentMessage)
        morse_encoded_message.text = ".... . .-.. .-.. ---"
        incoming_message.movementMethod = ScrollingMovementMethod()

        morse_message.doOnTextChanged { text, _, _, _ ->
            val charArray = arrayListOf<Char>()
            text.toString().trim().toCharArray().forEach {
                charArray.add(it)
            }
            val morseCode = getMorseForMessage(charArray)
            morse_encoded_message.text = morseCode
        }

        next_button.setOnClickListener {
            currentMessageIndex++
            if (currentMessageIndex > messages.size - 1) {
                currentMessageIndex = 0
            }
            currentMessage = messages[currentMessageIndex]
            morse_message.setText(currentMessage)
        }

        start_stop_button.setOnClickListener {
            if (ignoreClicks) {
                runCleanUp()
            } else {
                val charMessage = morse_message.text.toString().trim()
                if (charMessage.isBlank()) {
                    Toast.makeText(
                        this, R.string.no_message_to_transmit, Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                val charArray = arrayListOf<Char>()
                charMessage.toCharArray().forEach {
                    charArray.add(it)
                }
                playWithFlash(charArray)
            }
        }

        decode_button.setOnClickListener {
            val morseMessage = DecoderUtils.findMorseFromTimings(timings, diffTimings)
            if (morseMessage.isNotBlank()) {
                if (!morseMessage.contains("-")) {
                    // All the units are of same size, it could be . or -
                    val dashedMessage = morseMessage.replace(".", "-")
                    incoming_message.text = getString(
                        R.string.dot_message_or_dash_message, morseMessage, dashedMessage
                    )
                    decoded_message.text = getString(
                        R.string.dot_message_or_dash_message,
                        DecoderUtils.decryptMorse(morseMessage),
                        DecoderUtils.decryptMorse(dashedMessage)
                    )
                } else {
                    incoming_message.text = morseMessage
                    decoded_message.text = DecoderUtils.decryptMorse(morseMessage)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun playWithFlash(charMessage: ArrayList<Char>) {
        // Setup, remove click listeners
        ignoreClicks = true
        incoming_message.text = ""
        decoded_message.text = ""
        start_stop_button.text = getString(R.string.stop)
        next_button.isEnabled = false
        handler.postDelayed(timer3Sec, 0)
        handler.postDelayed(timer2Sec, 1000)
        handler.postDelayed(timer1Sec, 2000)
        handler.postDelayed(timer0Sec, 2950)
        handler.postDelayed({
            // Speed can be from 1 to 10, 3 means 1 unit = 3/3 sec, 10 means 1 unit = 3/10 sec
            // 1 means 1 unit = 3/1 sec. Default speed is 3 which means 1 sec = 1 unit.
            val transmissionSpeed = 1f
            val timeUnits = StringBuilder()
            val morseCode = StringBuilder()

            // Add character morse timings to string builder
            for (char in charMessage) {
                if (char == ' ') {
                    timeUnits.replace(timeUnits.length - 1, timeUnits.length, "")
                }
                timeUnits.append(charToUnits[char])
                morseCode.append(charToMorse[char])
            }
            // Remove last character because we have added 3 units for space after every character
            timeUnits.replace(timeUnits.length - 1, timeUnits.length, "")

            var delay = 0L
            val onOffDelays = arrayListOf<Long>()
            morse_encoded_message.text = morseCode.toString()
            for (i in timeUnits.indices) {
                onOffDelays.add((delay * 1000 * transmissionSpeed).toLong())
                val unit = timeUnits[i].toString().toInt()
                delay += unit
            }

            for (i in onOffDelays.indices) {
                if (!isFlashOn) {
                    //Log.d(TAG, "Delay on: ${onOffDelays[i]}")
                    isFlashOn = true
                    handler.postDelayed(
                        onRunnable,
                        onOffDelays[i]
                    )
                } else {
                    isFlashOn = false
                    //Log.d(TAG, "Delay off: ${onOffDelays[i]}")
                    handler2.postDelayed(
                        offRunnable,
                        onOffDelays[i]
                    )
                }
            }
            handler2.postDelayed(cleanUpRunnable, (delay * 1000 * transmissionSpeed).toLong())
        }, 3000)
    }

    private fun removeHandlerCallbacks() {
        try {
            handler.removeCallbacksAndMessages(null)
        } catch (npe: NullPointerException) {
            FirebaseCrashlytics.getInstance().recordException(npe)
            //Log.d(TAG, "Error: ${npe.localizedMessage}")
        }
    }

    private fun setTorchOffImageView() {
        val typedValue = TypedValue()
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
            ContextCompat.getColor(this, R.color.colorAccent),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
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

    private fun runCleanUp() {
        ignoreClicks = false
        isFlashOn = false
        setTorchOffImageView()
        next_button.isEnabled = true
        start_stop_button.text = getString(R.string.start)
        removeHandlerCallbacks()
    }
}