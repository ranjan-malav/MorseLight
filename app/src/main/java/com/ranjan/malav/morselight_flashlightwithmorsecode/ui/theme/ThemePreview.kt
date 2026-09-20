package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Foundation preview: proves the Personal UI tokens, fonts, and the three-state morse span
// colouring compile and render. Not wired into the running app yet (that is Phase 4).
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = 0x20)
@Composable
private fun DesignSystemPreview() {
    MorseLightTheme {
        val c = MorseTheme.colors
        Surface(color = c.bgApp) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("MORSELIGHT", style = EyebrowStyle, color = c.textSubtle)
                Text("Send", style = androidx.compose.material3.MaterialTheme.typography.displaySmall, color = c.textHeading)

                // Sunken morse card with three-state per-symbol colouring: sent / current / pending.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(c.surfaceSunken, RoundedCornerShape(MorseRadius.card))
                        .padding(16.dp),
                ) {
                    Text("MORSE", style = EyebrowStyle, color = c.textSubtle)
                    Text(threeStateMorse(c, ".... . .-.. .-.. ---", currentIndex = 6))
                }

                Button(onClick = {}) { Text("Send") }
            }
        }
    }
}

private fun threeStateMorse(c: MorseColors, morse: String, currentIndex: Int) =
    buildAnnotatedString {
        morse.forEachIndexed { i, ch ->
            val color = when {
                i < currentIndex -> c.morseSent
                i == currentIndex -> c.morseCurrent
                else -> c.morsePending
            }
            withStyle(SpanStyle(color = color, fontFamily = JetBrainsMono)) { append(ch) }
        }
    }
