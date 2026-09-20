package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Radius roles (tokens/radius.css): heavily rounded corner language.
object MorseRadius {
    val control = 999.dp // pills, discs
    val field = 16.dp
    val tile = 20.dp
    val card = 24.dp
    val sheet = 28.dp
}

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(MorseRadius.field),
    large = RoundedCornerShape(MorseRadius.card),
    extraLarge = RoundedCornerShape(MorseRadius.sheet),
)
