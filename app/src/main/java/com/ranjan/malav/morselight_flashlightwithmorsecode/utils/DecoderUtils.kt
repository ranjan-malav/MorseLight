package com.ranjan.malav.morselight_flashlightwithmorsecode.utils

import android.util.Log


object DecoderUtils {
    const val SMALL_UNITS = "small_units"
    const val MEDIUM_UNITS = "medium_units"
    const val BIG_UNITS = "big_units"

    /*
        Divide the differences by the smaller of the numbers it's a difference between to
        get a percentage change. Set a threshold and when the change exceeds that threshold,
        start a new "cluster".
     */
    fun findNaturalBreakOnTimings(timings: ArrayList<Long>): HashMap<String, ArrayList<Long>> {
        timings.sort()
        val percentageDiffs = arrayListOf<Float>()
        timings.forEachIndexed { index, timing ->
            if (index == 0) return@forEachIndexed
            val percentageInc = ((timing - timings[index - 1]) * 100F) / (timings[index - 1])
            percentageDiffs.add(percentageInc)
        }

        val percDiffs = StringBuilder()
        percentageDiffs.forEach {
            percDiffs.append(it).append(" * ")
        }
        Log.d("ManualDecode", "On percentage Diffs: $percDiffs")

        val smallerUnits = arrayListOf<Long>()
        val biggerUnits = arrayListOf<Long>()
        var switchToBiggerUnits = false
        timings.forEachIndexed { index, timing ->
            if (switchToBiggerUnits) {
                biggerUnits.add(timing)
            } else {
                smallerUnits.add(timing)
            }
            try {
                if (percentageDiffs[index] > 100 && !switchToBiggerUnits) {
                    switchToBiggerUnits = true
                }
            } catch (ex: IndexOutOfBoundsException) {

            }
        }
        return hashMapOf(
            SMALL_UNITS to smallerUnits,
            BIG_UNITS to biggerUnits
        )
    }

    fun findNaturalBreakOffTimings(timings: ArrayList<Long>): HashMap<String, ArrayList<Long>> {
        timings.sort()
        val percentageDiffs = arrayListOf<Float>()
        timings.forEachIndexed { index, timing ->
            if (index == 0) return@forEachIndexed
            val percentageInc = ((timing - timings[index - 1]) * 100F) / (timings[index - 1])
            percentageDiffs.add(percentageInc)
        }

        val percDiffs = StringBuilder()
        percentageDiffs.forEach {
            percDiffs.append(it).append(" * ")
        }
        Log.d("ManualDecode", "Off percentage Diffs: $percDiffs")

        val smallerUnits = arrayListOf<Long>()
        val mediumUnits = arrayListOf<Long>()
        val biggerUnits = arrayListOf<Long>()
        var switchToMediumUnits = false
        var switchToBiggerUnits = false
        timings.forEachIndexed { index, timing ->
            when {
                switchToBiggerUnits -> {
                    biggerUnits.add(timing)
                }
                switchToMediumUnits -> {
                    mediumUnits.add(timing)
                }
                else -> {
                    smallerUnits.add(timing)
                }
            }
            try {
                if (percentageDiffs[index] > 100 && !switchToMediumUnits) {
                    switchToMediumUnits = true
                } else if (switchToMediumUnits &&
                    percentageDiffs[index] > 100 && !switchToBiggerUnits
                ) {
                    switchToBiggerUnits = true
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
}

