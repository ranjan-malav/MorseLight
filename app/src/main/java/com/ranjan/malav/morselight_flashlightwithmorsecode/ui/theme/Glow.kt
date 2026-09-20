package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Coloured halo behind a lit element (torch/key disc, filled primary) — the design's
 * `0 0 46px -6px var(--accent)` / `--shadow-accent` glow, which M3 elevation cannot express.
 * Draws a soft radial wash just outside the content bounds; pair with the element's own shape.
 */
fun Modifier.accentGlow(
    color: Color,
    radius: Dp = 46.dp,
    enabled: Boolean = true,
): Modifier = if (!enabled) this else this
    .padding(radius / 3)
    .drawBehind {
        val r = size.maxDimension / 2f + radius.toPx()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color, Color.Transparent),
                center = Offset(size.width / 2f, size.height / 2f),
                radius = r,
            ),
            radius = r,
            center = Offset(size.width / 2f, size.height / 2f),
        )
    }
