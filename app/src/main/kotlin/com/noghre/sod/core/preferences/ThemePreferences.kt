package com.noghre.sod.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Theme mode enumeration
 */
enum class ThemeMode {
    LIGHT,      // Always light theme
    DARK,       // Always dark theme
    SYSTEM      // Follow system setting (default)
}

/**
 * DataStore extension for preferences
 */
private val Context.themePreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "theme_preferences"
)

/**
 * Theme Preferences Manager
 * Handles theme mode selection and persistence
 * 
 * Uses DataStore for efficient storage and reactive updates
 */
@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val DEFAULT_THEME_MODE = ThemeMode.SYSTEM
    }
    
    private val dataStore = context.themePreferencesDataStore
    
    /**
     * Flow of theme mode
     * Emits whenever theme mode changes
     */
    val themeMode: Flow<ThemeMode> = dataStore.data.map { preferences ->
        val modeString = preferences[THEME_MODE_KEY]
        Timber.d("Theme mode from preferences: $modeString")
        
        when (modeString) {
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            ThemeMode.DARK.name -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }
    
    /**
     * Set theme mode
     * 
     * @param mode Theme mode to set
     */
    suspend fun setThemeMode(mode: ThemeMode) {
        try {
            dataStore.edit { preferences ->
                preferences[THEME_MODE_KEY] = mode.name
            }
            Timber.i("Theme mode changed to: ${mode.name}")
        } catch (e: Exception) {
            Timber.e(e, "Error setting theme mode")
        }
    }
    
    /**
     * Get current theme mode (non-reactive)
     * Useful for immediate access
     */
    suspend fun getCurrentThemeMode(): ThemeMode {
        return try {
            val preferences = dataStore.data.map { prefs ->
                when (prefs[THEME_MODE_KEY]) {
                    ThemeMode.LIGHT.name -> ThemeMode.LIGHT
                    ThemeMode.DARK.name -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
            }
            var result = DEFAULT_THEME_MODE
            preferences.collect { mode ->
                result = mode
            }
            result
        } catch (e: Exception) {
            Timber.e(e, "Error getting theme mode")
            DEFAULT_THEME_MODE
        }
    }
    
    /**
     * Reset theme mode to system default
     */
    suspend fun resetToDefault() {
        setThemeMode(DEFAULT_THEME_MODE)
        Timber.i("Theme mode reset to default")
    }
}

/**
 * Extension function for easy access in ViewModels
 */
suspend fun ThemePreferences.toggleThemeMode() {
    val current = getCurrentThemeMode()
    val next = when (current) {
        ThemeMode.LIGHT -> ThemeMode.DARK
        ThemeMode.DARK -> ThemeMode.SYSTEM
        ThemeMode.SYSTEM -> ThemeMode.LIGHT
    }
    setThemeMode(next)
}