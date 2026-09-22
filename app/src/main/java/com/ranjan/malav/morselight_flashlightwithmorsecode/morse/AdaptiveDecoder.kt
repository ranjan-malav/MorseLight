package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

/**
 * Speed-agnostic manual-key decoder. The sender may key at any speed; the receiver just taps (or the
 * camera pulses) in time with the light. Rather than a fixed WPM threshold, this records the raw
 * press and gap durations and, on every update, classifies marks into dots/dashes purely from their
 * own distribution — dash ≈ 3× dot — and gaps into intra-character / character / word by the same
 * relative scale.
 *
 * When every mark is a single cluster (a lone tap, or several equal taps) dot vs dash is genuinely
 * ambiguous, so the character is reported as both readings, e.g. a single mark is `"E/T"`, two are
 * `"I/M"`. Everything is recomputed from the stored timings each call, so the reading self-corrects
 * live as more of the message arrives (a lone `E/T` resolves to `E` the moment a dash appears).
 */
class AdaptiveDecoder {

    private data class Mark(val dur: Long, val gapBefore: Long)

    private val marks = mutableListOf<Mark>()
    private var downAt: Long? = null
    private var lastUp: Long? = null
    private var pendingGap = -1L

    val hasInput: Boolean get() = marks.isNotEmpty()

    fun onDown(nowMs: Long) {
        downAt = nowMs
        pendingGap = lastUp?.let { nowMs - it } ?: -1L
    }

    fun onUp(nowMs: Long) {
        val d = downAt ?: return
        marks += Mark(dur = (nowMs - d).coerceAtLeast(1L), gapBefore = pendingGap)
        downAt = null
        lastUp = nowMs
        pendingGap = -1L
    }

    fun reset() {
        marks.clear(); downAt = null; lastUp = null; pendingGap = -1L
    }

    /** Raw dot/dash view: '.'/'-' when resolved, '?' when ambiguous, with gap separators. */
    val morse: String
        get() {
            if (marks.isEmpty()) return ""
            val thr = dotThresholdOrNull()
            val u = unitEstimate(thr)
            return buildString {
                marks.forEachIndexed { i, m ->
                    if (i > 0) append(separator(m.gapBefore, u))
                    append(if (thr == null) '?' else if (m.dur < thr) '.' else '-')
                }
            }
        }

    /** Decoded text; an ambiguous character renders as "dot-reading/dash-reading", e.g. "E/T". */
    val text: String
        get() {
            if (marks.isEmpty()) return ""
            val thr = dotThresholdOrNull()
            val u = unitEstimate(thr)
            val sb = StringBuilder()
            var charMarks = mutableListOf<Mark>()
            marks.forEachIndexed { i, m ->
                if (i > 0) when (separator(m.gapBefore, u)) {
                    " " -> { sb.append(decodeChar(charMarks, thr)); charMarks = mutableListOf() }
                    " / " -> {
                        sb.append(decodeChar(charMarks, thr)); sb.append(' '); charMarks = mutableListOf()
                    }
                }
                charMarks.add(m)
            }
            sb.append(decodeChar(charMarks, thr))
            return sb.toString().trim()
        }

    // --- classification helpers ---

    /** Split point between dots and dashes, or null when the marks are a single cluster. */
    private fun dotThresholdOrNull(): Double? {
        if (marks.isEmpty()) return null
        val mn = marks.minOf { it.dur }.toDouble()
        val mx = marks.maxOf { it.dur }.toDouble()
        // Dash is ~3× a dot; require clear separation before we trust a split.
        return if (mx >= mn * RESOLVE_RATIO) (mn + mx) / 2.0 else null
    }

    /** Best estimate of one time unit (a dot). Uses the dot cluster, or the shortest mark. */
    private fun unitEstimate(threshold: Double?): Double {
        if (marks.isEmpty()) return 1.0
        val durs = marks.map { it.dur.toDouble() }
        return if (threshold != null) durs.filter { it < threshold }.ifEmpty { listOf(durs.min()) }.average()
        else durs.min()
    }

    private fun separator(gap: Long, unit: Double): String = when {
        gap < 0 -> ""
        gap < 2 * unit -> ""      // within a character
        gap < 5 * unit -> " "      // between characters (~3 units)
        else -> " / "              // between words (~7 units)
    }

    private fun decodeChar(charMarks: List<Mark>, threshold: Double?): String {
        if (charMarks.isEmpty()) return ""
        if (threshold == null) {
            // One cluster: every mark is the same length, so the character is all dots OR all dashes.
            val dots = ".".repeat(charMarks.size)
            val dashes = "-".repeat(charMarks.size)
            val a = morseToChar[dots]
            val b = morseToChar[dashes]
            return when {
                a != null && b != null && a != b -> "$a/$b"
                a != null -> a
                b != null -> b
                else -> MorseCode.UNKNOWN
            }
        }
        val code = charMarks.joinToString("") { if (it.dur < threshold) "." else "-" }
        return morseToChar[code] ?: MorseCode.UNKNOWN
    }

    private companion object {
        const val RESOLVE_RATIO = 1.8
    }
}
