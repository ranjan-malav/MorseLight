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
import com.ranjan.malav.morselight_flashlightwithmorsecode.databinding.ActivityMorseTutorialBinding
import java.util.*

class MorseTutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMorseTutorialBinding

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
        binding.startTimer.text = getString(R.string.finished)
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
        binding.startTimer.text = "3"
    }
    private val timer2Sec = Runnable {
        binding.startTimer.text = "2"
    }
    private val timer1Sec = Runnable {
        binding.startTimer.text = "1"
    }
    private val timer0Sec = Runnable {
        binding.startTimer.text = ""
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMorseTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tapAndHoldButton.setOnTouchListener { _, event ->
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

        binding.morseMessage.setText(currentMessage)
        binding.morseEncodedMessage.text = ".... . .-.. .-.. ---"
        binding.incomingMessage.movementMethod = ScrollingMovementMethod()

        binding.morseMessage.doOnTextChanged { text, _, _, _ ->
            val charArray = arrayListOf<Char>()
            text.toString().trim().toCharArray().forEach {
                charArray.add(it)
            }
            val morseCode = getMorseForMessage(charArray)
            binding.morseEncodedMessage.text = morseCode
        }

        binding.nextButton.setOnClickListener {
            currentMessageIndex++
            if (currentMessageIndex > messages.size - 1) {
                currentMessageIndex = 0
            }
            currentMessage = messages[currentMessageIndex]
            binding.morseMessage.setText(currentMessage)
        }

        binding.startStopButton.setOnClickListener {
            if (ignoreClicks) {
                runCleanUp()
            } else {
                val charMessage = binding.morseMessage.text.toString().trim()
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

        binding.decodeButton.setOnClickListener {
            val morseMessage = DecoderUtils.findMorseFromTimings(timings, diffTimings)
            if (morseMessage.isNotBlank()) {
                if (!morseMessage.contains("-")) {
                    // All the units are of same size, it could be . or -
                    val dashedMessage = morseMessage.replace(".", "-")
                    binding.incomingMessage.text = getString(
                        R.string.dot_message_or_dash_message, morseMessage, dashedMessage
                    )
                    binding.decodedMessage.text = getString(
                        R.string.dot_message_or_dash_message,
                        DecoderUtils.decryptMorse(morseMessage),
                        DecoderUtils.decryptMorse(dashedMessage)
                    )
                } else {
                    binding.incomingMessage.text = morseMessage
                    binding.decodedMessage.text = DecoderUtils.decryptMorse(morseMessage)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun playWithFlash(charMessage: ArrayList<Char>) {
        // Setup, remove click listeners
        ignoreClicks = true
        binding.incomingMessage.text = ""
        binding.decodedMessage.text = ""
        binding.startStopButton.text = getString(R.string.stop)
        binding.nextButton.isEnabled = false
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
            binding.morseEncodedMessage.text = morseCode.toString()
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
        binding.flashStatusText.text = getString(R.string.off)
        binding.flashStatusView.setColorFilter(
            color,
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    private fun setTorchOnImageView() {
        binding.flashStatusText.text = getString(R.string.on)
        binding.flashStatusView.setColorFilter(
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
            binding.decodedMessage.text = ""
        }
        binding.incomingMessage.text = sb.toString().trim()
    }

    private fun runCleanUp() {
        ignoreClicks = false
        isFlashOn = false
        setTorchOffImageView()
        binding.nextButton.isEnabled = true
        binding.startStopButton.text = getString(R.string.start)
        removeHandlerCallbacks()
    }
}