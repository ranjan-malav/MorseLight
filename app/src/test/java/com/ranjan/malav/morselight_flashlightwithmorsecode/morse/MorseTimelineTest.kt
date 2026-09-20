package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

import org.junit.Assert.assertEquals
import org.junit.Test

class MorseTimelineTest {

    @Test fun unitMillisFollowsItuFormula() {
        assertEquals(100.0, unitMillis(12), 0.0001) // 1200/12
        assertEquals(60.0, unitMillis(20), 0.0001)
        assertEquals(240.0, unitMillis(5), 0.0001)
    }

    @Test fun singleDotIsOneUnitOnThenGap() {
        val tl = MorseTimeline.of(".")
        assertEquals(listOf(SymbolEvent(0, true, 0), SymbolEvent(1, false, 0)), tl.events)
        assertEquals(2, tl.totalUnits) // 1 on + 1 intra-gap
    }

    @Test fun dashIsThreeUnitsOn() {
        val tl = MorseTimeline.of("-")
        assertEquals(listOf(SymbolEvent(0, true, 0), SymbolEvent(3, false, 0)), tl.events)
        assertEquals(4, tl.totalUnits)
    }

    @Test fun characterGapIsThreeUnits() {
        // "E E" -> dot, char-gap, dot. Silent gap between the two elements must be 3 units.
        val tl = MorseTimeline.of(". .")
        val firstOff = tl.events.first { !it.on }
        val secondOn = tl.events.first { it.on && it.symbolIndex == 2 }
        assertEquals(3, secondOn.atUnit - firstOff.atUnit)
    }

    @Test fun wordGapIsSevenUnits() {
        // "E / E" -> dot, word-gap, dot. Silent gap between the two elements must be 7 units.
        val tl = MorseTimeline.of(". / .")
        val firstOff = tl.events.first { !it.on }
        val secondOn = tl.events.first { it.on && it.symbolIndex > 0 }
        assertEquals(7, secondOn.atUnit - firstOff.atUnit)
    }

    @Test fun eventsCarrySymbolIndexForColouring() {
        val tl = MorseTimeline.of("...")
        // three on-edges at symbol indices 0,1,2
        assertEquals(listOf(0, 1, 2), tl.events.filter { it.on }.map { it.symbolIndex })
    }
}
