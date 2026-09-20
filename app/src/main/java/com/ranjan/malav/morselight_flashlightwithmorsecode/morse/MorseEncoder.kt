package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

/**
 * Encodes a message into the flash-timing schedule used by every transmit path.
 *
 * Before Phase 2 this block was copy-pasted, with small divergences, into SendFragment,
 * ManualDecodeFragment, AutoDecodeFragment and MorseTutorialActivity (audit bug B1). It is
 * reproduced here verbatim so behaviour is identical; the callers now delegate to [encode].
 *
 * Speed model (unchanged): speed 1..10, one unit = 3/speed seconds. Speed 3 => 1 unit = 1 s.
 */
object MorseEncoder {

    /**
     * @param onOffDelays  offset in ms from the start of transmission for each on/off edge
     * @param charUnits    units occupied by each character, with the +3 inter-character gap folded in
     * @param morseCode    dots/dashes for the whole message, e.g. "... --- ... "
     * @param finalOffDelay total transmission length in ms (when the flash is switched off for good)
     */
    data class Transmission(
        val onOffDelays: ArrayList<Long>,
        val charUnits: ArrayList<Int>,
        val morseCode: String,
        val finalOffDelay: Long,
    )

    fun encode(message: List<Char>, speed: Int): Transmission {
        // one unit = 3/speed seconds
        val transmissionSpeed: Float = 3f / speed
        val timeUnits = StringBuilder()
        val morseCode = StringBuilder()
        val charUnits = arrayListOf<Int>()

        var index = 0
        for (char in message) {
            if (char == ' ') {
                timeUnits.replace(timeUnits.length - 1, timeUnits.length, "")
            }
            timeUnits.append(charToUnits[char])
            morseCode.append(charToMorse[char])
            if (charUnits.isNotEmpty()) {
                if (char == ' ') {
                    charUnits.add(charToTotalUnits[char]!!)
                } else {
                    charUnits[index - 1] = charUnits[index - 1] + 3
                    charUnits.add(charToTotalUnits[char]!!)
                }
            } else {
                charUnits.add(charToTotalUnits[char]!!)
            }
            index++
        }
        // Remove the trailing inter-character gap unit added after the last character.
        timeUnits.replace(timeUnits.length - 1, timeUnits.length, "")

        var delay = 0L
        val onOffDelays = arrayListOf<Long>()
        for (i in timeUnits.indices) {
            onOffDelays.add((delay * 1000 * transmissionSpeed).toLong())
            val unit = timeUnits[i].toString().toInt()
            delay += unit
        }

        return Transmission(
            onOffDelays = onOffDelays,
            charUnits = charUnits,
            morseCode = morseCode.toString(),
            finalOffDelay = (delay * 1000 * transmissionSpeed).toLong(),
        )
    }
}
