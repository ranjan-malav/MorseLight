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
import com.google.firebase.analytics.analytics
import com.google.firebase.Firebase
import com.ranjan.malav.morselight_flashlightwithmorsecode.MainViewModel
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import androidx.lifecycle.lifecycleScope
import com.ranjan.malav.morselight_flashlightwithmorsecode.data.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.*
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.MorseEncoder
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.charToMorse
import com.ranjan.malav.morselight_flashlightwithmorsecode.databinding.FragmentSendBinding
import java.util.*


class SendFragment : Fragment(R.layout.fragment_send) {

    private var _binding: FragmentSendBinding? = null
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

    companion object {
        private const val TAG = "SendFragment"
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSendBinding.bind(view)

        transmissionSpeed = runBlocking { settings.settings.first() }.speed
        binding.speedSlider.value = transmissionSpeed.toFloat()

        binding.speedSlider.addOnChangeListener { _, value, _ ->
            transmissionSpeed = value.toInt()
            viewLifecycleOwner.lifecycleScope.launch { settings.setSpeed(transmissionSpeed) }
            ignoreClicks = false
        }

        binding.flashStatusView.setOnTouchListener { _, event ->
            if (ignoreClicks) return@setOnTouchListener false
            if (event.action == MotionEvent.ACTION_DOWN) {
                callback?.switchTorch(true)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                callback?.switchTorch(false)
            }
            return@setOnTouchListener true
        }

        binding.sosButton.setOnClickListener {
            if (ignoreClicks) {
                runCleanUp()
                callback?.removeHandlers()
            } else {
                val charMessage = arrayListOf('S', 'O', 'S')
                playWithFlash(charMessage, transmissionSpeed, "SOS", true)
            }
        }

        binding.signalButton.setOnClickListener {
            if (ignoreClicks) return@setOnClickListener
            val charMessage = arrayListOf('E', 'E', 'E')
            playWithFlash(
                charMessage, 20, "EEE",
                false, isOnlySignal = true
            )
        }

        binding.startStopButton.setOnClickListener {
            if (ignoreClicks) {
                runCleanUp()
                callback?.removeHandlers()
            } else {
                val charMessage = binding.messageInput.editText?.text.toString().trim()
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
            if (it == ' ') {
                binding.currentChar.text = ""
                binding.currentCharMorse.text = ""
            } else {
                binding.currentChar.text = "$it = "
                binding.currentCharMorse.text = charToMorse[it]
            }
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
            callback?.setCurrentFragment("Send")
        } catch (castException: ClassCastException) {
            throw ClassCastException("Context does not implement $TAG callback")
        }
    }

    override fun onPause() {
        super.onPause()
        callback?.releaseWakeLock()
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
        binding.startStopButton.text = getString(R.string.start)
        binding.sosButton.text = getString(R.string.sos)
        binding.signalButton.isEnabled = true
        binding.currentChar.text = ""
        binding.currentCharMorse.text = ""
    }

    private fun playWithFlash(
        charMessage: ArrayList<Char>, speed: Int, message: String, shouldUpdateCurrentChar: Boolean,
        isOnlySignal: Boolean = false,
    ) {
        // Setup, remove click listeners
        ignoreClicks = true
        binding.startStopButton.text = getString(R.string.stop)
        binding.sosButton.text = getString(R.string.stop)
        binding.signalButton.isEnabled = false

        val tx = MorseEncoder.encode(charMessage, speed)
        if (!isOnlySignal) {
            binding.encodedMorseCode.text = tx.morseCode
            binding.messageInput.editText?.setText(message)
        }
        val characters: ArrayList<Char> =
            if (shouldUpdateCurrentChar) ArrayList(charMessage) else arrayListOf()
        isFlashOn = false
        callback?.playWithFlash(
            tx.onOffDelays, tx.charUnits, characters, speed,
            shouldUpdateCurrentChar, tx.finalOffDelay
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
