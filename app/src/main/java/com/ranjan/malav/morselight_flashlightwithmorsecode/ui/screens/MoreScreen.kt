package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ranjan.malav.morselight_flashlightwithmorsecode.data.Settings
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.BadgeTone
import com.ranjan.malav.morselight_flashlightwithmorsecode.BuildConfig
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.CardSurface
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.StatusBadge
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.Eyebrow
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.SunkenCard
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseRadius
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseLightTheme
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MoreScreen(
    vm: MoreViewModel,
    onOpenDecodingDrill: () -> Unit,
    onOpenSendingDrill: () -> Unit,
    onOpenReferenceChart: () -> Unit,
    onRate: () -> Unit,
    onSource: () -> Unit,
    onDonate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s by vm.state.collectAsStateWithLifecycle()
    MoreContent(
        s = s,
        onKeyTone = vm::setKeyTone, onLoop = vm::setLoop, onKeepAwake = vm::setKeepAwake,
        onOpenDecodingDrill = onOpenDecodingDrill, onOpenSendingDrill = onOpenSendingDrill,
        onOpenReferenceChart = onOpenReferenceChart, onRate = onRate, onSource = onSource,
        onDonate = onDonate, modifier = modifier,
    )
}

@Composable
fun MoreContent(
    s: Settings,
    onKeyTone: (Boolean) -> Unit,
    onLoop: (Boolean) -> Unit,
    onKeepAwake: (Boolean) -> Unit,
    onOpenDecodingDrill: () -> Unit,
    onOpenSendingDrill: () -> Unit,
    onOpenReferenceChart: () -> Unit,
    onRate: () -> Unit,
    onSource: () -> Unit,
    onDonate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MorseTheme.colors

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Progress card (accent tint, no border per the tinted-card rule)
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(MorseRadius.card)).background(c.accentSoft)
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(progress = { 12f / 36f }, modifier = Modifier.size(64.dp),
                        color = c.accent, trackColor = c.surfaceCard)
                    Text("12", style = MaterialTheme.typography.titleMedium, color = c.accentOnSoft)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.progress_learned, 12, 36), style = MaterialTheme.typography.titleMedium, color = c.textHeading)
                    Text(stringResource(R.string.progress_lesson), style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
                    StatusBadge(stringResource(R.string.progress_streak, 6), tone = BadgeTone.Success)
                }
            }
        }

        // Donation banner (amber wash) with a coffee mark in a white circle.
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(MorseRadius.card)).background(c.warningSoft)
                .clickable(onClick = onDonate).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(percent = 50)).background(c.surfaceCard),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Coffee, contentDescription = null, tint = c.warning, modifier = Modifier.size(20.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(stringResource(R.string.donate_title), style = MaterialTheme.typography.titleMedium, color = c.warningOnSoft)
                Text(stringResource(R.string.donate_subtitle), style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
            }
        }

        // Learn / practice
        CardSurface(Modifier.fillMaxWidth(), padding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
            Column {
                NavRow(stringResource(R.string.more_decoding_drill), stringResource(R.string.more_decoding_drill_sub), onOpenDecodingDrill)
                HorizontalDivider(color = c.borderSubtle)
                NavRow(stringResource(R.string.more_sending_drill), stringResource(R.string.more_sending_drill_sub), onOpenSendingDrill)
                HorizontalDivider(color = c.borderSubtle)
                NavRow(stringResource(R.string.more_reference_chart), stringResource(R.string.more_reference_chart_sub), onOpenReferenceChart)
            }
        }

        // Preferences
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Eyebrow(stringResource(R.string.pref_section))
            CardSurface(Modifier.fillMaxWidth(), padding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                Column {
                    SwitchRow(stringResource(R.string.pref_key_tone), stringResource(R.string.pref_key_tone_sub), s.keyTone, onKeyTone)
                    HorizontalDivider(color = c.borderSubtle)
                    SwitchRow(stringResource(R.string.pref_loop), stringResource(R.string.pref_loop_sub), s.loop, onLoop)
                    HorizontalDivider(color = c.borderSubtle)
                    SwitchRow(stringResource(R.string.pref_awake), stringResource(R.string.pref_awake_sub), s.keepAwake, onKeepAwake)
                }
            }
        }

        // Support
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Eyebrow(stringResource(R.string.support_section))
            CardSurface(Modifier.fillMaxWidth(), padding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                Column {
                    NavRow(stringResource(R.string.support_rate), stringResource(R.string.support_rate_sub), onRate)
                    HorizontalDivider(color = c.borderSubtle)
                    NavRow(stringResource(R.string.support_source), stringResource(R.string.support_source_sub), onSource)
                }
            }
        }

        SunkenCard(Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            Text(
                stringResource(R.string.about_text, BuildConfig.VERSION_NAME),
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

@Preview(showBackground = true)
@Composable
private fun MorePreview() {
    MorseLightTheme {
        MoreContent(Settings(), {}, {}, {}, {}, {}, {}, {}, {}, {})
    }
}
