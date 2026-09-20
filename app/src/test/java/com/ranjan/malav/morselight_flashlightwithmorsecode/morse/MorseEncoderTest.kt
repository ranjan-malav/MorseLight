package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Locks the encoder behaviour that was previously copy-pasted across four screens (bug B1).
 * Expected values are hand-derived from the morse tables and the 3/speed unit model.
 */
class MorseEncoderTest {

    @Test
    fun singleLetterE_atDefaultSpeed() {
        // 'E' -> units "13", trailing gap unit stripped -> "1"; morse ". "
        val tx = MorseEncoder.encode(listOf('E'), speed = 3)
        assertEquals(". ", tx.morseCode)
        assertEquals(arrayListOf(1), tx.charUnits)
        assertEquals(arrayListOf(0L), tx.onOffDelays)
        assertEquals(1000L, tx.finalOffDelay) // 1 unit * 1000ms * (3/3)
    }

    @Test
    fun sos_atDefaultSpeed() {
        val tx = MorseEncoder.encode(listOf('S', 'O', 'S'), speed = 3)
        assertEquals("... --- ... ", tx.morseCode)
        // per-char units with +3 inter-character gaps folded in: 5+3, 11+3, 5
        assertEquals(arrayListOf(8, 14, 5), tx.charUnits)
        assertEquals(17, tx.onOffDelays.size)
        assertEquals(0L, tx.onOffDelays.first())
        assertEquals(26000L, tx.onOffDelays.last())
        assertEquals(27000L, tx.finalOffDelay) // total 27 units at 1s/unit
    }

    @Test
    fun sos_cumulativeDelaysAreMonotonicAndScheduledBeforeEachUnit() {
        val tx = MorseEncoder.encode(listOf('S', 'O', 'S'), speed = 3)
        val expected = listOf(
            0L, 1000, 2000, 3000, 4000, 5000, 8000, 11000, 12000,
            15000, 16000, 19000, 22000, 23000, 24000, 25000, 26000
        )
        assertEquals(expected, tx.onOffDelays.toList())
    }

    @Test
    fun speedScalesTimingsInverselyButNotMorseText() {
        val fast = MorseEncoder.encode(listOf('E'), speed = 6) // 3/6 = 0.5s per unit
        assertEquals(". ", fast.morseCode)
        assertEquals(500L, fast.finalOffDelay)
    }

    @Test
    fun spaceUsesWordGapAndDoesNotPadPrecedingChar() {
        // 'E',' ','E' -> units "171", morse ". / . "; space char does NOT add the +3 char gap
        val tx = MorseEncoder.encode(listOf('E', ' ', 'E'), speed = 3)
        assertEquals(". / . ", tx.morseCode)
        assertEquals(arrayListOf(1, 7, 1), tx.charUnits)
        assertEquals(arrayListOf(0L, 1000L, 8000L), tx.onOffDelays)
        assertEquals(9000L, tx.finalOffDelay)
    }
}
