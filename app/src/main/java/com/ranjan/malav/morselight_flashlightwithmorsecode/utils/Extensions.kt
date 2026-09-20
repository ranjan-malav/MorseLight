package com.ranjan.malav.morselight_flashlightwithmorsecode.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.ranjan.malav.morselight_flashlightwithmorsecode.R

fun Context.rateApp() {
    val uri = Uri.parse("market://details?id=" + applicationContext.packageName)
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    try {
        startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, getString(R.string.no_play_store), Toast.LENGTH_LONG).show()
    }
}

fun Context.launchWeb(uri: Uri) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(this, R.string.no_browser_app, Toast.LENGTH_SHORT).show()
    }
}
