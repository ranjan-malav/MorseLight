package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.ranjan.malav.morselight_flashlightwithmorsecode.R

// Variable fonts (wght axis). FontVariation requires API 26+, which matches minSdk 26.
private fun pjs(weight: Int) = Font(
    R.font.plus_jakarta_sans,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

private fun jbm(weight: Int) = Font(
    R.font.jetbrains_mono,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

/** Plus Jakarta Sans — UI + display. */
val PlusJakarta = FontFamily(pjs(400), pjs(500), pjs(600), pjs(700), pjs(800))

/** JetBrains Mono — morse strings and codes. */
val JetBrainsMono = FontFamily(jbm(400), jbm(500), jbm(600))

// iOS-derived ramp (tokens/typography.css): 11·12·13·15·17·20·22·28·34·44, body 17/1.45.
val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(800), fontSize = 44.sp, lineHeight = 48.sp),
    displayMedium = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(800), fontSize = 34.sp, lineHeight = 40.sp),
    displaySmall = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(700), fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.01).em),
    headlineLarge = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(700), fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.01).em),
    headlineMedium = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(700), fontSize = 22.sp, lineHeight = 28.sp),
    headlineSmall = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(600), fontSize = 20.sp, lineHeight = 26.sp),
    titleLarge = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(600), fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(600), fontSize = 17.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(600), fontSize = 15.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(400), fontSize = 17.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(400), fontSize = 15.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(400), fontSize = 13.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(600), fontSize = 15.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(600), fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.06.em),
    labelSmall = TextStyle(fontFamily = PlusJakarta, fontWeight = FontWeight(600), fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.06.em),
)

/** Eyebrow/label caps role (12px/600, +0.06em tracking) used for "MORSELIGHT", "MORSE", etc. */
val EyebrowStyle = TextStyle(
    fontFamily = PlusJakarta, fontWeight = FontWeight(600), fontSize = 12.sp, letterSpacing = 0.06.em,
)

/** Morse string / code role (JetBrains Mono, 22/700 on the Send card). */
val MorseTextStyle = TextStyle(
    fontFamily = JetBrainsMono, fontWeight = FontWeight(700), fontSize = 22.sp, lineHeight = 30.sp, letterSpacing = 0.06.em,
)
