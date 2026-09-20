package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.ranjan.malav.morselight_flashlightwithmorsecode.R

// Nunito Sans, from the TTFs already in res/font (matches the pre-migration brand type).
val NunitoSans = FontFamily(
    Font(R.font.nunito_sans_400, FontWeight.Normal),
    Font(R.font.nunito_sans_600, FontWeight.SemiBold),
    Font(R.font.nunito_sans_700, FontWeight.Bold),
    Font(R.font.nunito_sans_800, FontWeight.ExtraBold),
)

private val default = Typography()
private fun TextStyle.nunito() = copy(fontFamily = NunitoSans)

val AppTypography = Typography(
    displayLarge = default.displayLarge.nunito(),
    displayMedium = default.displayMedium.nunito(),
    displaySmall = default.displaySmall.nunito(),
    headlineLarge = default.headlineLarge.nunito(),
    headlineMedium = default.headlineMedium.nunito(),
    headlineSmall = default.headlineSmall.nunito(),
    titleLarge = default.titleLarge.nunito(),
    titleMedium = default.titleMedium.nunito(),
    titleSmall = default.titleSmall.nunito(),
    bodyLarge = default.bodyLarge.nunito(),
    bodyMedium = default.bodyMedium.nunito(),
    bodySmall = default.bodySmall.nunito(),
    labelLarge = default.labelLarge.nunito(),
    labelMedium = default.labelMedium.nunito(),
    labelSmall = default.labelSmall.nunito(),
)
