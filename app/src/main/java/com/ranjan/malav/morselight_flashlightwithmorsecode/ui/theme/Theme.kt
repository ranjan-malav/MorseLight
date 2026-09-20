package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Personal UI design system (design_handoff_morselight). Single signal-blue accent, no dynamic
 * colour (brand identity), light + dark. Stock Material components read the M3 ColorScheme below;
 * everything bespoke reads the richer role set via [MorseTheme.colors].
 */
private fun schemeFrom(c: MorseColors) = if (c.isDark) {
    darkColorScheme(
        primary = c.accent, onPrimary = c.textOnAccent,
        secondary = c.accent, onSecondary = c.textOnAccent,
        background = c.bgApp, onBackground = c.textBody,
        surface = c.surfaceCard, onSurface = c.textBody,
        surfaceVariant = c.surfaceSunken, onSurfaceVariant = c.textMuted,
        error = c.danger, onError = Color.Black,
        outline = c.borderStrong, outlineVariant = c.borderSubtle,
    )
} else {
    lightColorScheme(
        primary = c.accent, onPrimary = c.textOnAccent,
        secondary = c.accent, onSecondary = c.textOnAccent,
        background = c.bgApp, onBackground = c.textBody,
        surface = c.surfaceCard, onSurface = c.textBody,
        surfaceVariant = c.surfaceSunken, onSurfaceVariant = c.textMuted,
        error = c.danger, onError = Color.White,
        outline = c.borderStrong, outlineVariant = c.borderSubtle,
    )
}

@Composable
fun MorseLightTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkMorseColors else LightMorseColors
    CompositionLocalProvider(LocalMorseColors provides colors) {
        MaterialTheme(
            colorScheme = schemeFrom(colors),
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}

/** Access point for the extended design-system roles: `MorseTheme.colors.surfaceSunken`, etc. */
object MorseTheme {
    val colors: MorseColors
        @Composable @ReadOnlyComposable
        get() = LocalMorseColors.current
}
