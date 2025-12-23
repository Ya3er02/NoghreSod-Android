package com.noghre.sod.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import timber.log.Timber

/**
 * Secure token management with AES-256-GCM encryption
 * Handles access tokens, refresh tokens, and auth state
 */
class TokenManager(context: Context) {
    private val prefs: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Save access token
     */
    fun saveAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
        Timber.d("Access token saved")
    }

    /**
     * Get access token
     */
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    /**
     * Save refresh token
     */
    fun saveRefreshToken(token: String) {
        prefs.edit().putString(KEY_REFRESH_TOKEN, token).apply()
        Timber.d("Refresh token saved")
    }

    /**
     * Get refresh token
     */
    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    /**
     * Save user ID
     */
    fun saveUserId(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    /**
     * Get user ID
     */
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return getAccessToken() != null && getUserId() != null
    }

    /**
     * Clear all tokens (logout)
     */
    fun clearTokens() {
        prefs.edit().apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_USER_ID)
        }.apply()
        Timber.d("Tokens cleared - user logged out")
    }

    companion object {
        private const val PREFS_NAME = "noghresod_secure_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
    }
}