package com.ranjan.malav.morselight_flashlightwithmorsecode.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import com.ranjan.malav.morselight_flashlightwithmorsecode.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Immutable snapshot of the three persisted transmission settings. */
data class Settings(
    val speed: Int = DEFAULT_SPEED,
    val reactSize: Int = DEFAULT_REACT_SIZE,
    val perceptibility: Int = DEFAULT_PERCEPTIBILITY,
) {
    companion object {
        const val DEFAULT_SPEED = 3
        const val DEFAULT_REACT_SIZE = 50
        const val DEFAULT_PERCEPTIBILITY = 30
    }
}

// Single DataStore per process. Keys match the old SharedPreferences names so the migration
// below imports existing values verbatim, then deletes the legacy prefs file.
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "morse_settings",
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(
                context = context,
                sharedPreferencesName = BuildConfig.APPLICATION_ID, // legacy file name
                keysToMigrate = setOf(KEY_SPEED, KEY_REACT_SIZE, KEY_PERCEPTIBILITY),
            )
        )
    },
)

private const val KEY_SPEED = "speed"
private const val KEY_REACT_SIZE = "react_size"
private const val KEY_PERCEPTIBILITY = "perceptibility"

/**
 * Persists the three transmission settings via Jetpack DataStore (decision D4).
 * Replaces the old SharedPreferenceUtils; existing users' values are carried over by the
 * SharedPreferencesMigration on first access.
 */
class SettingsRepository(context: Context) {

    private val dataStore = context.applicationContext.settingsDataStore

    private val speedKey = intPreferencesKey(KEY_SPEED)
    private val reactSizeKey = intPreferencesKey(KEY_REACT_SIZE)
    private val perceptibilityKey = intPreferencesKey(KEY_PERCEPTIBILITY)

    val settings: Flow<Settings> = dataStore.data.map { prefs ->
        Settings(
            speed = prefs[speedKey] ?: Settings.DEFAULT_SPEED,
            reactSize = prefs[reactSizeKey] ?: Settings.DEFAULT_REACT_SIZE,
            perceptibility = prefs[perceptibilityKey] ?: Settings.DEFAULT_PERCEPTIBILITY,
        )
    }

    suspend fun setSpeed(value: Int) = dataStore.edit { it[speedKey] = value }
    suspend fun setReactSize(value: Int) = dataStore.edit { it[reactSizeKey] = value }
    suspend fun setPerceptibility(value: Int) = dataStore.edit { it[perceptibilityKey] = value }
}
