package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.EyebrowStyle
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseMotion
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.accentGlow
import androidx.compose.animation.core.tween

/**
 * The 168px torch / key disc — the one large target per screen. When [on], the inner circle fills
 * with the accent and the disc gains a coloured halo (design's `0 0 46px -6px accent`). Attach the
 * press-and-hold gesture to [modifier] at the call site.
 */
@Composable
fun TorchDisc(
    on: Boolean,
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.FlashlightOn,
    contentDescription: String? = null,
) {
    val c = MorseTheme.colors
    val fill by animateColorAsState(
        if (on) c.accent else c.surfaceSunken,
        animationSpec = tween(MorseMotion.durTorch), label = "torchFill",
    )
    val content = if (on) c.textOnAccent else c.textMuted

    Box(modifier = modifier.size(168.dp).accentGlow(c.accentGlow, enabled = on), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(168.dp)
                .background(c.surfaceSunken, CircleShape)
                .border(BorderStroke(1.dp, c.borderSubtle), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier.size(154.dp).background(fill, CircleShape)
                    .semantics { contentDescription?.let { this.contentDescription = it } },
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(40.dp))
                    Text(label.uppercase(), style = EyebrowStyle, color = content, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
