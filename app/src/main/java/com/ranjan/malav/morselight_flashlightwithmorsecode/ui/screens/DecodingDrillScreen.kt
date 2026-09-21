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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.BadgeTone
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.Eyebrow
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.PillTone
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SoftPill
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.StatusBadge
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SunkenCard
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.TorchDisc
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseRadius
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseLightTheme
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DecodingDrillScreen(vm: DecodingDrillViewModel, modifier: Modifier = Modifier) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    DecodingDrillContent(ui, vm::play, vm::next, vm::toggleReveal, vm::copyDown, vm::copyUp, vm::resetCopy, modifier)
}

@Composable
fun DecodingDrillContent(
    ui: DecodingDrillUi,
    onPlay: () -> Unit,
    onNext: () -> Unit,
    onToggleReveal: () -> Unit,
    onCopyDown: () -> Unit,
    onCopyUp: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MorseTheme.colors
    val copyCd = stringResource(R.string.hold_to_copy_cd)

    Column(modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Target card
        SunkenCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Eyebrow(stringResource(R.string.label_target), Modifier.weight(1f))
                    StatusBadge(
                        when { ui.matched -> stringResource(R.string.badge_match); ui.playing -> stringResource(R.string.badge_playing); else -> stringResource(R.string.badge_ready) },
                        tone = when { ui.matched -> BadgeTone.Success; ui.playing -> BadgeTone.Accent; else -> BadgeTone.Neutral },
                    )
                }
                Text(
                    if (ui.reveal) ui.target else ui.masked,
                    style = MaterialTheme.typography.headlineMedium, color = c.textHeading,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SoftPill(if (ui.playing) stringResource(R.string.action_stop) else stringResource(R.string.action_play), onPlay, tone = PillTone.Accent)
                    SoftPill(stringResource(R.string.action_next), onNext, tone = PillTone.Neutral)
                    SoftPill(if (ui.reveal) stringResource(R.string.action_hide) else stringResource(R.string.action_reveal), onToggleReveal, tone = PillTone.Neutral)
                }
            }
        }

        // Mocked sender disc
        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            TorchDisc(on = ui.senderOn, label = if (ui.senderOn) stringResource(R.string.torch_on) else stringResource(R.string.torch_off))
        }

        // Your copy
        SunkenCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Eyebrow(stringResource(R.string.label_your_copy))
                Text(ui.copied.ifBlank { stringResource(R.string.copy_hint) },
                    style = MaterialTheme.typography.titleLarge,
                    color = if (ui.copied.isBlank()) c.textSubtle else c.textHeading)
            }
        }

        Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.weight(1f).height(64.dp).clip(RoundedCornerShape(MorseRadius.control))
                    .background(c.accent)
                    .pointerInput(Unit) { detectTapGestures(onPress = { onCopyDown(); tryAwaitRelease(); onCopyUp() }) }
                    .semantics { role = Role.Button; contentDescription = copyCd },
                contentAlignment = Alignment.Center,
            ) { Text(stringResource(R.string.hold_to_copy), style = MaterialTheme.typography.labelLarge, color = c.textOnAccent) }
            SoftPill(stringResource(R.string.action_reset), onReset, tone = PillTone.Neutral)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DecodingDrillPreview() {
    MorseLightTheme { DecodingDrillContent(DecodingDrillUi(reveal = true), {}, {}, {}, {}, {}, {}) }
}
