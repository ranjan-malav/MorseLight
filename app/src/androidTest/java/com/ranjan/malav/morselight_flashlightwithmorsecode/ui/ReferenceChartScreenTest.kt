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

        // A (".-") is the first tile, so it's on screen on any display size. (Don't assert a
        // later letter like S here — in the LazyVerticalGrid it may be scrolled off on a short
        // screen and never composed, which is what broke this test on the CI emulator.)
        compose.onNodeWithText(".-").assertIsDisplayed() // A

        // Searching filters the grid, bringing the match to the top where it's visible.
        compose.onNodeWithText("Search character or code").performTextInput("S")
        compose.onNodeWithText("...").assertIsDisplayed() // S
    }
}
