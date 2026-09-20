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
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
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

@Composable
fun SendScreen(vm: SendViewModel, modifier: Modifier = Modifier) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val c = MorseTheme.colors

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = ui.message,
            onValueChange = vm::onMessageChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Type your message") },
            shape = RoundedCornerShape(MorseRadius.field),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = c.accent, unfocusedBorderColor = c.borderDefault,
            ),
        )

        SunkenCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    Eyebrow("Morse", Modifier.weight(1f))
                    Text(
                        if (ui.transmitting) "${(ui.tx.percent * 100).toInt()}% sent"
                        else "${ui.symbolCount} symbols",
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
                    label = if (ui.torchOn) "On" else "Off",
                    contentDescription = "Flashlight ${if (ui.torchOn) "on" else "off"}. Hold to key by hand.",
                    modifier = Modifier.pointerInput(ui.transmitting) {
                        if (!ui.transmitting) detectTapGestures(
                            onPress = {
                                vm.setManualTorch(true)
                                tryAwaitRelease()
                                vm.setManualTorch(false)
                            },
                        )
                    },
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(
                        if (ui.transmitting) "Transmitting" else if (ui.torchOn) "Keying" else "Idle",
                        tone = if (ui.transmitting) BadgeTone.Accent else BadgeTone.Neutral,
                    )
                    Text("Hold to key by hand", style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
                }
            }
        }

        LabeledSlider(
            title = "Transmission speed", valueLabel = "${ui.wpm} wpm",
            value = ui.wpm.toFloat(), onValueChange = { vm.onWpmChange(it.toInt()) },
            valueRange = 5f..25f, steps = 25 - 5 - 1,
        )

        Row(
            Modifier.fillMaxWidth().padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SoftPill("Signal", onClick = vm::sendSignal, tone = PillTone.Accent, enabled = !ui.transmitting)
            FilledPill(
                if (ui.transmitting) "Stop" else "Send",
                onClick = vm::toggleSend,
                modifier = Modifier.weight(1f),
                tone = if (ui.transmitting) PillTone.Danger else PillTone.Accent,
            )
            SoftPill("SOS", onClick = vm::sendSos, tone = PillTone.Danger, enabled = !ui.transmitting)
        }
        Spacer(Modifier.height(0.dp))
    }
}
