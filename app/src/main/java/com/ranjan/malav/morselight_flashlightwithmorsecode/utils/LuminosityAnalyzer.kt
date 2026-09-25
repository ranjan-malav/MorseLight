package com.ranjan.malav.morselight_flashlightwithmorsecode.utils

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

typealias LumaListener = (luma: Double) -> Unit


class LuminosityAnalyzer(
    private val listener: LumaListener,
    private var considerableArea: Int = 50
) : ImageAnalysis.Analyzer {

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    fun updateConsiderableArea(percentage: Int) {
        considerableArea = percentage
    }

    override fun analyze(image: ImageProxy) {
        try {
            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()

            // Measure a centred SQUARE region — side = considerableArea% of the frame's shorter
            // side — so it matches the square drawn in the viewfinder.
            val side = considerableArea.coerceIn(1, 100) * minOf(image.width, image.height) / 100
            val cx = image.width / 2
            val cy = image.height / 2
            val startingX = (cx - side / 2).coerceAtLeast(0)
            val endingX = (cx + side / 2).coerceAtMost(image.width - 1)
            val startingY = (cy - side / 2).coerceAtLeast(0)
            val endingY = (cy + side / 2).coerceAtMost(image.height - 1)

            var sum = 0.0
            var counter = 0
            for (y in startingY..endingY) {
                val row = image.width * y
                for (x in startingX..endingX) {
                    sum += data[row + x].toInt() and 0xFF
                    counter++
                }
            }

            if (counter > 0) listener(sum / counter)
        } finally {
            image.close()
        }
    }
}