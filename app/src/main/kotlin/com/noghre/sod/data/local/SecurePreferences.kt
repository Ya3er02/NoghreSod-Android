package com.noghre.sod.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure preferences wrapper using EncryptedSharedPreferences.
 * All sensitive data (tokens, keys, etc.) must be stored here, NOT in regular SharedPreferences.
 * 
 * @since 1.0.0
 */
@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }
    
    private val encryptedSharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    /**
     * Save authentication token.
     */
    fun saveAuthToken(token: String) {
        encryptedSharedPreferences.edit().apply {
            putString(AUTH_TOKEN_KEY, token)
            apply()
        }
    }
    
    /**
     * Get authentication token.
     */
    fun getAuthToken(): String? {
        return encryptedSharedPreferences.getString(AUTH_TOKEN_KEY, null)
    }
    
    /**
     * Clear authentication token (logout).
     */
    fun clearAuthToken() {
        encryptedSharedPreferences.edit().apply {
            remove(AUTH_TOKEN_KEY)
            apply()
        }
    }
    
    /**
     * Save user credentials (use sparingly - tokens preferred).
     */
    fun saveUserCredentials(email: String, password: String) {
        encryptedSharedPreferences.edit().apply {
            putString(USER_EMAIL_KEY, email)
            putString(USER_PASSWORD_KEY, password)
            apply()
        }
    }
    
    /**
     * Get user email.
     */
    fun getUserEmail(): String? {
        return encryptedSharedPreferences.getString(USER_EMAIL_KEY, null)
    }
    
    /**
     * Get user password.
     */
    fun getUserPassword(): String? {
        return encryptedSharedPreferences.getString(USER_PASSWORD_KEY, null)
    }
    
    /**
     * Clear user credentials.
     */
    fun clearUserCredentials() {
        encryptedSharedPreferences.edit().apply {
            remove(USER_EMAIL_KEY)
            remove(USER_PASSWORD_KEY)
            apply()
        }
    }
    
    /**
     * Save refresh token.
     */
    fun saveRefreshToken(token: String) {
        encryptedSharedPreferences.edit().apply {
            putString(REFRESH_TOKEN_KEY, token)
            apply()
        }
    }
    
    /**
     * Get refresh token.
     */
    fun getRefreshToken(): String? {
        return encryptedSharedPreferences.getString(REFRESH_TOKEN_KEY, null)
    }
    
    /**
     * Save API key.
     */
    fun saveApiKey(key: String) {
        encryptedSharedPreferences.edit().apply {
            putString(API_KEY, key)
            apply()
        }
    }
    
    /**
     * Get API key.
     */
    fun getApiKey(): String? {
        return encryptedSharedPreferences.getString(API_KEY, null)
    }
    
    /**
     * Clear all sensitive data (logout).
     */
    fun clearAll() {
        encryptedSharedPreferences.edit().clear().apply()
    }
    
    companion object {
        private const val ENCRYPTED_PREFS_NAME = "noghre_encrypted_prefs"
        private const val AUTH_TOKEN_KEY = "auth_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val USER_EMAIL_KEY = "user_email"
        private const val USER_PASSWORD_KEY = "user_password"
        private const val API_KEY = "api_key"
    }
}
