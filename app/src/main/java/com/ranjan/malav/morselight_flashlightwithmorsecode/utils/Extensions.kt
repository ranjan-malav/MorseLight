package com.ranjan.malav.morselight_flashlightwithmorsecode.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.Firebase
import com.ranjan.malav.morselight_flashlightwithmorsecode.R


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun startInstalledAppDetailsActivity(context: Context?) {
    if (context == null) {
        return
    }
    val i = Intent()
    i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    i.addCategory(Intent.CATEGORY_DEFAULT)
    i.data = Uri.parse("package:" + context.packageName)
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    context.startActivity(i)
}

fun Context.showSettingsOpenDialog(
    setCancelable: Boolean = true,
) {
    val builder = AlertDialog.Builder(this)
    builder.setCancelable(setCancelable)
    builder.setTitle(R.string.open_settings_title)
    builder.setMessage(R.string.open_settings_message)
    builder.setNegativeButton(R.string.cancel, null)
    builder.setPositiveButton(
        R.string.ok
    ) { _, _ -> startInstalledAppDetailsActivity(this) }
    builder.create().show()
}






fun Context.shareApp(): Intent {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(
        Intent.EXTRA_TEXT,
        "Hey! Check out this awesome app!!" +
                "https://play.google.com/store/apps/details?id=" + applicationContext.packageName
    )
    return shareIntent
}

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

fun Context.contactMail() {
    val i = Intent(Intent.ACTION_SENDTO)
    i.type = "message/rfc822"
    i.data = Uri.parse("mailto:ranjan2192@gmail.com")
    val mailer = Intent.createChooser(i, getString(R.string.send_mail))
    try {
        startActivity(mailer)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, getString(R.string.no_email_app), Toast.LENGTH_LONG).show()
    }
}

fun Context.launchWeb(uri: Uri) {
    try {
        val browserIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(browserIntent)
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(this, R.string.no_browser_app, Toast.LENGTH_SHORT).show()
    }
}

fun Activity.askIfDecryptedCorrectly(message: String, timings: ArrayList<Long>) {
    val builder = AlertDialog.Builder(this)
    val inflater: LayoutInflater = this.layoutInflater
    val dialogView: View = inflater.inflate(R.layout.feedback_dialog, null)
    builder.setView(dialogView)
    val messageInput: TextInputLayout = dialogView.findViewById(R.id.message_input)
    val reportButton: MaterialButton = dialogView.findViewById(R.id.negative_button)
    val positiveButton: MaterialButton = dialogView.findViewById(R.id.positive_button)
    val dialog = builder.create()
    messageInput.editText?.setText(message)

    reportButton.setOnClickListener {
        val finalMessage = messageInput.editText?.text.toString()
        FirebaseCrashlytics.getInstance()
            .log("Message: $finalMessage; Timings: ${getTimingsString(timings)}")
        FirebaseCrashlytics.getInstance().recordException(Throwable("Wrong decoding"))
        dialog.dismiss()
    }

    positiveButton.setOnClickListener {
        val param = Bundle().apply {
            putString("message", messageInput.editText?.text.toString())
            putString("timings", getTimingsString(timings))

        }
        Firebase.analytics.logEvent("positive_decode", param)
        dialog.dismiss()
    }

    dialog.show()
}

private fun getTimingsString(timings: ArrayList<Long>): String {
    val sb = StringBuilder()
    if (timings.size > 1) {
        timings.forEachIndexed { index, _ ->
            if (index == 0) return@forEachIndexed
            val diff = timings[index] - timings[index - 1]
            if (index % 2 == 0) {
                sb.append("${String.format("%.1f", (diff / 1000f))}s ")
            } else {
                sb.append("${String.format("%.1f", (diff / 1000f))}s ")
            }
        }
    }
    return sb.toString()
}