package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseRadius
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme

enum class PillTone { Accent, Danger, Neutral }

/** Filled primary pill (52px) — the one strong action per screen. */
@Composable
fun FilledPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: PillTone = PillTone.Accent,
    enabled: Boolean = true,
    leading: (@Composable () -> Unit)? = null,
) {
    val c = MorseTheme.colors
    val bg = if (tone == PillTone.Danger) c.danger else c.accent
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(MorseRadius.control),
        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = Color.White),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        leading?.invoke()
        Text(text)
    }
}

/** Soft-tinted secondary pill — Signal (accent) / SOS (danger). */
@Composable
fun SoftPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: PillTone = PillTone.Accent,
    enabled: Boolean = true,
) {
    val c = MorseTheme.colors
    val (bg, fg) = when (tone) {
        PillTone.Accent -> c.accentSoft to c.accentOnSoft
        PillTone.Danger -> c.dangerSoft to c.dangerOnSoft
        PillTone.Neutral -> c.surfaceSunken to c.textBody
    }
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(MorseRadius.control),
        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) { Text(text) }
}
