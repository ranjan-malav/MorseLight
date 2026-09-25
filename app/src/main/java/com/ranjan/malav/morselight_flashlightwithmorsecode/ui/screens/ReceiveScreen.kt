package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.annotation.StringRes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.torch.TorchController
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.CameraPreview
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SegmentedControl
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.Eyebrow
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.KeepScreenOn
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SoftPill
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.PillTone
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SunkenCard
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.TorchDisc
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.LabeledSlider
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.JetBrainsMono
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseRadius
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseLightTheme
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ReceiveScreen(
    vm: ReceiveViewModel,
    torch: TorchController,
    helpOpen: Boolean = false,
    onHelpOpenChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var camGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> camGranted = granted }

    // Ask for CAMERA the first time the camera tab is opened (not at app launch).
    LaunchedEffect(ui.mode) {
        if (ui.mode == RxMode.Camera && !camGranted) permLauncher.launch(Manifest.permission.CAMERA)
    }
    // Keep the screen awake the whole time the Receive screen is open (honouring the preference) —
    // decoding is passive, so the user isn't touching the screen to keep it alive.
    KeepScreenOn(ui.keepAwake)

    ReceiveContent(
        ui = ui,
        onSetMode = vm::setMode, onReset = vm::reset,
        onSensitivity = vm::setSensitivity, onDetectionArea = vm::setDetectionArea,
        onCalibrate = vm::recalibrate,
        onKeyDown = vm::keyDown, onKeyUp = vm::keyUp,
        cameraPermissionGranted = camGranted,
        onRequestCameraPermission = { permLauncher.launch(Manifest.permission.CAMERA) },
        cameraContent = { CameraPreview(torch, Modifier.fillMaxSize()) },
        helpOpen = helpOpen,
        onHelpOpenChange = onHelpOpenChange,
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
    onCalibrate: () -> Unit,
    onKeyDown: () -> Unit,
    onKeyUp: () -> Unit,
    cameraContent: @Composable () -> Unit,
    cameraPermissionGranted: Boolean = true,
    onRequestCameraPermission: () -> Unit = {},
    helpOpen: Boolean = false,
    onHelpOpenChange: (Boolean) -> Unit = {},
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
                    style = MaterialTheme.typography.titleMedium,
                    color = if (ui.decoded.isBlank()) c.textSubtle else c.textHeading,
                )
                Text(
                    ui.buffer.trim().ifBlank { "—" },
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = JetBrainsMono),
                    color = c.textMuted,
                )
            }
        }

        if (ui.mode == RxMode.Camera && !cameraPermissionGranted) {
            // Ask for CAMERA before showing the viewfinder — it's only needed for camera decode.
            SunkenCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.camera_permission_title),
                        style = MaterialTheme.typography.titleMedium, color = c.textHeading)
                    Text(stringResource(R.string.camera_permission_body),
                        style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
                    SoftPill(stringResource(R.string.action_allow_camera), onRequestCameraPermission, tone = PillTone.Accent)
                }
            }
        } else if (ui.mode == RxMode.Camera) {
            // Live viewfinder with a square detection region, status label and a Calibrate button.
            // It takes the flexible space (weight) so it grows on tall screens and shrinks on short
            // ones — the sliders below always stay on screen. `heightIn` keeps it usable when small.
            Box(
                Modifier.fillMaxWidth().weight(1f).heightIn(min = 120.dp)
                    .clip(RoundedCornerShape(MorseRadius.card))
                    .background(androidx.compose.ui.graphics.Color(0xFF05070B)),
            ) {
                cameraContent()
                // Square detection region, centred, sized as a % of the viewfinder height.
                Box(
                    Modifier.align(Alignment.Center).fillMaxHeight(ui.detectionArea / 100f).aspectRatio(1f)
                        .border(2.dp, c.accent, RoundedCornerShape(10.dp)),
                )
                // Status label + Calibrate button, stacked at the bottom over the preview.
                Column(
                    Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        when {
                            ui.calibrating -> stringResource(R.string.rx_calibrating)
                            !ui.armed -> stringResource(R.string.rx_tap_calibrate)
                            ui.reading -> stringResource(R.string.rx_reading)
                            else -> stringResource(R.string.rx_waiting)
                        },
                        style = MaterialTheme.typography.labelMedium, color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.clip(RoundedCornerShape(percent = 50))
                            .background(androidx.compose.ui.graphics.Color(0x99000000))
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                    )
                    // Aim at the light, then tap to re-measure the ambient baseline.
                    Row(
                        Modifier.clip(RoundedCornerShape(percent = 50)).background(c.accent)
                            .clickable(enabled = !ui.calibrating, onClick = onCalibrate)
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(Icons.Outlined.CenterFocusStrong, contentDescription = null,
                            tint = c.textOnAccent, modifier = Modifier.size(16.dp))
                        Text(
                            if (ui.calibrating) stringResource(R.string.rx_calibrating)
                            else stringResource(R.string.rx_calibrate),
                            style = MaterialTheme.typography.labelMedium, color = c.textOnAccent)
                    }
                }
            }
            // Live brightness: current reading (green while above the threshold) vs the ambient average.
            // Before calibration we only show the guidance hint (no average measured yet).
            if (!ui.armed || ui.calibrating) {
                Text(stringResource(R.string.rx_camera_hint),
                    style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(stringResource(R.string.lum_value, ui.luminance.toInt()),
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = JetBrainsMono),
                        color = if (ui.reading) c.success else c.textMuted)
                    Text(stringResource(R.string.lum_average, ui.average.toInt()),
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = JetBrainsMono),
                        color = c.textSubtle)
                }
            }
            SunkenCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabeledSlider(stringResource(R.string.sensitivity), stringResource(R.string.percent, ui.sensitivity), ui.sensitivity.toFloat(),
                        { onSensitivity(it.toInt()) }, 0f..100f, 0, onHelp = { onHelpOpenChange(true) })
                    LabeledSlider(stringResource(R.string.detection_area), "${ui.detectionArea}", ui.detectionArea.toFloat(),
                        { onDetectionArea(it.toInt()) }, 8f..80f, 0, onHelp = { onHelpOpenChange(true) })
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

    if (helpOpen) ReceiveHelpSheet(onDismiss = { onHelpOpenChange(false) })
}

/** One combined help sheet for the whole camera-decode flow (opened from every help affordance). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReceiveHelpSheet(onDismiss: () -> Unit) {
    val c = MorseTheme.colors
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = c.surfaceCard) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(stringResource(R.string.rx_help_title),
                style = MaterialTheme.typography.titleLarge, color = c.textHeading)
            HelpSection(R.string.calibrate_help_title, R.string.calibrate_help_body)
            HelpSection(R.string.sensitivity_help_title, R.string.sensitivity_help_body)
            HelpSection(R.string.detection_area_help_title, R.string.detection_area_help_body)
        }
    }
}

@Composable
private fun HelpSection(@StringRes titleRes: Int, @StringRes bodyRes: Int) {
    val c = MorseTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(stringResource(titleRes), style = MaterialTheme.typography.titleMedium, color = c.textHeading)
        Text(stringResource(bodyRes), style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
    }
}

@Preview(name = "Receive manual", showBackground = true)
@Composable
private fun ReceiveManualPreview() {
    MorseLightTheme {
        ReceiveContent(
            ui = ReceiveUiState(mode = RxMode.Manual, decoded = "SOS", buffer = "... --- ..."),
            onSetMode = {}, onReset = {}, onSensitivity = {}, onDetectionArea = {},
            onCalibrate = {}, onKeyDown = {}, onKeyUp = {}, cameraContent = {},
        )
    }
}
