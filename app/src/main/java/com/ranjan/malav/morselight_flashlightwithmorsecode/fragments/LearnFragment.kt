package com.ranjan.malav.morselight_flashlightwithmorsecode.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.SharedPreferenceUtils
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


@OptIn(KoinApiExtension::class)
class LearnFragment : Fragment(R.layout.fragment_learn), KoinComponent {

    private val sharedPref: SharedPreferenceUtils by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}