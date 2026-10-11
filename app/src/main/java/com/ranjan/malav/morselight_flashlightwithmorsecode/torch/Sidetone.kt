package com.ranjan.malav.morselight_flashlightwithmorsecode.torch

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

/**
 * 620 Hz sine sidetone played while the light is on (design §Sidetone), gated by the Key-tone
 * setting. A short looped buffer keyed on/off by muting, so start/stop is instant.
 */
class Sidetone(frequencyHz: Int = 620) {
    private val sampleRate = 44100
    private val track: AudioTrack
    @Volatile private var enabled = false

    init {
        val samples = sampleRate / frequencyHz // one wavelength
        val buffer = ShortArray(samples) { i ->
            (sin(2.0 * PI * i / samples) * Short.MAX_VALUE * 0.35).toInt().toShort()
        }
        track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(buffer, 0, buffer.size)
        track.setLoopPoints(0, buffer.size, -1)
    }

    fun setEnabled(value: Boolean) { enabled = value }

    fun on() {
        if (!enabled) return
        try { track.reloadStaticData(); track.play() } catch (_: Exception) {}
    }

    fun off() {
        try { if (track.playState == AudioTrack.PLAYSTATE_PLAYING) track.pause() } catch (_: Exception) {}
    }

    fun release() {
        try { track.stop(); track.release() } catch (_: Exception) {}
    }
}
