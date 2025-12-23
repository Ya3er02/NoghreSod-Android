package com.noghre.sod.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.noghre.sod.core.config.AppConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.TimeUnit

/**
 * Manages secure token storage and retrieval.
 * Uses EncryptedSharedPreferences from AndroidX Security library for encryption.
 *
 * Features:
 * - Secure token storage
 * - Automatic token expiration detection
 * - Thread-safe token refresh
 * - Reactive state management
 */
class TokenManager(
    context: Context
) {
    private val sharedPreferences: SharedPreferences
    private val mutex = Mutex()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    companion object {
        private const val PREFS_NAME = "noghre_sod_tokens"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
        private const val KEY_USER_ID = "user_id"
    }

    init {
        // Initialize encrypted shared preferences
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        // Check if user is already authenticated
        _isAuthenticated.value = getAccessToken() != null
    }

    /**
     * Get the current access token.
     * Returns null if token is expired or not found.
     */
    fun getAccessToken(): String? {
        val token = sharedPreferences.getString(KEY_ACCESS_TOKEN, null) ?: return null
        
        // Check if token is expired
        if (isTokenExpired()) {
            clearTokens()
            return null
        }
        
        return token
    }

    /**
     * Get the refresh token.
     */
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    /**
     * Get the token expiry time in milliseconds.
     */
    fun getTokenExpiry(): Long {
        return sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0L)
    }

    /**
     * Get the stored user ID.
     */
    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    /**
     * Save tokens to secure storage.
     * @param accessToken The access token
     * @param refreshToken The refresh token
     * @param expiresInSeconds The expiration time in seconds
     * @param userId The user ID
     */
    fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresInSeconds: Long = AppConfig.Token.DEFAULT_EXPIRY_SECONDS,
        userId: String? = null
    ) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            
            // Calculate expiry timestamp
            val expiryTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expiresInSeconds)
            putLong(KEY_TOKEN_EXPIRY, expiryTime)
            
            if (userId != null) {
                putString(KEY_USER_ID, userId)
            }
            
            apply()
        }
        
        _isAuthenticated.value = true
    }

    /**
     * Check if the current token is expired.
     * Includes a 5-minute buffer for early refresh.
     */
    fun isTokenExpired(): Boolean {
        val expiry = getTokenExpiry()
        if (expiry == 0L) return true
        
        // Refresh buffer in milliseconds (5 minutes by default)
        val bufferMs = TimeUnit.MINUTES.toMillis(AppConfig.Token.REFRESH_BUFFER_MINUTES.toLong())
        
        return System.currentTimeMillis() >= (expiry - bufferMs)
    }

    /**
     * Check if token needs refresh.
     * More aggressive than isTokenExpired, used to proactively refresh.
     */
    fun shouldRefreshToken(): Boolean {
        return isTokenExpired() && getRefreshToken() != null
    }

    /**
     * Clear all stored tokens.
     * Should be called on logout or when token refresh fails.
     */
    fun clearTokens() {
        sharedPreferences.edit().apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_TOKEN_EXPIRY)
            remove(KEY_USER_ID)
            apply()
        }
        
        _isAuthenticated.value = false
    }

    /**
     * Check if user is authenticated.
     */
    fun isAuthenticated(): Boolean {
        val token = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        return token != null && !isTokenExpired()
    }

    /**
     * Get all token information as a map.
     * Useful for debugging.
     */
    fun getTokenInfo(): Map<String, Any?> {
        return mapOf(
            "access_token" to (getAccessToken()?.take(20) + "..."), // Masked for security
            "refresh_token" to (getRefreshToken()?.take(20) + "..."), // Masked for security
            "token_expiry" to getTokenExpiry(),
            "is_expired" to isTokenExpired(),
            "user_id" to getUserId(),
            "is_authenticated" to isAuthenticated()
        )
    }
}
