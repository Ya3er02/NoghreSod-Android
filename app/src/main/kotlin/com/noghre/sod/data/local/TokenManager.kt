package com.noghre.sod.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages secure token storage and authentication state.
 * Uses EncryptedSharedPreferences for secure token storage with encryption.
 *
 * Provides methods to:
 * - Save and retrieve tokens securely
 * - Check token expiration
 * - Manage authentication state
 * - Clear tokens on logout
 */
class TokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFERENCES_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // StateFlow for authentication state
    private val _isAuthenticatedFlow = MutableStateFlow<Boolean>(
        hasValidAccessToken()
    )
    val isAuthenticatedFlow: StateFlow<Boolean> = _isAuthenticatedFlow.asStateFlow()

    private val _accessTokenFlow = MutableStateFlow<String?>(
        getAccessToken()
    )
    val accessTokenFlow: StateFlow<String?> = _accessTokenFlow.asStateFlow()

    /**
     * Saves tokens to encrypted storage.
     *
     * @param accessToken The access token
     * @param refreshToken The refresh token
     * @param expiresIn Token expiration time in seconds
     */
    fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresIn: Long
    ) {
        encryptedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            // Calculate expiration timestamp
            putLong(
                KEY_TOKEN_EXPIRY,
                System.currentTimeMillis() + (expiresIn * 1000)
            )
            apply()
        }
        // Update StateFlow
        _accessTokenFlow.value = accessToken
        _isAuthenticatedFlow.value = true
    }

    /**
     * Gets the stored access token.
     *
     * @return The access token or null if not found
     */
    fun getAccessToken(): String? {
        return encryptedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    /**
     * Gets the stored refresh token.
     *
     * @return The refresh token or null if not found
     */
    fun getRefreshToken(): String? {
        return encryptedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    /**
     * Checks if the access token is valid (not expired).
     * Considers a 5-minute buffer before actual expiration for safety.
     *
     * @return true if token is valid, false otherwise
     */
    fun hasValidAccessToken(): Boolean {
        val accessToken = getAccessToken() ?: return false
        val expiryTime = encryptedPreferences.getLong(KEY_TOKEN_EXPIRY, 0L)

        if (expiryTime == 0L) return true // No expiry set, consider as valid

        // Consider token invalid if expiring within 5 minutes
        val bufferTime = 5 * 60 * 1000 // 5 minutes in milliseconds
        val currentTime = System.currentTimeMillis()

        return currentTime < (expiryTime - bufferTime)
    }

    /**
     * Checks if the token has expired.
     *
     * @return true if token is expired, false otherwise
     */
    fun isTokenExpired(): Boolean {
        val expiryTime = encryptedPreferences.getLong(KEY_TOKEN_EXPIRY, 0L)
        if (expiryTime == 0L) return false
        return System.currentTimeMillis() >= expiryTime
    }

    /**
     * Gets the remaining time until token expiration.
     *
     * @return Remaining time in milliseconds, or 0 if no token
     */
    fun getTokenExpiryTimeRemaining(): Long {
        val expiryTime = encryptedPreferences.getLong(KEY_TOKEN_EXPIRY, 0L)
        if (expiryTime == 0L) return 0L
        val remaining = expiryTime - System.currentTimeMillis()
        return if (remaining > 0) remaining else 0L
    }

    /**
     * Updates only the access token (used after refresh).
     *
     * @param accessToken The new access token
     * @param expiresIn Token expiration time in seconds
     */
    fun updateAccessToken(
        accessToken: String,
        expiresIn: Long
    ) {
        encryptedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putLong(
                KEY_TOKEN_EXPIRY,
                System.currentTimeMillis() + (expiresIn * 1000)
            )
            apply()
        }
        _accessTokenFlow.value = accessToken
        _isAuthenticatedFlow.value = true
    }

    /**
     * Clears all tokens and resets authentication state.
     * Called on logout or when token refresh fails permanently.
     */
    fun clearTokens() {
        encryptedPreferences.edit().apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_TOKEN_EXPIRY)
            apply()
        }
        _accessTokenFlow.value = null
        _isAuthenticatedFlow.value = false
    }

    /**
     * Checks if user is authenticated (has a valid access token).
     *
     * @return true if authenticated, false otherwise
     */
    fun isAuthenticated(): Boolean {
        return hasValidAccessToken()
    }

    companion object {
        private const val PREFERENCES_NAME = "noghresod_tokens"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
    }
}
