package com.noghre.sod.data.local.prefs

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages secure token storage using EncryptedSharedPreferences.
 * Handles access token, refresh token, and expiration time.
 */
class TokenManager @Inject constructor(
    private val encryptedPrefs: SharedPreferences
) {

    companion object {
        private const val PREF_ACCESS_TOKEN = "access_token"
        private const val PREF_REFRESH_TOKEN = "refresh_token"
        private const val PREF_TOKEN_EXPIRY = "token_expiry"
        private const val PREF_TOKEN_TYPE = "token_type"
    }

    /**
     * Save tokens and expiry time.
     *
     * @param accessToken The access token from authentication response
     * @param refreshToken The refresh token for token renewal
     * @param expiresIn Token expiry duration in seconds
     */
    fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long = 3600) {
        val expiryTime = System.currentTimeMillis() + (expiresIn * 1000)
        encryptedPrefs.edit().apply {
            putString(PREF_ACCESS_TOKEN, accessToken)
            putString(PREF_REFRESH_TOKEN, refreshToken)
            putLong(PREF_TOKEN_EXPIRY, expiryTime)
            putString(PREF_TOKEN_TYPE, "Bearer")
            apply()
        }
        Timber.d("Tokens saved successfully")
    }

    /**
     * Get current access token.
     *
     * @return Access token or null if not available
     */
    fun getAccessToken(): String? {
        val token = encryptedPrefs.getString(PREF_ACCESS_TOKEN, null)
        return if (token != null && !isTokenExpired()) {
            token
        } else {
            null
        }
    }

    /**
     * Get refresh token for token renewal.
     *
     * @return Refresh token or null if not available
     */
    fun getRefreshToken(): String? {
        return encryptedPrefs.getString(PREF_REFRESH_TOKEN, null)
    }

    /**
     * Check if current token is expired.
     *
     * @return True if token has expired, false otherwise
     */
    fun isTokenExpired(): Boolean {
        val expiryTime = encryptedPrefs.getLong(PREF_TOKEN_EXPIRY, 0)
        val isExpired = System.currentTimeMillis() > expiryTime
        if (isExpired) {
            Timber.d("Token has expired")
        }
        return isExpired
    }

    /**
     * Clear all stored tokens.
     * Called during logout.
     */
    fun clearTokens() {
        encryptedPrefs.edit().apply {
            remove(PREF_ACCESS_TOKEN)
            remove(PREF_REFRESH_TOKEN)
            remove(PREF_TOKEN_EXPIRY)
            remove(PREF_TOKEN_TYPE)
            apply()
        }
        Timber.d("Tokens cleared")
    }

    /**
     * Check if user is authenticated.
     *
     * @return True if valid token exists, false otherwise
     */
    fun isAuthenticated(): Boolean {
        return getAccessToken() != null
    }
}
