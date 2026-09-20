package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.EyebrowStyle
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme

enum class BadgeTone { Neutral, Accent, Success, Warning, Danger }

/** Small tinted state pill — "Idle", "Keying", "Transmitting", "Match", etc. */
@Composable
fun StatusBadge(text: String, tone: BadgeTone = BadgeTone.Neutral, modifier: Modifier = Modifier) {
    val c = MorseTheme.colors
    val (bg, fg) = when (tone) {
        BadgeTone.Neutral -> c.surfaceSunken to c.textMuted
        BadgeTone.Accent -> c.accentSoft to c.accentOnSoft
        BadgeTone.Success -> c.successSoft to c.successOnSoft
        BadgeTone.Warning -> c.warningSoft to c.warningOnSoft
        BadgeTone.Danger -> c.dangerSoft to c.dangerOnSoft
    }
    Text(
        text,
        style = EyebrowStyle,
        color = fg,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(PaddingValues(horizontal = 10.dp, vertical = 4.dp)),
    )
}
