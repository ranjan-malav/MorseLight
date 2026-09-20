package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Fixed brand scheme (no dynamic color) so the teal identity is consistent across devices.
private val LightColors = lightColorScheme(
    primary = Teal800,
    onPrimary = AppWhite,
    primaryContainer = Teal800Light,
    onPrimaryContainer = AppBlack,
    secondary = TealA700,
    onSecondary = AppWhite,
    tertiary = TealA700Light,
    background = AppWhite,
    onBackground = AppBlack,
    surface = AppWhite,
    onSurface = AppBlack,
    error = ErrorLight,
    onError = AppWhite,
)

private val DarkColors = darkColorScheme(
    primary = Teal800Light,
    onPrimary = AppBlack,
    primaryContainer = Teal800Dark,
    onPrimaryContainer = AppWhite,
    secondary = TealA700Light,
    onSecondary = AppBlack,
    tertiary = TealA700,
    background = AppDark,
    onBackground = AppWhite,
    surface = AppDarkSurface,
    onSurface = AppWhite,
    error = ErrorDark,
    onError = AppBlack,
)

@Composable
fun MorseLightTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
