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
 * sent = success, current = accent (held through the off-gap), pending = subtle. Idle (not
 * transmitting) paints the whole string in the body colour. [currentIndex]/[doneIndex] are symbol
 * positions in [morse], as emitted by TransmitState.
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
        if (!transmitting) {
            withStyle(SpanStyle(color = c.morseIdle)) { append(morse) }
        } else {
            morse.forEachIndexed { i, ch ->
                val color = when {
                    i <= doneIndex -> c.morseSent
                    i == currentIndex -> c.morseCurrent
                    else -> c.morsePending
                }
                withStyle(SpanStyle(color = color)) { append(ch) }
            }
        }
    }
    Text(text = text, style = MorseTextStyle, modifier = modifier)
}
