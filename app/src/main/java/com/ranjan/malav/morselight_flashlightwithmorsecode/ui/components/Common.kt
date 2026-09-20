package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.EyebrowStyle
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseRadius
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme

/** Uppercase caps label (12/600, +0.06em) — "MORSELIGHT", "MORSE", section headers. */
@Composable
fun Eyebrow(text: String, modifier: Modifier = Modifier, color: Color = MorseTheme.colors.textSubtle) {
    Text(text.uppercase(), style = EyebrowStyle, color = color, modifier = modifier,
        maxLines = 1, overflow = TextOverflow.Ellipsis)
}

/** Sunken tinted card for read-only output (never a field-like bordered box). */
@Composable
fun SunkenCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(MorseRadius.card))
            .background(MorseTheme.colors.surfaceSunken)
            .padding(padding),
    ) { content() }
}

/** Raised card with a hairline border (grouped lists, tiles). */
@Composable
fun CardSurface(
    modifier: Modifier = Modifier,
    radius: androidx.compose.ui.unit.Dp = MorseRadius.card,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit,
) {
    val c = MorseTheme.colors
    Box(
        modifier
            .clip(RoundedCornerShape(radius))
            .background(c.surfaceCard)
            .border(BorderStroke(1.dp, c.borderSubtle), RoundedCornerShape(radius))
            .padding(padding),
    ) { content() }
}
