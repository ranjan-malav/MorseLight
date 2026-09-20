package com.ranjan.malav.morselight_flashlightwithmorsecode.morse

/**
 * Decodes captured on/off flash timings into morse and then text.
 *
 * Ported from the pre-migration DecoderUtils. Algorithm unchanged; the dead debug scaffolding
 * (unused percentage-diff lists and StringBuilders — audit bug B8) has been removed.
 */
object MorseDecoder {
    private const val SMALL_UNITS = "small_units"
    private const val MEDIUM_UNITS = "medium_units"
    private const val BIG_UNITS = "big_units"

    /*
        Divide the differences by the smaller of the numbers it's a difference between to
        get a percentage change. Set a threshold and when the change exceeds that threshold,
        start a new "cluster".
     */
    private fun findNaturalBreakOnTimings(timings: ArrayList<Long>): HashMap<String, ArrayList<Long>> {
        timings.sort()

        val smallerUnits = arrayListOf<Long>()
        val biggerUnits = arrayListOf<Long>()
        var switchToBiggerUnits = false
        var movingAverage = 0L
        var addedCount = 0
        timings.forEachIndexed { _, timing ->
            movingAverage = if (movingAverage > 0) {
                (movingAverage * addedCount + timing) / (addedCount + 1)
            } else {
                timing
            }
            addedCount++
            try {
                // Compare it with moving average
                val diffFromMovingAvg: Float = if (movingAverage == 0L) {
                    0F
                } else {
                    (timing - movingAverage).toFloat() / movingAverage * 100
                }
                when {
                    switchToBiggerUnits -> {
                        biggerUnits.add(timing)
                    }
                    diffFromMovingAvg > 50 -> {
                        switchToBiggerUnits = true
                        biggerUnits.add(timing)
                        movingAverage = 0L
                        addedCount = 0
                    }
                    else -> {
                        movingAverage = if (movingAverage > 0) {
                            (movingAverage * addedCount + timing) / (addedCount + 1)
                        } else {
                            timing
                        }
                        addedCount++
                        smallerUnits.add(timing)
                    }
                }
            } catch (ex: IndexOutOfBoundsException) {

            }
        }
        return hashMapOf(
            SMALL_UNITS to smallerUnits,
            BIG_UNITS to biggerUnits
        )
    }

    private fun findNaturalBreakOffTimings(timings: ArrayList<Long>): HashMap<String, ArrayList<Long>> {
        timings.sort()

        val smallerUnits = arrayListOf<Long>()
        val mediumUnits = arrayListOf<Long>()
        val biggerUnits = arrayListOf<Long>()
        var switchToMediumUnits = false
        var switchToBiggerUnits = false
        var movingAverage = 0L
        var addedCount = 0
        timings.forEachIndexed { _, timing ->
            try {
                // Compare it with moving average
                val diffFromMovingAvg: Float = if (movingAverage == 0L) {
                    0F
                } else {
                    (timing - movingAverage).toFloat() / movingAverage * 100
                }
                if (switchToBiggerUnits) {
                    biggerUnits.add(timing)
                } else if (switchToMediumUnits && diffFromMovingAvg > 50) {
                    switchToBiggerUnits = true
                    biggerUnits.add(timing)
                    movingAverage = timing
                    addedCount = 1
                } else if (switchToMediumUnits) {
                    movingAverage = if (movingAverage > 0) {
                        (movingAverage * addedCount + timing) / (addedCount + 1)
                    } else {
                        timing
                    }
                    addedCount++
                    mediumUnits.add(timing)
                } else if (diffFromMovingAvg > 50) {
                    switchToMediumUnits = true
                    mediumUnits.add(timing)
                    movingAverage = timing
                    addedCount = 1
                } else {
                    movingAverage = if (movingAverage > 0) {
                        (movingAverage * addedCount + timing) / (addedCount + 1)
                    } else {
                        timing
                    }
                    addedCount++
                    smallerUnits.add(timing)
                }
            } catch (ex: IndexOutOfBoundsException) {

            }
        }
        return hashMapOf(
            SMALL_UNITS to smallerUnits,
            MEDIUM_UNITS to mediumUnits,
            BIG_UNITS to biggerUnits
        )
    }

    fun decryptMorse(message: String): String {
        val myArray: Array<String> = message.split(" ").toTypedArray()
        val show = StringBuilder()
        for (i in myArray.indices) {
            val c = morseToChar[myArray[i]]
            if (c != null) {
                show.append(c)
            } else {
                show.append('?')
            }
        }
        return show.toString()
    }

    fun findMorseFromTimings(
        timings: ArrayList<Long>, diffTimings: ArrayList<Long>
    ): String {
        if (timings.isNotEmpty()) {
            val onTimings = arrayListOf<Long>()
            val offTimings = arrayListOf<Long>()
            for (i in diffTimings.indices) {
                if (i % 2 == 0) {
                    onTimings.add(diffTimings[i])
                } else {
                    offTimings.add(diffTimings[i])
                }
            }
            val onDecodedMap = findNaturalBreakOnTimings(onTimings)
            val smallOnTimings = onDecodedMap[SMALL_UNITS]
            val bigOnTimings = onDecodedMap[BIG_UNITS]

            val offDecodedMap = findNaturalBreakOffTimings(offTimings)
            val mediumOffTimings = offDecodedMap[MEDIUM_UNITS]
            val bigOffTimings = offDecodedMap[BIG_UNITS]

            val message = StringBuilder()
            timings.forEachIndexed { index, _ ->
                if (index == 0) return@forEachIndexed
                val diff = timings[index] - timings[index - 1]
                when {
                    smallOnTimings!!.contains(diff) -> {
                        message.append(".")
                    }
                    bigOnTimings!!.contains(diff) -> {
                        message.append("-")
                    }
                    mediumOffTimings!!.contains(diff) -> {
                        message.append(" ")
                    }
                    bigOffTimings!!.contains(diff) -> {
                        message.append(" / ")
                    }
                }
            }
            timings.clear()
            diffTimings.clear()
            return message.toString()
        }
        return ""
    }

    fun getMorseForMessage(charMessage: ArrayList<Char>): String {
        val morseCode = StringBuilder()
        for (char in charMessage) {
            morseCode.append(charToMorse[char])
        }
        return morseCode.toString()
    }
}
