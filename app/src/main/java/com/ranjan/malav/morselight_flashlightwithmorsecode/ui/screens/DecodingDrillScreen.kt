package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.BadgeTone
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.Eyebrow
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.PillTone
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SoftPill
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.StatusBadge
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SunkenCard
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.TorchDisc
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseRadius
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme

@Composable
fun DecodingDrillScreen(vm: DecodingDrillViewModel, modifier: Modifier = Modifier) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val c = MorseTheme.colors

    Column(modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Target card
        SunkenCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Eyebrow("Target", Modifier.weight(1f))
                    StatusBadge(
                        when { ui.matched -> "Match"; ui.playing -> "Playing"; else -> "Ready" },
                        tone = when { ui.matched -> BadgeTone.Success; ui.playing -> BadgeTone.Accent; else -> BadgeTone.Neutral },
                    )
                }
                Text(
                    if (ui.reveal) ui.target else ui.masked,
                    style = MaterialTheme.typography.headlineMedium, color = c.textHeading,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SoftPill(if (ui.playing) "Stop" else "Play", vm::play, tone = PillTone.Accent)
                    SoftPill("Next", vm::next, tone = PillTone.Neutral)
                    SoftPill(if (ui.reveal) "Hide" else "Reveal", vm::toggleReveal, tone = PillTone.Neutral)
                }
            }
        }

        // Mocked sender disc
        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            TorchDisc(on = ui.senderOn, label = if (ui.senderOn) "On" else "Off")
        }

        // Your copy
        SunkenCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Eyebrow("Your copy")
                Text(ui.copied.ifBlank { "Hold the pad below to copy" },
                    style = MaterialTheme.typography.titleLarge,
                    color = if (ui.copied.isBlank()) c.textSubtle else c.textHeading)
            }
        }

        Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.weight(1f).height(64.dp).clip(RoundedCornerShape(MorseRadius.control))
                    .background(c.accent)
                    .pointerInput(Unit) { detectTapGestures(onPress = { vm.copyDown(); tryAwaitRelease(); vm.copyUp() }) },
                contentAlignment = Alignment.Center,
            ) { Text("Hold to copy", style = MaterialTheme.typography.labelLarge, color = c.textOnAccent) }
            SoftPill("Reset", vm::resetCopy, tone = PillTone.Neutral)
        }
    }
}
