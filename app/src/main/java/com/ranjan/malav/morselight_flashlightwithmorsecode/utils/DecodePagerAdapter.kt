package com.ranjan.malav.morselight_flashlightwithmorsecode.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ranjan.malav.morselight_flashlightwithmorsecode.fragments.AutoDecodeFragment
import com.ranjan.malav.morselight_flashlightwithmorsecode.fragments.ManualDecodeFragment
import org.koin.core.component.KoinApiExtension

class DecodePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    @KoinApiExtension
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ManualDecodeFragment()
            1 -> AutoDecodeFragment()
            else -> throw IllegalStateException("Unexpected position for FoodPagerAdapter, position: $position")
        }
    }
}