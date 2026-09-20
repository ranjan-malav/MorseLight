package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

import org.junit.Assert.assertEquals
import org.junit.Test

class KeyClassifierTest {
    // 12 wpm -> 100 ms per unit
    private fun classifier() = KeyClassifier(unitMillis = 100.0)

    @Test fun shortPressIsDot() {
        val k = classifier()
        k.onDown(0); k.onUp(50) // 0.5 units
        assertEquals(".", k.buffer)
    }

    @Test fun longPressIsDash() {
        val k = classifier()
        k.onDown(0); k.onUp(300) // 3 units
        assertEquals("-", k.buffer)
    }

    @Test fun mediumGapInsertsCharacterSeparator() {
        val k = classifier()
        k.onDown(0); k.onUp(50)      // "."  release at 50
        k.onDown(450); k.onUp(500)   // gap = (450-50)/100 = 4 units -> " ", then "."
        assertEquals(". .", k.buffer)
    }

    @Test fun largeGapInsertsWordSeparator() {
        val k = classifier()
        k.onDown(0); k.onUp(50)       // "."  release at 50
        k.onDown(700); k.onUp(750)    // gap = 6.5 units -> " / ", then "."
        assertEquals(". / .", k.buffer)
    }

    @Test fun classifiesLetterS() {
        val k = classifier()
        // three quick dots, tight gaps (< 2 units) -> no separators
        k.onDown(0); k.onUp(50)
        k.onDown(120); k.onUp(170)
        k.onDown(240); k.onUp(290)
        assertEquals("...", k.buffer)
        assertEquals("S", MorseCode.decode(k.buffer))
    }

    @Test fun onUpWithoutDownReturnsNull() {
        assertEquals(null, classifier().onUp(100))
    }

    @Test fun commitCharacterGapClosesLetter() {
        val k = classifier()
        k.onDown(0); k.onUp(50)
        k.commitCharacterGap()
        assertEquals(". ", k.buffer)
    }
}
