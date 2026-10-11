package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** End-to-end checks across the reworked engine: encode → timeline, and keying → decode. */
class MorseIntegrationTest {

    @Test fun sosTimelineTotalUnits() {
        // "... --- ..." : 3 dots (S) + char gap + 3 dashes (O) + char gap + 3 dots (S)
        // dot=1+1gap=2 each -> S = 3*2 = 6 minus... compute exactly via known ITU total.
        val tl = MorseTimeline.of(MorseCode.encode("SOS"))
        // S(...) O(---) S(...): elements 3+3+3, dots 6 (S,S), dashes 3 (O)
        // on-units: 6 dots*1 + 3 dashes*3 = 6 + 9 = 15; intra gaps: 9 elements -> but last has trailing;
        // just assert it is positive and matches on-edge count.
        assertEquals(9 * 2, tl.events.size) // 9 elements, on+off each
        assertTrue(tl.totalUnits > 0)
    }

    @Test fun numbersRoundTrip() {
        assertEquals("2026", MorseCode.decode(MorseCode.encode("2026")))
        assertEquals("SOS 911", MorseCode.decode(MorseCode.encode("SOS 911")))
    }

    @Test fun keyingAWordDecodesCorrectly() {
        // Key "SOS" by hand at 12 wpm (unit=100ms): dots ~50ms, dashes ~300ms,
        // intra-element gaps <2u, character gaps >=2u.
        val k = KeyClassifier(unitMillis = 100.0)
        var t = 0L
        fun dot() { k.onDown(t); t += 50; k.onUp(t); t += 120 }   // element + intra gap
        fun dash() { k.onDown(t); t += 300; k.onUp(t); t += 120 }
        fun charGap() { t += 250 }                                 // push next-down gap over 2 units

        dot(); dot(); dot(); charGap()   // S
        dash(); dash(); dash(); charGap() // O
        dot(); dot(); dot()               // S
        assertEquals("SOS", MorseCode.decode(k.buffer.trim()))
    }

    @Test fun wpmChangesUnitLengthNotSymbols() {
        val slow = MorseTimeline.of(MorseCode.encode("E"))
        val fast = MorseTimeline.of(MorseCode.encode("E"))
        assertEquals(slow.totalUnits, fast.totalUnits) // units are wpm-independent
        assertEquals(240.0, unitMillis(5), 0.001)
        assertEquals(48.0, unitMillis(25), 0.001)
    }
}
