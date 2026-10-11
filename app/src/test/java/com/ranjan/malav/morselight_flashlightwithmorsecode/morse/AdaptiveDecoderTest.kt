package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

import org.junit.Assert.assertEquals
import org.junit.Test

class AdaptiveDecoderTest {

    /** Feed (markDuration, gapBeforeThisMark) pairs; the first mark's gap is ignored. */
    private fun decode(vararg marks: Pair<Long, Long>): AdaptiveDecoder {
        val d = AdaptiveDecoder()
        var lastUp = 0L
        marks.forEachIndexed { i, (dur, gap) ->
            val down = if (i == 0) 0L else lastUp + gap
            d.onDown(down)
            lastUp = down + dur
            d.onUp(lastUp)
        }
        return d
    }

    @Test fun singleTapIsAmbiguousEorT() {
        val d = decode(50L to 0L)
        assertEquals("E/T", d.text)
        assertEquals("?", d.morse)
    }

    @Test fun twoEqualTapsAreIorM() {
        val d = decode(50L to 0L, 50L to 50L)
        assertEquals("I/M", d.text)
    }

    @Test fun sosResolvesFromItsOwnTimings() {
        // dot 50, dash 150, intra-gap 50, char-gap 150
        val d = decode(
            50L to 0L, 50L to 50L, 50L to 50L,        // S
            150L to 150L, 150L to 50L, 150L to 50L,   // O
            50L to 150L, 50L to 50L, 50L to 50L,      // S
        )
        assertEquals("... --- ...", d.morse)
        assertEquals("SOS", d.text)
    }

    @Test fun sameMessageAtHalfSpeedStillDecodes() {
        // everything doubled — a slower sender; the decoder must adapt with no WPM.
        val d = decode(
            100L to 0L, 100L to 100L, 100L to 100L,   // S
            300L to 300L, 300L to 100L, 300L to 100L, // O
            100L to 300L, 100L to 100L, 100L to 100L, // S
        )
        assertEquals("SOS", d.text)
    }

    @Test fun aLongFirstMarkIsNotAssumedDash() {
        // "TE": a dash then a dot. The long mark is only a dash *relative* to the short one.
        val d = decode(150L to 0L, 50L to 150L)
        assertEquals("- .", d.morse)
        assertEquals("TE", d.text) // one word, two characters

    }

    @Test fun resetClears() {
        val d = decode(50L to 0L, 50L to 50L)
        d.reset()
        assertEquals("", d.text)
        assertEquals("", d.morse)
    }
}
