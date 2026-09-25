package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

/**
 * Keeps the screen awake while [enabled] is true, scoped to the composable that calls it. Sets
 * `View.keepScreenOn` (which toggles the window's FLAG_KEEP_SCREEN_ON) and always clears it on
 * leave — so the screen only stays on where a screen actually asks for it, never app-wide.
 */
@Composable
fun KeepScreenOn(enabled: Boolean) {
    val view = LocalView.current
    DisposableEffect(enabled) {
        view.keepScreenOn = enabled
        onDispose { view.keepScreenOn = false }
    }
}
