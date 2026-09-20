package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.BadgeTone
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.Eyebrow
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SoftPill
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.PillTone
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.StatusBadge
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SunkenCard
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.JetBrainsMono
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseRadius
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme

@Composable
fun SendingDrillScreen(vm: SendingDrillViewModel, modifier: Modifier = Modifier) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val c = MorseTheme.colors

    Column(modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SunkenCard(Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()) {
                Eyebrow("Send this character")
                Text(ui.target.toString(), style = MaterialTheme.typography.displayLarge, color = c.textHeading)
                Text(if (ui.showHint) ui.code else "· · · · ·",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono), color = c.accent)
                StatusBadge(
                    when (ui.result) { SdResult.Correct -> "Correct"; SdResult.Wrong -> "Not that one"; else -> "Waiting" },
                    tone = when (ui.result) { SdResult.Correct -> BadgeTone.Success; SdResult.Wrong -> BadgeTone.Danger; else -> BadgeTone.Neutral },
                )
            }
        }

        SunkenCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Eyebrow("You keyed")
                Text(ui.buffer.ifBlank { "—" },
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono), color = c.textHeading)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SoftPill("Clear", vm::clear, tone = PillTone.Neutral)
            SoftPill(if (ui.showHint) "Hide" else "Hint", vm::toggleHint, tone = PillTone.Neutral)
            SoftPill("Skip", vm::skip, tone = PillTone.Neutral)
        }

        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.BottomCenter) {
            Box(
                Modifier.fillMaxWidth().height(64.dp).padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(MorseRadius.control))
                    .background(if (ui.keyOn) c.accentPress else c.accent)
                    .pointerInput(Unit) { detectTapGestures(onPress = { vm.keyDown(); tryAwaitRelease(); vm.keyUp() }) }
                    .semantics { role = Role.Button; contentDescription = "Hold to key the character" },
                contentAlignment = Alignment.Center,
            ) { Text("Hold to key", style = MaterialTheme.typography.labelLarge, color = c.textOnAccent, textAlign = TextAlign.Center) }
        }
    }
}
