package com.ranjan.malav.morselight_flashlightwithmorsecode.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.DecodePagerAdapter
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.SharedPreferenceUtils
import kotlinx.android.synthetic.main.fragment_receive.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


@OptIn(KoinApiExtension::class)
class ReceiveFragment : Fragment(R.layout.fragment_receive), KoinComponent {

    private val sharedPref: SharedPreferenceUtils by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        receive_viewpager.adapter = DecodePagerAdapter(requireActivity())

        TabLayoutMediator(tabs, receive_viewpager) { tab, position ->
            when (position) {
                0 -> tab.text = resources.getString(R.string.manual)
                1 -> tab.text = resources.getString(R.string.auto)
            }
        }.attach()
    }
}