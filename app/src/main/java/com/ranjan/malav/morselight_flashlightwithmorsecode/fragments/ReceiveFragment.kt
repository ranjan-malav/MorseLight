package com.ranjan.malav.morselight_flashlightwithmorsecode.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.DecodePagerAdapter
import kotlinx.android.synthetic.main.fragment_receive.*


class ReceiveFragment : Fragment(R.layout.fragment_receive) {

    private var callback: FragmentCallbacks? = null

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as FragmentCallbacks
        } catch (castException: ClassCastException) {
            throw ClassCastException("Context does not implement ReceiveFragment callback")
        }
    }

    override fun onPause() {
        super.onPause()
        callback?.removeImageListener()
        callback?.resetCameraBinds()
        callback?.releaseWakeLock()
    }
}