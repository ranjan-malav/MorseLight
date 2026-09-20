package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme

/** A titled slider with a right-aligned value label (e.g. "Transmission speed" … "12 wpm"). */
@Composable
fun LabeledSlider(
    title: String,
    valueLabel: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    modifier: Modifier = Modifier,
) {
    val c = MorseTheme.colors
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = c.textBody,
                modifier = Modifier.weight(1f))
            Text(valueLabel, style = MaterialTheme.typography.titleSmall, color = c.textMuted,
                textAlign = TextAlign.End)
        }
        Slider(
            value = value, onValueChange = onValueChange, valueRange = valueRange, steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = c.accent, activeTrackColor = c.accent, inactiveTrackColor = c.track,
            ),
        )
    }
}
