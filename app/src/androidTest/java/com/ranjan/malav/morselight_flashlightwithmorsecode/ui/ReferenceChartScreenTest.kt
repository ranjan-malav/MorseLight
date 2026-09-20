package com.ranjan.malav.morselight_flashlightwithmorsecode.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens.ReferenceChartScreen
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseLightTheme
import org.junit.Rule
import org.junit.Test

class ReferenceChartScreenTest {

    @get:Rule val compose = createComposeRule()

    @Test fun showsLettersAndFiltersBySearch() {
        compose.setContent { MorseLightTheme { ReferenceChartScreen() } }

        // A morse code for S is "..." and for A is ".-"; both present initially.
        compose.onNodeWithText("...").assertIsDisplayed() // S
        compose.onNodeWithText(".-").assertIsDisplayed()  // A

        // Search narrows to a single character.
        compose.onNodeWithText("Search character or code").performTextInput("Z")
        compose.onNodeWithText("--..").assertIsDisplayed() // Z
    }
}
