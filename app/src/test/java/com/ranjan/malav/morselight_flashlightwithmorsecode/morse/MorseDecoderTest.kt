package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

import org.junit.Assert.assertEquals
import org.junit.Test

class MorseDecoderTest {

    @Test
    fun decryptMorse_decodesSpaceSeparatedTokens() {
        assertEquals("SOS", MorseDecoder.decryptMorse("... --- ..."))
        assertEquals("E", MorseDecoder.decryptMorse("."))
    }

    @Test
    fun decryptMorse_slashTokenIsWordSpace() {
        // "/" maps back to a space between words
        assertEquals("E E", MorseDecoder.decryptMorse(". / ."))
    }

    @Test
    fun decryptMorse_unknownTokenBecomesQuestionMark() {
        assertEquals("?", MorseDecoder.decryptMorse("........"))
    }

    @Test
    fun getMorseForMessage_concatenatesTableEntries() {
        assertEquals("... --- ... ", MorseDecoder.getMorseForMessage(arrayListOf('S', 'O', 'S')))
    }

    @Test
    fun encodeThenDecodeMorseText_roundTrips() {
        // encoder morse output, trimmed, decodes back to the original text
        val tx = MorseEncoder.encode(listOf('H', 'I'), speed = 3)
        assertEquals("HI", MorseDecoder.decryptMorse(tx.morseCode.trim()))
    }
}
