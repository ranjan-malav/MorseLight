package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

/** One torch on/off edge, timed in morse units. [symbolIndex] is the position in the morse string. */
data class SymbolEvent(val atUnit: Int, val on: Boolean, val symbolIndex: Int)

/**
 * Pure playback plan for a morse string (redesign transmit engine, §7.3), in ITU-R M.1677 units:
 * dot = 1 unit on, dash = 3 units on, +1 unit intra-character gap after each element, character
 * gap = 3 units total, word gap = 7 units total. A coroutine ticker (Phase 4 ViewModel) walks this
 * at `unitMillis(wpm)`, driving the torch and the three-state symbol colouring.
 */
data class MorseTimeline(val events: List<SymbolEvent>, val totalUnits: Int) {
    companion object {
        fun of(morse: String): MorseTimeline {
            val out = ArrayList<SymbolEvent>()
            var t = 0
            morse.forEachIndexed { i, ch ->
                when (ch) {
                    '.', '-' -> {
                        out.add(SymbolEvent(t, on = true, symbolIndex = i))
                        t += if (ch == '.') 1 else 3
                        out.add(SymbolEvent(t, on = false, symbolIndex = i))
                        t += 1 // intra-character gap
                    }
                    ' ' -> t += 2  // + the trailing intra-gap above = 3-unit character gap
                    '/' -> t += 2  // ' ' + '/' + ' ' around it = 7-unit word gap
                }
            }
            return MorseTimeline(out, t)
        }
    }
}

/** Milliseconds per morse unit at the given words-per-minute (1200 / wpm, per ITU-R M.1677). */
fun unitMillis(wpm: Int): Double = 1200.0 / wpm
