package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Foundation-only preview: proves Compose + the brand theme compile and render.
// Not wired into the running app yet (fragment UI is still live until Phase 4).
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = 0x20)
@Composable
private fun ThemeShowcasePreview() {
    MorseLightTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("MorseLight", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
                Text("Send · Receive · Learn", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                Button(onClick = {}) { Text("Start") }
            }
        }
    }
}
