package com.ranjan.malav.morselight_flashlightwithmorsecode.utils

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ranjan.malav.morselight_flashlightwithmorsecode.R


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
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

// Check if camera permission is given
fun Context.doesHaveCameraPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

fun Fragment.checkForCameraPermission(
    REQUIRED_PERMISSIONS: Array<String>,
    REQUEST_CODE_PERMISSIONS: Int,
    successCallback: () -> Unit
) {
    when {
        ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED -> {
            successCallback()
        }
        shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
            requireContext().showSettingsOpenDialog()
        }
        else -> {
            requestPermissions(
                REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }
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


var charToUnits = hashMapOf(
    'A' to "1133",
    'B' to "31111113",
    'C' to "31113113",
    'D' to "311113",
    'E' to "13",
    'F' to "11113113",
    'G' to "313113",
    'H' to "11111113",
    'I' to "1113",
    'J' to "11313133",
    'K' to "311133",
    'L' to "11311113",
    'M' to "3133",
    'N' to "3113",
    'O' to "313133",
    'P' to "11313113",
    'Q' to "31311133",
    'R' to "113113",
    'S' to "111113",
    'T' to "33",
    'U' to "111133",
    'V' to "11111133",
    'W' to "113133",
    'X' to "31111133",
    'Y' to "31113133",
    'Z' to "31311113",
    '0' to "3131313133",
    '1' to "1131313133",
    '2' to "1111313133",
    '3' to "1111113133",
    '4' to "1111111133",
    '5' to "1111111113",
    '6' to "3111111113",
    '7' to "3131111113",
    '8' to "3131311113",
    '9' to "3131313113",
    ' ' to "7",
)

var charToTotalUnits = hashMapOf(
    'A' to 5,
    'B' to 9,
    'C' to 11,
    'D' to 7,
    'E' to 1,
    'F' to 9,
    'G' to 9,
    'H' to 7,
    'I' to 3,
    'J' to 13,
    'K' to 9,
    'L' to 9,
    'M' to 7,
    'N' to 5,
    'O' to 11,
    'P' to 11,
    'Q' to 13,
    'R' to 7,
    'S' to 5,
    'T' to 3,
    'U' to 7,
    'V' to 9,
    'W' to 9,
    'X' to 11,
    'Y' to 13,
    'Z' to 11,
    '0' to 19,
    '1' to 17,
    '2' to 15,
    '3' to 13,
    '4' to 11,
    '5' to 9,
    '6' to 11,
    '7' to 13,
    '8' to 15,
    '9' to 17,
    ' ' to 7,
)

var charToMorse = hashMapOf(
    'A' to ".- ",
    'B' to "-... ",
    'C' to "-.-. ",
    'D' to "-.. ",
    'E' to ". ",
    'F' to "..-. ",
    'G' to "--. ",
    'H' to ".... ",
    'I' to ".. ",
    'J' to ".--- ",
    'K' to "-.- ",
    'L' to ".-.. ",
    'M' to "-- ",
    'N' to "-. ",
    'O' to "--- ",
    'P' to ".--. ",
    'Q' to "--.- ",
    'R' to ".-. ",
    'S' to "... ",
    'T' to "- ",
    'U' to "..- ",
    'V' to "...- ",
    'W' to ".-- ",
    'X' to "-..- ",
    'Y' to "-.-- ",
    'Z' to "--.. ",
    '0' to "----- ",
    '1' to ".---- ",
    '2' to "..--- ",
    '3' to "...-- ",
    '4' to "....- ",
    '5' to "..... ",
    '6' to "-.... ",
    '7' to "--... ",
    '8' to "---.. ",
    '9' to "----. ",
    ' ' to "/ ",
)

var morseToChar = hashMapOf(
    ".-" to "A",
    "-..." to "B",
    "-.-." to "C",
    "-.." to "D",
    "." to "E",
    "..-." to "F",
    "--." to "G",
    "...." to "H",
    ".." to "I",
    ".---" to "J",
    "-.-" to "K",
    ".-.." to "L",
    "--" to "M",
    "-." to "N",
    "---" to "O",
    ".--." to "P",
    "--.-" to "Q",
    ".-." to "R",
    "..." to "S",
    "-" to "T",
    "..-" to "U",
    "...-" to "V",
    ".--" to "W",
    "-..-" to "X",
    "-.--" to "Y",
    "--.." to "Z",
    "-----" to "0",
    ".----" to "1",
    "..---" to "2",
    "...--" to "3",
    "....-" to "4",
    "....." to "5",
    "-...." to "6",
    "--..." to "7",
    "---.." to "8",
    "----." to "9",
    "/" to " ",
)

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