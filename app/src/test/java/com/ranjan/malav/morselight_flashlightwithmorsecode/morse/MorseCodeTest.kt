package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

import org.junit.Assert.assertEquals
import org.junit.Test

class MorseCodeTest {

    @Test fun encodesSingleWord() {
        assertEquals(".... ..", MorseCode.encode("HI"))
        assertEquals("... --- ...", MorseCode.encode("SOS"))
    }

    @Test fun encodesWordsWithSlashSeparator() {
        // H I / Y O U
        assertEquals(".... .. / -.-- --- ..-", MorseCode.encode("hi you"))
    }

    @Test fun encodeIsCaseAndWhitespaceInsensitive() {
        assertEquals(".... ..", MorseCode.encode("  Hi  "))
        assertEquals(".... .. / -.-- --- ..-", MorseCode.encode("Hi   You"))
    }

    @Test fun decodesBackToText() {
        assertEquals("SOS", MorseCode.decode("... --- ..."))
        assertEquals("HI YOU", MorseCode.decode(".... .. / -.-- --- ..-"))
    }

    @Test fun unknownTokenBecomesInterpunct() {
        assertEquals(MorseCode.UNKNOWN, MorseCode.decode("........"))
    }

    @Test fun roundTrips() {
        val text = "MEET AT NINE"
        assertEquals(text, MorseCode.decode(MorseCode.encode(text)))
    }
}
