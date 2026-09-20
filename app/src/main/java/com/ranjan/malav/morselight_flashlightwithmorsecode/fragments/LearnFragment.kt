package com.ranjan.malav.morselight_flashlightwithmorsecode.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.screens.MorseDetailActivity
import com.ranjan.malav.morselight_flashlightwithmorsecode.screens.MorseTutorialActivity
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.contactMail
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.launchWeb
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.rateApp
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.shareApp
import com.ranjan.malav.morselight_flashlightwithmorsecode.databinding.FragmentLearnBinding


class LearnFragment : Fragment(R.layout.fragment_learn) {

    private var callback: FragmentCallbacks? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentLearnBinding.bind(view)

        binding.learnAbout.setOnClickListener {
            startActivity(Intent(requireContext(), MorseDetailActivity::class.java))
        }

        binding.learnDecode.setOnClickListener {
            startActivity(Intent(requireContext(), MorseTutorialActivity::class.java))
        }

        binding.learnContact.setOnClickListener { requireContext().contactMail() }

        binding.learnRate.setOnClickListener { requireContext().rateApp() }

        binding.learnShare.setOnClickListener { requireContext().shareApp() }

        binding.learnSource.setOnClickListener {
            requireContext().launchWeb(
                Uri.parse("https://github.com/ranjan-malav/MorseLight")
            )
        }

        binding.learnDonate.setOnClickListener {
            requireContext().launchWeb(
                Uri.parse("https://ko-fi.com/ranjan")
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as FragmentCallbacks
            callback?.setCurrentFragment("Learn")
        } catch (castException: ClassCastException) {
            throw ClassCastException("Context does not implement LearnFragment callback")
        }
    }
}