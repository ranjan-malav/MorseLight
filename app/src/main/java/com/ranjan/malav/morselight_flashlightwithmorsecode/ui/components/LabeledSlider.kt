package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme

/**
 * Titled slider with a right-aligned value label, styled to the Personal UI design system:
 * a 6dp rounded track and a 24dp white round thumb (soft shadow + hairline), no tick marks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabeledSlider(
    title: String,
    valueLabel: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    modifier: Modifier = Modifier,
    onHelp: (() -> Unit)? = null,
) {
    val c = MorseTheme.colors
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = c.textBody)
            if (onHelp != null) {
                Icon(
                    Icons.AutoMirrored.Outlined.HelpOutline,
                    contentDescription = stringResource(R.string.action_help, title),
                    tint = c.textSubtle,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(28.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onHelp)
                        .padding(5.dp),
                )
            }
            Spacer(Modifier.weight(1f))
            Text(valueLabel, style = MaterialTheme.typography.titleSmall, color = c.textMuted, textAlign = TextAlign.End)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            // Continuous drag (we snap in the callback); no visible tick marks, per the design.
            steps = 0,
            colors = SliderDefaults.colors(
                thumbColor = c.surfaceCard,
                activeTrackColor = c.accent,
                inactiveTrackColor = c.track,
            ),
            thumb = {
                Box(
                    Modifier
                        .size(24.dp)
                        .shadow(4.dp, CircleShape)
                        .background(c.surfaceCard, CircleShape)
                        .border(1.dp, c.borderSubtle, CircleShape),
                )
            },
            track = { state ->
                SliderDefaults.Track(
                    sliderState = state,
                    colors = SliderDefaults.colors(
                        activeTrackColor = c.accent,
                        inactiveTrackColor = c.track,
                    ),
                    drawStopIndicator = null,
                    thumbTrackGapSize = 0.dp,
                    trackInsideCornerSize = 0.dp,
                )
            },
        )
    }
}
