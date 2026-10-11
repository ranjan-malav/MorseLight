package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

/**
 * Classifies manual key press-and-hold into dots/dashes and gap separators (redesign logic, §7.3),
 * replacing the old timing-difference clustering. All timing is relative to one morse unit.
 *
 * Down: the gap since the last release inserts a word (`" / "`, ≥6 units) or character (`" "`, ≥2)
 * separator. Up: press duration < 2 units is a dot, else a dash. The owning ViewModel additionally
 * schedules [commitCharacterGap] at 3.5 units and [commitWordGap] at 8 units of idle to close out a
 * character/word when the sender stops keying. [buffer] holds the raw dot-dash string for decode().
 */
class KeyClassifier(private val unitMillis: Double) {

    var buffer: String = ""
        private set

    private var downAt: Long? = null
    private var lastUp: Long? = null

    fun onDown(nowMs: Long) {
        lastUp?.let { up ->
            val gap = (nowMs - up) / unitMillis
            buffer += when {
                gap >= 6 -> " / "
                gap >= 2 -> " "
                else -> ""
            }
        }
        downAt = nowMs
    }

    /** Returns the element committed ('.' or '-'), or null if there was no matching down. */
    fun onUp(nowMs: Long): Char? {
        val down = downAt ?: return null
        val dur = (nowMs - down) / unitMillis
        downAt = null
        lastUp = nowMs
        val element = if (dur < 2) '.' else '-'
        buffer += element
        return element
    }

    /** Idle ≥3.5 units since release: close the character with a space. */
    fun commitCharacterGap() {
        if (buffer.isNotEmpty() && !buffer.endsWith(" ") && !buffer.endsWith("/ ")) {
            buffer += " "
        }
        lastUp = null
    }

    /** Idle ≥8 units since release: close the word with a separator. */
    fun commitWordGap() {
        if (Regex("[^/] $").containsMatchIn(buffer)) {
            buffer += "/ "
        }
    }

    fun reset() {
        buffer = ""
        downAt = null
        lastUp = null
    }
}
