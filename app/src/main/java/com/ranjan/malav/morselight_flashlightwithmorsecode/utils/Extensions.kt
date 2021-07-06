package com.ranjan.malav.morselight_flashlightwithmorsecode.utils

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
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