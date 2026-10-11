package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic colour roles from the Personal UI design system (tokens/colors.css + dark.css).
 * These carry the roles M3's ColorScheme has no slot for (sunken/raised surfaces, the three text
 * tiers, soft status washes, hairline borders, the accent glow). Access via MaterialTheme's sibling
 * `MorseTheme.colors`. The core roles are also mapped into the M3 ColorScheme in Theme.kt so stock
 * Material components pick up the accent.
 */
@Immutable
data class MorseColors(
    val accent: Color,
    val accentHover: Color,
    val accentPress: Color,
    val accentSoft: Color,
    val accentSoftBorder: Color,
    val accentOnSoft: Color,
    val accentGlow: Color, // colored halo behind lit torch disc + filled primary (--shadow-accent)

    val bgApp: Color,
    val surfaceCard: Color,
    val surfaceSunken: Color,
    val surfaceRaised: Color,

    val textHeading: Color,
    val textBody: Color,
    val textMuted: Color,
    val textSubtle: Color,
    val textOnAccent: Color,

    val borderSubtle: Color,
    val borderDefault: Color,
    val borderStrong: Color,
    val focusRing: Color,

    val success: Color,
    val successSoft: Color,
    val successOnSoft: Color,
    val warning: Color,
    val warningSoft: Color,
    val warningOnSoft: Color,
    val danger: Color,
    val dangerSoft: Color,
    val dangerOnSoft: Color,
    val info: Color,
    val infoSoft: Color,
    val infoOnSoft: Color,

    val track: Color, // slider/progress track
    val isDark: Boolean,
) {
    // Three-state per-symbol morse colouring (README §7.2).
    val morseSent: Color get() = success
    val morseCurrent: Color get() = accent
    val morsePending: Color get() = textSubtle
    val morseIdle: Color get() = textBody
}

val LightMorseColors = MorseColors(
    accent = Blue500,
    accentHover = Blue600,
    accentPress = Blue700,
    accentSoft = Blue50,
    accentSoftBorder = Blue100,
    accentOnSoft = Blue700,
    accentGlow = Color(0x6B1E5EFF), // rgba(30,94,255,.42)
    bgApp = Slate50,
    surfaceCard = Slate0,
    surfaceSunken = Slate100,
    surfaceRaised = Slate0,
    textHeading = Slate900,
    textBody = Slate800,
    textMuted = Slate600,
    textSubtle = Slate500,
    textOnAccent = Color.White,
    borderSubtle = Slate150,
    borderDefault = Slate200,
    borderStrong = Slate300,
    focusRing = Color(0x611E5EFF), // rgba(30,94,255,.38)
    success = Mint500, successSoft = Mint100, successOnSoft = Mint600,
    warning = Amber500, warningSoft = Amber100, warningOnSoft = Amber600,
    danger = Rose500, dangerSoft = Rose100, dangerOnSoft = Rose600,
    info = Violet500, infoSoft = Violet100, infoOnSoft = Violet600,
    track = Slate150,
    isDark = false,
)

val DarkMorseColors = MorseColors(
    accent = Blue400,
    accentHover = Blue300,
    accentPress = Blue500,
    accentSoft = Color(0x291E5EFF), // rgba(30,94,255,.16)
    accentSoftBorder = Color(0x3D8FAEFF), // rgba(143,174,255,.24)
    accentOnSoft = Blue200,
    accentGlow = Color(0x991E5EFF), // rgba(30,94,255,.60)
    bgApp = Slate950,
    surfaceCard = Slate900,
    surfaceSunken = Slate950,
    surfaceRaised = Slate850,
    textHeading = Color(0xFFF2F4F8),
    textBody = Color(0xFFE4E8F0),
    textMuted = Color(0xFFA6AEBC),
    textSubtle = Color(0xFF79828F),
    textOnAccent = Color.White,
    borderSubtle = Color(0xFF20252E),
    borderDefault = Color(0xFF262C37),
    borderStrong = Color(0xFF39414F),
    focusRing = Color(0x738FAEFF), // rgba(143,174,255,.45)
    success = MintDark, successSoft = Color(0x2E0B8A5C), successOnSoft = Color(0xFF7CDCB4),
    warning = AmberDark, warningSoft = Color(0x33B57300), warningOnSoft = Color(0xFFF3C878),
    danger = RoseDark, dangerSoft = Color(0x29D93B3B), dangerOnSoft = Color(0xFFFFA9A7),
    info = VioletDark, infoSoft = Color(0x336B4CF6), infoOnSoft = Color(0xFFC4B6FF),
    track = Color(0xFF262C37),
    isDark = true,
)

val LocalMorseColors = staticCompositionLocalOf { LightMorseColors }
