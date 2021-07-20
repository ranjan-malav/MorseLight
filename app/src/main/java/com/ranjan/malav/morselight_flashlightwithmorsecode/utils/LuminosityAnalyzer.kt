package com.ranjan.malav.morselight_flashlightwithmorsecode.utils

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.ranjan.malav.morselight_flashlightwithmorsecode.fragments.LumaListener
import java.nio.ByteBuffer


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

        val buffer = image.planes[0].buffer
        val data = buffer.toByteArray()
        val pixels = data.map { it.toInt() and 0xFF }

        val startingX = (50 - considerableArea / 2) * image.width / 100
        val startingY = (50 - considerableArea / 2) * image.height / 100
        val endingX = (50 + considerableArea / 2) * image.width / 100
        val endingY = (50 + considerableArea / 2) * image.height / 100

        var sum = 0.0
        var counter = 0
        for (y in startingY..endingY) {
            for (x in startingX..endingX) {
                sum += pixels[image.width * y + x]
                counter++
            }
        }

        if (counter == 0) return
        val luma = sum / counter

        listener(luma)

        image.close()
    }
}