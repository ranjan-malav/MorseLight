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
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SoftPill
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.PillTone
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.StatusBadge
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SunkenCard
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.JetBrainsMono
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseRadius
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseLightTheme
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SendingDrillScreen(vm: SendingDrillViewModel, modifier: Modifier = Modifier) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    SendingDrillContent(ui, vm::clear, vm::toggleHint, vm::skip, vm::keyDown, vm::keyUp, modifier)
}

@Composable
fun SendingDrillContent(
    ui: SendingDrillUi,
    onClear: () -> Unit,
    onToggleHint: () -> Unit,
    onSkip: () -> Unit,
    onKeyDown: () -> Unit,
    onKeyUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MorseTheme.colors
    val holdCd = stringResource(R.string.hold_to_key_cd)

    Column(modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SunkenCard(Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()) {
                Eyebrow(stringResource(R.string.send_this_character))
                Text(ui.target.toString(), style = MaterialTheme.typography.displayLarge, color = c.textHeading)
                Text(if (ui.showHint) ui.code else "· · · · ·",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono), color = c.accent)
                StatusBadge(
                    when (ui.result) { SdResult.Correct -> stringResource(R.string.badge_correct); SdResult.Wrong -> stringResource(R.string.badge_wrong); else -> stringResource(R.string.badge_waiting) },
                    tone = when (ui.result) { SdResult.Correct -> BadgeTone.Success; SdResult.Wrong -> BadgeTone.Danger; else -> BadgeTone.Neutral },
                )
            }
        }

        SunkenCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Eyebrow(stringResource(R.string.label_you_keyed))
                Text(ui.buffer.ifBlank { "—" },
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMono), color = c.textHeading)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SoftPill(stringResource(R.string.action_clear), onClear, tone = PillTone.Neutral)
            SoftPill(if (ui.showHint) stringResource(R.string.action_hide) else stringResource(R.string.action_hint), onToggleHint, tone = PillTone.Neutral)
            SoftPill(stringResource(R.string.action_skip), onSkip, tone = PillTone.Neutral)
        }

        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.BottomCenter) {
            Box(
                Modifier.fillMaxWidth().height(64.dp).padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(MorseRadius.control))
                    .background(if (ui.keyOn) c.accentPress else c.accent)
                    .pointerInput(Unit) { detectTapGestures(onPress = { onKeyDown(); tryAwaitRelease(); onKeyUp() }) }
                    .semantics { role = Role.Button; contentDescription = holdCd },
                contentAlignment = Alignment.Center,
            ) { Text(stringResource(R.string.hold_to_key), style = MaterialTheme.typography.labelLarge, color = c.textOnAccent, textAlign = TextAlign.Center) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SendingDrillPreview() {
    MorseLightTheme { SendingDrillContent(SendingDrillUi(showHint = true), {}, {}, {}, {}, {}) }
}
