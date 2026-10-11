package com.ranjan.malav.morselight_flashlightwithmorsecode.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ranjan.malav.morselight_flashlightwithmorsecode.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Persisted settings for the redesign (WPM model + camera tuning + preference switches). */
data class Settings(
    val wpm: Int = DEFAULT_WPM,
    val reactSize: Int = DEFAULT_REACT_SIZE,       // camera detection-area px/percent
    val perceptibility: Int = DEFAULT_PERCEPTIBILITY, // camera sensitivity %
    val keyTone: Boolean = true,
    val loop: Boolean = false,
    val keepAwake: Boolean = true,
) {
    companion object {
        const val DEFAULT_WPM = 8
        const val MIN_WPM = 1
        const val MAX_WPM = 10
        const val DEFAULT_REACT_SIZE = 50
        const val DEFAULT_PERCEPTIBILITY = 30
    }
}

private const val KEY_SPEED = "speed"               // legacy (1..10)
private const val KEY_WPM = "wpm"
private const val KEY_REACT_SIZE = "react_size"
private const val KEY_PERCEPTIBILITY = "perceptibility"
private const val KEY_TONE = "key_tone"
private const val KEY_LOOP = "loop"
private const val KEY_AWAKE = "keep_awake"
private const val KEY_SENDING_DRILL_INDEX = "sending_drill_index"

private val wpmKey = intPreferencesKey(KEY_WPM)
private val reactSizeKey = intPreferencesKey(KEY_REACT_SIZE)
private val perceptibilityKey = intPreferencesKey(KEY_PERCEPTIBILITY)
private val sendingDrillIndexKey = intPreferencesKey(KEY_SENDING_DRILL_INDEX)

/** Map the old speed slider (1..10) to a comparable WPM (~5..23), decision D4/§7.3. */
internal fun speedToWpm(speed: Int): Int = (5 + (speed - 1) * 2).coerceIn(5, 25)

private val speedKey = intPreferencesKey(KEY_SPEED)

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "morse_settings",
    produceMigrations = { context ->
        // Copies speed/react_size/perceptibility verbatim from the old prefs file, then deletes it.
        // `speed` (1..10) is translated to `wpm` at read time (see the flow below).
        listOf(
            SharedPreferencesMigration(
                context = context,
                sharedPreferencesName = BuildConfig.APPLICATION_ID,
                keysToMigrate = setOf(KEY_SPEED, KEY_REACT_SIZE, KEY_PERCEPTIBILITY),
            )
        )
    },
)

/** DataStore-backed settings (D4) with a one-time migration from the old SharedPreferences. */
class SettingsRepository(context: Context) {

    private val dataStore = context.applicationContext.settingsDataStore

    private val toneKey = booleanPreferencesKey(KEY_TONE)
    private val loopKey = booleanPreferencesKey(KEY_LOOP)
    private val awakeKey = booleanPreferencesKey(KEY_AWAKE)

    val settings: Flow<Settings> = dataStore.data.map { p ->
        Settings(
            // Clamp to the usable range (1..10 wpm): past ~10 wpm the dots are too fast to key or
            // read reliably by hand (and the torch HAL / camera lag compounds it). Old saved values
            // above the cap are clamped here too.
            wpm = (p[wpmKey] ?: p[speedKey]?.let { speedToWpm(it) } ?: Settings.DEFAULT_WPM)
                .coerceIn(Settings.MIN_WPM, Settings.MAX_WPM),
            reactSize = p[reactSizeKey] ?: Settings.DEFAULT_REACT_SIZE,
            perceptibility = p[perceptibilityKey] ?: Settings.DEFAULT_PERCEPTIBILITY,
            keyTone = p[toneKey] ?: true,
            loop = p[loopKey] ?: false,
            keepAwake = p[awakeKey] ?: true,
        )
    }

    /** Sending-drill resume position (index into the drill's item list). Persisted so the next
     *  session starts on the same character; the Random button never writes to it. */
    val sendingDrillIndex: Flow<Int> = dataStore.data.map { it[sendingDrillIndexKey] ?: 0 }
    suspend fun setSendingDrillIndex(value: Int) = dataStore.edit { it[sendingDrillIndexKey] = value }

    suspend fun setWpm(value: Int) = dataStore.edit { it[wpmKey] = value }
    suspend fun setReactSize(value: Int) = dataStore.edit { it[reactSizeKey] = value }
    suspend fun setPerceptibility(value: Int) = dataStore.edit { it[perceptibilityKey] = value }
    suspend fun setKeyTone(value: Boolean) = dataStore.edit { it[toneKey] = value }
    suspend fun setLoop(value: Boolean) = dataStore.edit { it[loopKey] = value }
    suspend fun setKeepAwake(value: Boolean) = dataStore.edit { it[awakeKey] = value }
}
