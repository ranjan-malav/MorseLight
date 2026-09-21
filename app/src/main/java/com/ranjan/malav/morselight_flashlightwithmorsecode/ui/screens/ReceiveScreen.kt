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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.torch.TorchController
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.CameraPreview
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SegmentedControl
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.Eyebrow
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SunkenCard
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.TorchDisc
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.LabeledSlider
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.JetBrainsMono
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseRadius
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseLightTheme
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ReceiveScreen(vm: ReceiveViewModel, torch: TorchController, modifier: Modifier = Modifier) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    ReceiveContent(
        ui = ui,
        onSetMode = vm::setMode, onReset = vm::reset,
        onSensitivity = vm::setSensitivity, onDetectionArea = vm::setDetectionArea,
        onKeyDown = vm::keyDown, onKeyUp = vm::keyUp,
        cameraContent = { CameraPreview(torch, Modifier.fillMaxWidth().height(160.dp)) },
        modifier = modifier,
    )
}

@Composable
fun ReceiveContent(
    ui: ReceiveUiState,
    onSetMode: (RxMode) -> Unit,
    onReset: () -> Unit,
    onSensitivity: (Int) -> Unit,
    onDetectionArea: (Int) -> Unit,
    onKeyDown: () -> Unit,
    onKeyUp: () -> Unit,
    cameraContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MorseTheme.colors

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SegmentedControl(
            options = listOf(stringResource(R.string.rx_manual), stringResource(R.string.rx_camera)),
            selectedIndex = if (ui.mode == RxMode.Manual) 0 else 1,
            onSelect = { onSetMode(if (it == 0) RxMode.Manual else RxMode.Camera) },
            modifier = Modifier.fillMaxWidth(),
        )

        // Decoded output card
        SunkenCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Eyebrow(stringResource(R.string.label_decoded), Modifier.weight(1f))
                    TextButton(onClick = onReset) { Text(stringResource(R.string.action_reset)) }
                }
                Text(
                    ui.decoded.ifBlank { stringResource(R.string.nothing_copied) },
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
                    Text(if (ui.reading) stringResource(R.string.rx_reading) else stringResource(R.string.rx_waiting),
                        style = MaterialTheme.typography.labelMedium, color = c.textOnAccent)
                }
            }
            Text(stringResource(R.string.lum_value, ui.luminance.toInt()),
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = JetBrainsMono), color = c.textMuted)
            SunkenCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabeledSlider(stringResource(R.string.sensitivity), stringResource(R.string.percent, ui.sensitivity), ui.sensitivity.toFloat(),
                        { onSensitivity(it.toInt()) }, 0f..100f, 0)
                    LabeledSlider(stringResource(R.string.detection_area), "${ui.detectionArea}", ui.detectionArea.toFloat(),
                        { onDetectionArea(it.toInt()) }, 30f..90f, 0)
                }
            }
        } else {
            Text(stringResource(R.string.rx_manual_hint),
                style = MaterialTheme.typography.bodyMedium, color = c.textMuted,
                modifier = Modifier.fillMaxWidth())
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.BottomCenter) {
                TorchDisc(
                    on = ui.keyOn, label = stringResource(R.string.key_hold), icon = Icons.Outlined.RadioButtonChecked,
                    contentDescription = stringResource(R.string.key_cd),
                    modifier = Modifier.padding(bottom = 16.dp).pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            onKeyDown(); tryAwaitRelease(); onKeyUp()
                        })
                    },
                )
            }
        }
    }
}

@Preview(name = "Receive manual", showBackground = true)
@Composable
private fun ReceiveManualPreview() {
    MorseLightTheme {
        ReceiveContent(
            ui = ReceiveUiState(mode = RxMode.Manual, decoded = "SOS", buffer = "... --- ..."),
            onSetMode = {}, onReset = {}, onSensitivity = {}, onDetectionArea = {},
            onKeyDown = {}, onKeyUp = {}, cameraContent = {},
        )
    }
}
