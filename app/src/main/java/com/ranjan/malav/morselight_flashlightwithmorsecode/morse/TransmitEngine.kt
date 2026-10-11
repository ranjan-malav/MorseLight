package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

/** Live transmit state consumed by the Send screen (torch + three-state colouring + progress). */
data class TransmitState(
    val running: Boolean = false,
    val torchOn: Boolean = false,
    val symbolIndex: Int = -1, // current symbol in the morse string (accent)
    val doneIndex: Int = -1,   // last completed symbol (success)
    val percent: Float = 0f,
)

/**
 * Coroutine ticker that walks a [MorseTimeline] at `unitMillis(wpm)`, emitting [TransmitState] and
 * toggling the torch (redesign transmit engine, §7.3). Replaces the old Handler chain. Cancelling
 * the coroutine stops playback; the torch is switched off in the finally block either way.
 */
class TransmitEngine(
    private val setTorch: (Boolean) -> Unit,
    private val onState: (TransmitState) -> Unit,
) {
    /** Suspends until the message finishes or the coroutine is cancelled. */
    suspend fun transmit(morse: String, wpm: Int) {
        val timeline = MorseTimeline.of(morse)
        if (timeline.events.isEmpty()) return
        val unit = unitMillis(wpm)
        val totalMs = timeline.totalUnits * unit
        val startNs = System.nanoTime()
        var torchState = false
        try {
            while (coroutineContext.isActive) {
                val elapsedMs = (System.nanoTime() - startNs) / 1_000_000.0
                if (elapsedMs >= totalMs) break
                val elapsedUnits = elapsedMs / unit

                // The most recent edge at or before now (-1 before the first edge).
                var edge = -1
                for (i in timeline.events.indices) {
                    if (timeline.events[i].atUnit <= elapsedUnits) edge = i else break
                }
                val e = timeline.events.getOrNull(edge)
                val on = e?.on == true
                if (on != torchState) {
                    torchState = on
                    setTorch(on)
                }
                onState(
                    TransmitState(
                        running = true,
                        torchOn = on,
                        // current symbol held through its off-gap so the accent stays visible
                        symbolIndex = e?.symbolIndex ?: -1,
                        doneIndex = when {
                            e == null -> -1
                            on -> e.symbolIndex - 1
                            else -> e.symbolIndex
                        },
                        percent = (elapsedMs / totalMs).coerceIn(0.0, 1.0).toFloat(),
                    )
                )
                delay(16)
            }
        } finally {
            setTorch(false)
            onState(TransmitState(running = false))
        }
    }
}
