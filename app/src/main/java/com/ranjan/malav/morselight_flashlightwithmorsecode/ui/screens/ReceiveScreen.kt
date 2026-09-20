package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.Eyebrow
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SunkenCard
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.TorchDisc
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.LabeledSlider
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.JetBrainsMono
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseRadius
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme

@Composable
fun ReceiveScreen(vm: ReceiveViewModel, modifier: Modifier = Modifier) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val c = MorseTheme.colors

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = ui.mode == RxMode.Manual, onClick = { vm.setMode(RxMode.Manual) },
                shape = SegmentedButtonDefaults.itemShape(0, 2),
            ) { Text("Manual key") }
            SegmentedButton(
                selected = ui.mode == RxMode.Camera, onClick = { vm.setMode(RxMode.Camera) },
                shape = SegmentedButtonDefaults.itemShape(1, 2),
            ) { Text("Camera") }
        }

        // Decoded output card
        SunkenCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Eyebrow("Decoded", Modifier.weight(1f))
                    TextButton(onClick = vm::reset) { Text("Reset") }
                }
                Text(
                    ui.decoded.ifBlank { "Nothing copied yet" },
                    style = MaterialTheme.typography.titleLarge,
                    color = if (ui.decoded.isBlank()) c.textSubtle else c.textHeading,
                )
                Text(
                    ui.buffer.trim().ifBlank { "—" },
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = JetBrainsMono),
                    color = c.textMuted,
                )
            }
        }

        if (ui.mode == RxMode.Camera) {
            // Viewfinder placeholder + live luminance chip (preview wiring lands with CameraX view).
            Box(
                Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(MorseRadius.card))
                    .background(androidx.compose.ui.graphics.Color(0xFF05070B)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier.fillMaxWidth(ui.detectionArea / 100f).height(120.dp)
                        .border(2.dp, c.accent, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(if (ui.reading) "Reading" else "Waiting",
                        style = MaterialTheme.typography.labelMedium, color = c.textOnAccent)
                }
            }
            Text("lum ${ui.luminance.toInt()}",
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = JetBrainsMono), color = c.textMuted)
            SunkenCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabeledSlider("Sensitivity", "${ui.sensitivity}%", ui.sensitivity.toFloat(),
                        { vm.setSensitivity(it.toInt()) }, 0f..100f, 0)
                    LabeledSlider("Detection area", "${ui.detectionArea}", ui.detectionArea.toFloat(),
                        { vm.setDetectionArea(it.toInt()) }, 30f..90f, 0)
                }
            }
        } else {
            Text("Hold while the sender's light is on. Short is a dot, long is a dash.",
                style = MaterialTheme.typography.bodyMedium, color = c.textMuted,
                modifier = Modifier.fillMaxWidth())
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.BottomCenter) {
                TorchDisc(
                    on = ui.keyOn, label = "Hold", icon = Icons.Outlined.RadioButtonChecked,
                    contentDescription = "Key: hold while the sender's light is on",
                    modifier = Modifier.padding(bottom = 16.dp).pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            vm.keyDown(); tryAwaitRelease(); vm.keyUp()
                        })
                    },
                )
            }
        }
    }
}
