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
import androidx.lifecycle.lifecycleScope
import com.ranjan.malav.morselight_flashlightwithmorsecode.data.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.*
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.MorseEncoder
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.MorseDecoder.decryptMorse
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.MorseDecoder.findMorseFromTimings
import com.ranjan.malav.morselight_flashlightwithmorsecode.databinding.FragmentManualDecodeBinding
import java.util.*


class ManualDecodeFragment : Fragment(R.layout.fragment_manual_decode) {

    private var _binding: FragmentManualDecodeBinding? = null
    private val binding get() = _binding!!

    private val settings by lazy { SettingsRepository(requireContext().applicationContext) }
    // Temporary bridge: the initial read blocks briefly on DataStore's first file read.
    // Acceptable here because these Fragments are replaced by Compose screens in Phase 4;
    // the Compose ViewModels will collect SettingsRepository.settings as a Flow instead.
    private var isFlashOn = false
    private var ignoreClicks = false
    private var transmissionSpeed: Int = 3
    private var callback: FragmentCallbacks? = null
    private val viewModel: MainViewModel by activityViewModels()
    private val timings = arrayListOf<Long>()
    private val diffTimings = arrayListOf<Long>()

    companion object {
        private const val TAG = "ManualDecode"
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentManualDecodeBinding.bind(view)

        transmissionSpeed = runBlocking { settings.settings.first() }.speed

        binding.tapAndHoldButton.setOnTouchListener { _, event ->
            if (ignoreClicks) {
                runCleanUp()
                callback?.removeHandlers()
            } else {
                binding.reportButton.gone()
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

        binding.incomingMessage.movementMethod = ScrollingMovementMethod()

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
            timings.clear()
            diffTimings.clear()
            binding.reportButton.gone()
            binding.incomingMessage.text = ""
            binding.decodedMessage.text = ""
        }

        binding.decodeButton.setOnClickListener {
            val timingsCopy = arrayListOf<Long>()
            timingsCopy.addAll(timings)
            val morseMessage = findMorseFromTimings(timings, diffTimings)
            if (morseMessage.isNotBlank()) {
                val decryptedMessage = if (!morseMessage.contains("-")) {
                    // All the units are of same size, it could be . or -
                    val dashedMessage = morseMessage.replace(".", "-")
                    binding.incomingMessage.text = getString(
                        R.string.dot_message_or_dash_message, morseMessage, dashedMessage
                    )
                    getString(
                        R.string.dot_message_or_dash_message,
                        decryptMorse(morseMessage), decryptMorse(dashedMessage)
                    )
                } else {
                    binding.incomingMessage.text = morseMessage
                    decryptMorse(morseMessage)
                }
                binding.incomingMessage.text = morseMessage
                binding.decodedMessage.text = decryptedMessage
                binding.reportButton.visible()
                binding.reportButton.setOnClickListener {
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
            binding.decodedMessage.text = ""
        }
        binding.incomingMessage.text = sb.toString().trim()
    }

    private fun setTorchOffImageView() {
        val typedValue = TypedValue()
        val theme = requireActivity().theme
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
            ContextCompat.getColor(requireContext(), R.color.colorAccent),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    private fun runCleanUp() {
        ignoreClicks = false
        binding.reportButton.gone()
        binding.sosButton.text = getString(R.string.sos)
        binding.tapAndHoldButton.text = getString(R.string.press_hold)
        binding.signalButton.isEnabled = true
    }

    private fun playWithFlash(charMessage: ArrayList<Char>, speed: Int) {
        // Setup, remove click listeners
        ignoreClicks = true
        binding.reportButton.gone()
        binding.sosButton.text = getString(R.string.stop)
        binding.tapAndHoldButton.text = getString(R.string.stop)
        binding.signalButton.isEnabled = false

        val tx = MorseEncoder.encode(charMessage, speed)
        isFlashOn = false
        callback?.playWithFlash(
            tx.onOffDelays, tx.charUnits, arrayListOf(), speed,
            false, tx.finalOffDelay
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
