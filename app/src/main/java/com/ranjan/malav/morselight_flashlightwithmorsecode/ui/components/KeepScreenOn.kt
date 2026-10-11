package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import java.util.WeakHashMap

/**
 * Keeps the screen awake while [enabled] is true, scoped to the composable that calls it.
 *
 * Reference-counted per host [View]: several screens can request "keep on" at once (which happens
 * briefly during a navigation transition, when the incoming screen has composed but the outgoing one
 * hasn't been disposed yet). A plain `view.keepScreenOn = true/false` would let the outgoing screen's
 * dispose clear the flag the incoming screen just set. Counting keeps it on until the last requester
 * leaves. Sets `View.keepScreenOn` (the window's FLAG_KEEP_SCREEN_ON), available since API 1.
 */
@Composable
fun KeepScreenOn(enabled: Boolean) {
    val view = LocalView.current
    DisposableEffect(view, enabled) {
        if (enabled) retain(view)
        onDispose { if (enabled) release(view) }
    }
}

// Effects run on the main thread, so this needs no synchronization. WeakHashMap avoids retaining Views.
private val counts = WeakHashMap<View, Int>()

private fun retain(view: View) {
    val n = (counts[view] ?: 0) + 1
    counts[view] = n
    view.keepScreenOn = n > 0
}

private fun release(view: View) {
    val n = ((counts[view] ?: 1) - 1).coerceAtLeast(0)
    counts[view] = n
    view.keepScreenOn = n > 0
}
