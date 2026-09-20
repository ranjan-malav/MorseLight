package com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ranjan.malav.morselight_flashlightwithmorsecode.morse.charToMorse
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.CardSurface
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.JetBrainsMono
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseRadius
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme

private val CHART = ('A'..'Z').toList() + ('0'..'9').toList()

@Composable
fun ReferenceChartScreen(modifier: Modifier = Modifier) {
    val c = MorseTheme.colors
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        val q = query.trim().uppercase()
        CHART.filter { ch ->
            q.isEmpty() || ch.toString() == q || (charToMorse[ch]?.trim()?.contains(q) == true)
        }
    }
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search character or code") }, singleLine = true,
            shape = RoundedCornerShape(MorseRadius.field),
        )
        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered) { ch ->
                CardSurface(radius = MorseRadius.tile) {
                    androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth()) {
                        Text(ch.toString(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = c.textHeading, modifier = Modifier.weight(1f))
                        Text(charToMorse[ch]?.trim() ?: "", color = c.accent,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = JetBrainsMono))
                    }
                }
            }
        }
    }
}
