package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

/**
 * WPM-based morse encode/decode (redesign logic, §7.3). Produces the standard ITU string form
 * `.... . .-.. .-.. --- / .-- ---` — words joined by " / ", characters by " ".
 *
 * Reuses the shared tables in MorseTables. Replaces the transmit half of the old MorseEncoder and
 * the timing-cluster guessing in MorseDecoder; those stay until the old fragment UI is removed.
 */
object MorseCode {

    /** Unknown decode token placeholder (interpunct), matching the design prototype. */
    const val UNKNOWN = "·"

    private val whitespace = Regex("\\s+")
    private val wordGap = Regex("\\s*/\\s*")

    fun encode(text: String): String =
        text.uppercase().trim().split(whitespace).filter { it.isNotEmpty() }
            .map { word ->
                word.mapNotNull { c -> charToMorse[c]?.trim()?.takeIf { it.isNotEmpty() } }
                    .joinToString(" ")
            }
            .filter { it.isNotEmpty() }
            .joinToString(" / ")

    fun decode(morse: String): String =
        morse.trim().split(wordGap).joinToString(" ") { word ->
            word.split(whitespace).filter { it.isNotEmpty() }
                .joinToString("") { token -> morseToChar[token] ?: UNKNOWN }
        }.trim()
}
