package com.ranjan.malav.morselight_flashlightwithmorsecode.utils

import android.content.Context
import android.content.SharedPreferences
import com.ranjan.malav.morselight_flashlightwithmorsecode.BuildConfig


class SharedPreferenceUtils(val context: Context) {

    fun getString(key: String, defaultString: String?): String? {
        return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
            .getString(key, defaultString)
    }

    fun setString(key: String, value: String?) {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getBool(key: String, defaultBool: Boolean): Boolean {
        return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
            .getBoolean(key, defaultBool)
    }

    fun setBool(key: String, value: Boolean) {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getInt(key: String, defaultInt: Int): Int {
        return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
            .getInt(key, defaultInt)
    }
}
