package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.CardSurface
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.Eyebrow
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SunkenCard
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme

@Composable
fun MoreScreen(
    vm: MoreViewModel,
    onOpenDecodingDrill: () -> Unit,
    onOpenSendingDrill: () -> Unit,
    onOpenReferenceChart: () -> Unit,
    onRate: () -> Unit,
    onSource: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s by vm.state.collectAsStateWithLifecycle()
    val c = MorseTheme.colors

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Learn / practice
        CardSurface(Modifier.fillMaxWidth(), padding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
            Column {
                NavRow("Decoding drill", "Copy a played message by hand", onOpenDecodingDrill)
                HorizontalDivider(color = c.borderSubtle)
                NavRow("Sending drill", "Key a prompted character", onOpenSendingDrill)
                HorizontalDivider(color = c.borderSubtle)
                NavRow("Reference chart", "Every letter and number", onOpenReferenceChart)
            }
        }

        // Preferences
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Eyebrow("Preferences")
            CardSurface(Modifier.fillMaxWidth(), padding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                Column {
                    SwitchRow("Key tone", "Sidetone while the light is on", s.keyTone, vm::setKeyTone)
                    HorizontalDivider(color = c.borderSubtle)
                    SwitchRow("Loop transmission", "Repeat the message until stopped", s.loop, vm::setLoop)
                    HorizontalDivider(color = c.borderSubtle)
                    SwitchRow("Keep screen awake", "While sending or receiving", s.keepAwake, vm::setKeepAwake)
                }
            }
        }

        // Support
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Eyebrow("Support")
            CardSurface(Modifier.fillMaxWidth(), padding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                Column {
                    NavRow("Rate this app", "Leave a review on the Play Store", onRate)
                    HorizontalDivider(color = c.borderSubtle)
                    NavRow("Source code", "MorseLight is open source", onSource)
                }
            }
        }

        SunkenCard(Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            Text(
                "MorseLight 3.0. Timing follows ITU-R M.1677, one unit at 12 wpm.",
                style = MaterialTheme.typography.bodyMedium, color = c.infoOnSoft,
            )
        }
    }
}

@Composable
private fun NavRow(title: String, subtitle: String, onClick: () -> Unit) {
    val c = MorseTheme.colors
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = c.textHeading)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
        }
        Icon(Icons.AutoMirrored.Outlined.ArrowForwardIos, null, tint = c.textSubtle,
            modifier = Modifier.padding(start = 8.dp).then(Modifier))
    }
}

@Composable
private fun SwitchRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    val c = MorseTheme.colors
    Row(
        Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = c.textHeading)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
        }
        Switch(
            checked = checked, onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedTrackColor = c.accent),
        )
    }
}
