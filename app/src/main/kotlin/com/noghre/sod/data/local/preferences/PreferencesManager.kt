package com.noghre.sod.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.noghre.sod.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages app preferences using DataStore.
 * 
* Stores authentication tokens, user info, and app settings securely.
 * Uses encrypted DataStore for sensitive data.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "noghre_sod_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {

    companion object {
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_MOBILE = stringPreferencesKey("user_mobile")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val APP_LANGUAGE = stringPreferencesKey("app_language")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val LAST_SYNC = stringPreferencesKey("last_sync")
    }

    /**
     * Set authentication token.
     */
    suspend fun setAuthToken(token: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[AUTH_TOKEN] = token
            }
            Timber.d("Auth token saved")
        } catch (e: Exception) {
            Timber.e(e, "Error saving auth token")
        }
    }

    /**
     * Get authentication token.
     */
    suspend fun getAuthToken(): String? {
        return try {
            context.dataStore.data.map { preferences ->
                preferences[AUTH_TOKEN]
            }.let { flow ->
                var result: String? = null
                flow.collect { result = it }
                result
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading auth token")
            null
        }
    }

    /**
     * Set refresh token.
     */
    suspend fun setRefreshToken(token: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[REFRESH_TOKEN] = token
            }
        } catch (e: Exception) {
            Timber.e(e, "Error saving refresh token")
        }
    }

    /**
     * Get refresh token.
     */
    suspend fun getRefreshToken(): String? {
        return try {
            context.dataStore.data.map { preferences ->
                preferences[REFRESH_TOKEN]
            }.let { flow ->
                var result: String? = null
                flow.collect { result = it }
                result
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading refresh token")
            null
        }
    }

    /**
     * Set current user info.
     */
    suspend fun setCurrentUser(user: User) {
        try {
            context.dataStore.edit { preferences ->
                preferences[USER_ID] = user.id
                preferences[USER_MOBILE] = user.mobile
                preferences[USER_NAME] = user.name
                preferences[USER_EMAIL] = user.email ?: ""
                preferences[IS_LOGGED_IN] = true
            }
            Timber.d("User info saved")
        } catch (e: Exception) {
            Timber.e(e, "Error saving user info")
        }
    }

    /**
     * Get current user ID.
     */
    suspend fun getCurrentUserId(): String? {
        return try {
            context.dataStore.data.map { preferences ->
                preferences[USER_ID]
            }.let { flow ->
                var result: String? = null
                flow.collect { result = it }
                result
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading user ID")
            null
        }
    }

    /**
     * Get current user mobile.
     */
    suspend fun getCurrentUserMobile(): String? {
        return try {
            context.dataStore.data.map { preferences ->
                preferences[USER_MOBILE]
            }.let { flow ->
                var result: String? = null
                flow.collect { result = it }
                result
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading user mobile")
            null
        }
    }

    /**
     * Check if user is logged in.
     */
    suspend fun isLoggedIn(): Boolean {
        return try {
            context.dataStore.data.map { preferences ->
                preferences[IS_LOGGED_IN] ?: false
            }.let { flow ->
                var result = false
                flow.collect { result = it }
                result
            }
        } catch (e: Exception) {
            Timber.e(e, "Error checking login status")
            false
        }
    }

    /**
     * Set app language.
     */
    suspend fun setAppLanguage(language: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[APP_LANGUAGE] = language
            }
        } catch (e: Exception) {
            Timber.e(e, "Error saving language")
        }
    }

    /**
     * Get app language.
     */
    suspend fun getAppLanguage(): String {
        return try {
            context.dataStore.data.map { preferences ->
                preferences[APP_LANGUAGE] ?: "fa"
            }.let { flow ->
                var result = "fa"
                flow.collect { result = it }
                result
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading language")
            "fa"
        }
    }

    /**
     * Set theme mode.
     */
    suspend fun setThemeMode(theme: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[THEME_MODE] = theme
            }
        } catch (e: Exception) {
            Timber.e(e, "Error saving theme")
        }
    }

    /**
     * Get theme mode.
     */
    suspend fun getThemeMode(): String {
        return try {
            context.dataStore.data.map { preferences ->
                preferences[THEME_MODE] ?: "light"
            }.let { flow ->
                var result = "light"
                flow.collect { result = it }
                result
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading theme")
            "light"
        }
    }

    /**
     * Clear all authentication data on logout.
     */
    suspend fun clearAuthData() {
        try {
            context.dataStore.edit { preferences ->
                preferences.remove(AUTH_TOKEN)
                preferences.remove(REFRESH_TOKEN)
                preferences.remove(USER_ID)
                preferences.remove(USER_MOBILE)
                preferences.remove(USER_NAME)
                preferences.remove(USER_EMAIL)
                preferences[IS_LOGGED_IN] = false
            }
            Timber.d("Auth data cleared")
        } catch (e: Exception) {
            Timber.e(e, "Error clearing auth data")
        }
    }

    /**
     * Set last sync time.
     */
    suspend fun setLastSync(timestamp: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[LAST_SYNC] = timestamp
            }
        } catch (e: Exception) {
            Timber.e(e, "Error saving last sync time")
        }
    }

    /**
     * Get last sync time.
     */
    suspend fun getLastSync(): String? {
        return try {
            context.dataStore.data.map { preferences ->
                preferences[LAST_SYNC]
            }.let { flow ->
                var result: String? = null
                flow.collect { result = it }
                result
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading last sync time")
            null
        }
    }
}
