package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTextStyle
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme

/**
 * The morse dot-dash string with three-state per-symbol colouring (README §7.2):
 * sent = success, current = accent (held through the off-gap), pending = subtle. Idle paints the
 * whole string in the body colour.
 *
 * The annotated-string structure is identical in both states — one [SpanStyle] per character
 * regardless of transmitting — so the layout never shifts when colours change (a per-character span
 * applies the style's letterSpacing differently than a single whole-string span would).
 */
@Composable
fun MorseString(
    morse: String,
    modifier: Modifier = Modifier,
    transmitting: Boolean = false,
    currentIndex: Int = -1,
    doneIndex: Int = -1,
) {
    val c = MorseTheme.colors
    val text = buildAnnotatedString {
        morse.forEachIndexed { i, ch ->
            val color = when {
                !transmitting -> c.morseIdle
                i <= doneIndex -> c.morseSent
                i == currentIndex -> c.morseCurrent
                else -> c.morsePending
            }
            withStyle(SpanStyle(color = color)) { append(ch) }
        }
    }
    Text(text = text, style = MorseTextStyle, modifier = modifier)
}
