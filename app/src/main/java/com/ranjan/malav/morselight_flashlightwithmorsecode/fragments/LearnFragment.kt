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
import kotlinx.android.synthetic.main.fragment_learn.*


class LearnFragment : Fragment(R.layout.fragment_learn) {

    private var callback: FragmentCallbacks? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        learn_about.setOnClickListener {
            startActivity(Intent(requireContext(), MorseDetailActivity::class.java))
        }

        learn_decode.setOnClickListener {
            startActivity(Intent(requireContext(), MorseTutorialActivity::class.java))
        }

        learn_contact.setOnClickListener { requireContext().contactMail() }

        learn_rate.setOnClickListener { requireContext().rateApp() }

        learn_share.setOnClickListener { requireContext().shareApp() }

        learn_source.setOnClickListener {
            requireContext().launchWeb(
                Uri.parse("https://github.com/ranjan-malav/MorseLight")
            )
        }

        learn_donate.setOnClickListener {
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