package com.ranjan.malav.morselight_flashlightwithmorsecode.utils

import android.content.Context
import android.content.SharedPreferences
import com.ranjan.malav.morselight_flashlightwithmorsecode.BuildConfig


class SharedPreferenceUtils(val context: Context) {

    fun setInt(key: String, value: Int) {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String, defaultInt: Int): Int {
        return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
            .getInt(key, defaultInt)
    }
}
