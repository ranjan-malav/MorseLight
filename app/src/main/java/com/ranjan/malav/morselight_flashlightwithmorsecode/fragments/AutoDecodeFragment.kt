package com.ranjan.malav.morselight_flashlightwithmorsecode.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ranjan.malav.morselight_flashlightwithmorsecode.MainViewModel
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import androidx.lifecycle.lifecycleScope
import com.ranjan.malav.morselight_flashlightwithmorsecode.data.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.*
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.MorseEncoder
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.MorseDecoder
import com.ranjan.malav.morselight_flashlightwithmorsecode.databinding.FragmentAutoDecodeBinding
import java.util.*


typealias LumaListener = (luma: Double) -> Unit

class AutoDecodeFragment : Fragment(R.layout.fragment_auto_decode), ImageAnalysisListener {

    private var _binding: FragmentAutoDecodeBinding? = null
    private val binding get() = _binding!!

    private val settings by lazy { SettingsRepository(requireContext().applicationContext) }
    // Temporary bridge: the initial read blocks briefly on DataStore's first file read.
    // Acceptable here because these Fragments are replaced by Compose screens in Phase 4;
    // the Compose ViewModels will collect SettingsRepository.settings as a Flow instead.
    private var isFlashOn = false
    private var ignoreClicks = false
    private var transmissionSpeed: Int = 3
    private var callback: FragmentCallbacks? = null
    private var startCapturing = false
    private var stopCapturingLowLuminosity = false
    private var avgLowLuminosity = 0.0
    private var avgHighLuminosity = 0.0
    private var avgCounter = 0
    private var perceptibility = 30
    private var percentageRectSize = 50
    private val handler = Handler(Looper.getMainLooper())
    private val viewModel: MainViewModel by activityViewModels()
    private val timings = arrayListOf<Long>()
    private val diffTimings = arrayListOf<Long>()

    companion object {
        private const val TAG = "AutoDecode"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAutoDecodeBinding.bind(view)

        val initial = runBlocking { settings.settings.first() }
        transmissionSpeed = initial.speed
        percentageRectSize = initial.reactSize
        perceptibility = initial.perceptibility

        binding.sizeSlider.value = percentageRectSize / 100f
        callback?.updateRectAreaPerc(percentageRectSize)

        binding.perceptibilitySlider.value = perceptibility.toFloat()
        setRectConstraints(percentageRectSize / 100f)

        binding.startStopButton.setOnClickListener {
            if (ignoreClicks) {
                runCleanUp()
                callback?.removeHandlers()
            } else {
                if (!startCapturing) {
                    // Start capturing high luminosity to on flash timings and lows to off flash timings
                    callback?.acquireWakeLock()
                    startCapturing = true
                    binding.reportButton.gone()
                    binding.incomingMessage.text = ""
                    binding.decodedMessage.text = ""
                    binding.startStopButton.text = getString(R.string.stop)
                    binding.sosButton.isEnabled = false
                    binding.signalButton.isEnabled = false
                    handler.postDelayed(timer3Sec, 0)
                    handler.postDelayed(timer2Sec, 1000)
                    handler.postDelayed(timer1Sec, 2000)
                    handler.postDelayed(timer0Sec, 2950)
                } else {
                    callback?.releaseWakeLock()
                    startCapturing = false
                    stopCapturingLowLuminosity = false
                    binding.sosButton.isEnabled = true
                    binding.signalButton.isEnabled = true
                    binding.startStopButton.text = getString(R.string.start)
                    binding.startTimer.text = ""
                    isFlashOn = false
                    binding.flashStatusView.gone()
                    avgLowLuminosity = 0.0
                    avgHighLuminosity = 0.0
                    avgCounter = 0
                    decodeNotedTimings()
                    removeHandlerCallbacks()
                }
            }
        }

        binding.perceptibilityTitle.isSelected = true
        binding.rectSizeTitle.isSelected = true
        binding.incomingMessage.movementMethod = ScrollingMovementMethod()

        binding.perceptibilitySlider.addOnChangeListener { _, value, _ ->
            perceptibility = value.toInt()
            viewLifecycleOwner.lifecycleScope.launch { settings.setPerceptibility(perceptibility) }
        }

        binding.sizeSlider.addOnChangeListener { _, value, _ ->
            percentageRectSize = (value * 100).toInt()
            viewLifecycleOwner.lifecycleScope.launch { settings.setReactSize(percentageRectSize) }
            setRectConstraints(value)
            callback?.updateRectAreaPerc(percentageRectSize)
        }

        binding.signalButton.setOnClickListener {
            if (ignoreClicks) return@setOnClickListener
            val charMessage = arrayListOf('E', 'E', 'E')
            playWithFlash(charMessage, 20)
        }

        binding.sosButton.setOnClickListener {
            if (ignoreClicks) {
                runCleanUp()
                callback?.removeHandlers()
            } else {
                val charMessage = arrayListOf('S', 'O', 'S')
                playWithFlash(charMessage, transmissionSpeed)
            }
        }

        binding.resetButton.setOnClickListener {
            runCleanUp()
        }

        viewModel.cleanRunFlag.observe(viewLifecycleOwner, {
            runCleanUp()
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as FragmentCallbacks
            callback?.setCurrentFragment("Auto")
        } catch (castException: ClassCastException) {
            throw ClassCastException("Context does not implement $TAG callback")
        }
    }

    override fun onResume() {
        super.onResume()
        callback?.bindPreview(binding.cameraPreview, this@AutoDecodeFragment)
    }

    override fun onPause() {
        super.onPause()
        callback?.removeImageListener()
        callback?.resetCameraBinds()
        callback?.releaseWakeLock()
    }

    private fun decodeNotedTimings() {
        val timingsCopy = arrayListOf<Long>()
        timingsCopy.addAll(timings)
        val morseMessage = MorseDecoder.findMorseFromTimings(timings, diffTimings)
        if (morseMessage.isNotBlank()) {
            val decryptedMessage = if (!morseMessage.contains("-")) {
                // All the units are of same size, it could be . or -
                val dashedMessage = morseMessage.replace(".", "-")
                binding.incomingMessage.text = getString(
                    R.string.dot_message_or_dash_message, morseMessage, dashedMessage
                )
                getString(
                    R.string.dot_message_or_dash_message,
                    MorseDecoder.decryptMorse(morseMessage),
                    MorseDecoder.decryptMorse(dashedMessage)
                )
            } else {
                binding.incomingMessage.text = morseMessage
                MorseDecoder.decryptMorse(morseMessage)
            }
            binding.decodedMessage.text = decryptedMessage
            binding.reportButton.visible()
            binding.reportButton.setOnClickListener {
                activity?.askIfDecryptedCorrectly(decryptedMessage, timingsCopy)
            }
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
            binding.decodedMessage.text = ""
        }
        binding.incomingMessage.text = sb.toString().trim()
    }

    private fun removeHandlerCallbacks() {
        try {
            handler.removeCallbacksAndMessages(null)
        } catch (npe: NullPointerException) {
            FirebaseCrashlytics.getInstance().recordException(npe)
            //Log.d(TAG, "Error: ${npe.localizedMessage}")
        }
    }

    private fun runCleanUp() {
        ignoreClicks = false
        binding.sosButton.text = getString(R.string.sos)
        binding.startStopButton.text = getString(R.string.start)
        binding.sosButton.isEnabled = true
        binding.signalButton.isEnabled = true
        binding.reportButton.gone()
        timings.clear()
        diffTimings.clear()
        binding.incomingMessage.text = ""
        binding.decodedMessage.text = ""
        avgLowLuminosity = 0.0
        avgHighLuminosity = 0.0
        avgCounter = 0
        startCapturing = false
        stopCapturingLowLuminosity = false
        binding.startTimer.text = ""
        isFlashOn = false
        binding.flashStatusView.gone()
    }

    private fun playWithFlash(charMessage: ArrayList<Char>, speed: Int) {
        // Setup, remove click listeners
        ignoreClicks = true
        binding.reportButton.gone()
        binding.sosButton.text = getString(R.string.stop)
        binding.startStopButton.text = getString(R.string.stop)
        binding.signalButton.isEnabled = false

        val tx = MorseEncoder.encode(charMessage, speed)
        isFlashOn = false
        callback?.playWithFlash(
            tx.onOffDelays, tx.charUnits, arrayListOf(), speed,
            false, tx.finalOffDelay
        )
    }

    private val timer3Sec = Runnable {
        binding.startTimer.text = getString(R.string.learning_low_luminosity, "3")
    }
    private val timer2Sec = Runnable {
        binding.startTimer.text = getString(R.string.learning_low_luminosity, "2")
    }
    private val timer1Sec = Runnable {
        binding.startTimer.text = getString(R.string.learning_low_luminosity, "1")
    }
    private val timer0Sec = Runnable {
        binding.startTimer.text = ""
        stopCapturingLowLuminosity = true
    }

    override fun listenLuminosity(luminosity: Double) {
        if (startCapturing) {
            activity?.let {
                it.runOnUiThread {
                    binding.avgLuminosity.text = getString(
                        R.string.average_current_luminosity,
                        String.format("%.1f", avgLowLuminosity),
                        String.format("%.1f", luminosity)
                    )
                }
            }
            if (!stopCapturingLowLuminosity) {
                avgLowLuminosity = (avgLowLuminosity * avgCounter + luminosity) / (avgCounter + 1)
                avgCounter++
            } else {
                avgCounter = 0
                if (luminosity > avgLowLuminosity * (1 + perceptibility / 100f) && !isFlashOn) {
                    isFlashOn = true
                    avgHighLuminosity =
                        (avgHighLuminosity * avgCounter + luminosity) / (avgCounter + 1)
                    avgCounter++
                    timings.add(System.currentTimeMillis())
                    if (timings.size > 1) {
                        diffTimings.add(timings[timings.size - 1] - timings[timings.size - 2])
                    }
                    activity?.let {
                        it.runOnUiThread {
                            binding.flashStatusView.visible()
                            updateTimingViews()
                        }
                    }
                }
                /*
                    Choosing 50% decrement to be considered as flash off change.
                    Consider this case, avgLowLuminosity = 100, if perceptibility is set to 30%
                    when luminosity is 130, we consider it flash on. Suppose avgHighLuminosity
                    slightly decreases from 130 and becomes 128.
                    When flash goes off, luminosity will return to near 100
                    If we compare it with (1 + perceptibility%), required luminosity to call flash
                    off becomes 98.46, that means low luminosity has to drop further than what it was
                    when we started.
                 */
                else if (luminosity * (1 + perceptibility / 200f) <= avgHighLuminosity && isFlashOn) {
                    isFlashOn = false
                    timings.add(System.currentTimeMillis())
                    if (timings.size > 1) {
                        diffTimings.add(timings[timings.size - 1] - timings[timings.size - 2])
                    }
                    activity?.let {
                        it.runOnUiThread {
                            binding.flashStatusView.gone()
                            updateTimingViews()
                        }
                    }
                }
            }
        } else {
            activity?.let {
                it.runOnUiThread {
                    binding.avgLuminosity.text =
                        getString(R.string.average_luminosity, String.format("%.1f", luminosity))
                }
            }
        }
    }

    private fun setRectConstraints(value: Float) {
        when (value) {
            0.1f -> setGuidePercentage(0.45f, 0.55f)
            0.2f -> setGuidePercentage(0.4f, 0.6f)
            0.3f -> setGuidePercentage(0.35f, 0.65f)
            0.4f -> setGuidePercentage(0.3f, 0.7f)
            0.5f -> setGuidePercentage(0.25f, 0.75f)
        }
    }

    private fun setGuidePercentage(leftTopPerc: Float, rightBottomPerc: Float) {
        val leftParams = binding.guideLeft.layoutParams as ConstraintLayout.LayoutParams
        leftParams.guidePercent = leftTopPerc
        binding.guideLeft.layoutParams = leftParams
        val topParams = binding.guideTop.layoutParams as ConstraintLayout.LayoutParams
        topParams.guidePercent = leftTopPerc
        binding.guideTop.layoutParams = topParams
        val rightParams = binding.guideRight.layoutParams as ConstraintLayout.LayoutParams
        rightParams.guidePercent = rightBottomPerc
        binding.guideRight.layoutParams = rightParams
        val bottomParams = binding.guideBottom.layoutParams as ConstraintLayout.LayoutParams
        bottomParams.guidePercent = rightBottomPerc
        binding.guideBottom.layoutParams = bottomParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
