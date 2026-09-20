package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme

import androidx.compose.animation.core.CubicBezierEasing

// Motion tokens (tokens/motion.css). Durations in ms; callers should honour reduced-motion.
object MorseMotion {
    const val durInstant = 90
    const val durFast = 140
    const val durBase = 220
    const val durSlow = 320

    // Light on/off is deliberately shorter so keying feels instant (README).
    const val durTorch = 60

    val easeStandard = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val easeEmphasized = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
    val easeSpring = CubicBezierEasing(0.34f, 1.32f, 0.64f, 1f)
}
