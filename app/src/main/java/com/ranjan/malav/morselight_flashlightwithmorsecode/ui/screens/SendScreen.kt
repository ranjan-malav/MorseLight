package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.Eyebrow
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.FilledPill
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.MorseString
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.PillTone
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SoftPill
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.StatusBadge
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.BadgeTone
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SunkenCard
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.TorchDisc
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.LabeledSlider
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseRadius
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme
import androidx.compose.material3.MaterialTheme
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import androidx.compose.ui.tooling.preview.Preview
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.TransmitState
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseLightTheme

@Composable
fun SendScreen(vm: SendViewModel, modifier: Modifier = Modifier) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    SendContent(
        ui = ui,
        onMessageChange = vm::onMessageChange,
        onWpmChange = vm::onWpmChange,
        onManualTorch = vm::setManualTorch,
        onSignal = vm::sendSignal,
        onToggleSend = vm::toggleSend,
        onSos = vm::sendSos,
        modifier = modifier,
    )
}

@Composable
fun SendContent(
    ui: SendUiState,
    onMessageChange: (String) -> Unit,
    onWpmChange: (Int) -> Unit,
    onManualTorch: (Boolean) -> Unit,
    onSignal: () -> Unit,
    onToggleSend: () -> Unit,
    onSos: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MorseTheme.colors

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = ui.message,
            onValueChange = onMessageChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.send_hint)) },
            shape = RoundedCornerShape(MorseRadius.field),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = c.accent, unfocusedBorderColor = c.borderDefault,
            ),
        )

        SunkenCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    Eyebrow(stringResource(R.string.label_morse), Modifier.weight(1f))
                    Text(
                        if (ui.transmitting) stringResource(R.string.percent_sent, (ui.tx.percent * 100).toInt())
                        else stringResource(R.string.symbols_count, ui.symbolCount),
                        style = MaterialTheme.typography.labelMedium, color = c.textSubtle,
                    )
                }
                MorseString(
                    morse = ui.morse, transmitting = ui.transmitting,
                    currentIndex = ui.tx.symbolIndex, doneIndex = ui.tx.doneIndex,
                )
            }
        }

        // Torch disc fills the flexible middle, press-and-hold to key by hand.
        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TorchDisc(
                    on = ui.torchOn,
                    label = if (ui.torchOn) stringResource(R.string.torch_on) else stringResource(R.string.torch_off),
                    contentDescription = if (ui.torchOn) stringResource(R.string.torch_cd_on) else stringResource(R.string.torch_cd_off),
                    modifier = Modifier.pointerInput(ui.transmitting) {
                        if (!ui.transmitting) detectTapGestures(
                            onPress = {
                                onManualTorch(true)
                                tryAwaitRelease()
                                onManualTorch(false)
                            },
                        )
                    },
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(
                        if (ui.transmitting) stringResource(R.string.state_transmitting)
                        else if (ui.torchOn) stringResource(R.string.state_keying)
                        else stringResource(R.string.state_idle),
                        tone = when {
                            ui.transmitting -> BadgeTone.Accent
                            ui.torchOn -> BadgeTone.Success   // held / keying by hand
                            else -> BadgeTone.Neutral
                        },
                    )
                    Text(stringResource(R.string.hold_to_key_by_hand), style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
                }
            }
        }

        LabeledSlider(
            title = stringResource(R.string.transmission_speed), valueLabel = stringResource(R.string.wpm_value, ui.wpm),
            value = ui.wpm.toFloat(), onValueChange = { onWpmChange(it.toInt()) },
            valueRange = 1f..25f, steps = 0,
        )

        Row(
            Modifier.fillMaxWidth().padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SoftPill(stringResource(R.string.action_signal), onClick = onSignal, tone = PillTone.Accent, enabled = !ui.transmitting)
            FilledPill(
                if (ui.transmitting) stringResource(R.string.action_stop) else stringResource(R.string.action_send),
                onClick = onToggleSend,
                modifier = Modifier.weight(1f),
                tone = if (ui.transmitting) PillTone.Danger else PillTone.Accent,
            )
            SoftPill(stringResource(R.string.action_sos), onClick = onSos, tone = PillTone.Danger, enabled = !ui.transmitting)
        }
        Spacer(Modifier.height(0.dp))
    }
}

@Preview(name = "Send idle", showBackground = true)
@Preview(name = "Send transmitting", showBackground = true)
@Composable
private fun SendContentPreview() {
    MorseLightTheme {
        SendContent(
            ui = SendUiState(
                message = "HELLO", wpm = 12,
                tx = TransmitState(running = true, torchOn = true, symbolIndex = 6, doneIndex = 4, percent = 0.3f),
            ),
            onMessageChange = {}, onWpmChange = {}, onManualTorch = {},
            onSignal = {}, onToggleSend = {}, onSos = {},
        )
    }
}
