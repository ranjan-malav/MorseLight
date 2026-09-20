package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.StatusBadge
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme

/** Temporary placeholder for the decoding/sending drills (built in the next increment). */
@Composable
fun DrillPlaceholderScreen(name: String, modifier: Modifier = Modifier) {
    val c = MorseTheme.colors
    Column(
        modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(name, style = MaterialTheme.typography.headlineMedium, color = c.textHeading)
        StatusBadge("Coming soon")
        Text(
            "This practice mode is being built next. The encode/decode and keying engine it uses is already done.",
            style = MaterialTheme.typography.bodyMedium, color = c.textMuted, textAlign = TextAlign.Center,
        )
    }
}
