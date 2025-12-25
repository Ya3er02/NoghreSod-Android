package com.noghre.sod.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure Preferences using AES-256-GCM encryption
 * 
 * Security features:
 * - Hardware-backed keystore (StrongBox) when available
 * - AES-256-GCM for value encryption
 * - AES-256-SIV for key encryption
 * - Automatic key generation and rotation
 * 
 * All tokens (access, refresh) are encrypted at rest.
 * 
 * @since 1.0.0
 */
@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private const val PREFS_NAME = "noghresod_secure_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_DEVICE_ID = "device_id"
    }
    
    /**
     * Master key using AES-256-GCM
     * 
     * Hardware-backed when available (StrongBox):
     * - Nexus 5X+
     * - Pixel 2+
     * - Devices with TEE (Trusted Execution Environment)
     */
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .setRequestStrongBoxBacked(true)  // Use hardware-backed keystore
            .setUserAuthenticationRequired(false)  // Can be set to true for biometric
            .build()
    }
    
    /**
     * Encrypted SharedPreferences instance
     * 
     * Encryption schemes:
     * - PrefKeyEncryptionScheme.AES256_SIV: Deterministic key encryption
     * - PrefValueEncryptionScheme.AES256_GCM: Value encryption
     */
    private val encryptedPrefs: android.content.SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    // ✅ AUTHENTICATION TOKENS (ENCRYPTED)
    
    /**
     * Save access token (encrypted)
     * 
     * @param token JWT access token
     */
    fun saveAccessToken(token: String) {
        encryptedPrefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, token)
            apply()
        }
    }
    
    /**
     * Get access token (decrypted)
     * 
     * @return Access token or null
     */
    fun getAccessToken(): String? {
        return encryptedPrefs.getString(KEY_ACCESS_TOKEN, null)
    }
    
    /**
     * Save refresh token (encrypted)
     * 
     * @param token JWT refresh token
     */
    fun saveRefreshToken(token: String) {
        encryptedPrefs.edit().apply {
            putString(KEY_REFRESH_TOKEN, token)
            apply()
        }
    }
    
    /**
     * Get refresh token (decrypted)
     * 
     * @return Refresh token or null
     */
    fun getRefreshToken(): String? {
        return encryptedPrefs.getString(KEY_REFRESH_TOKEN, null)
    }
    
    /**
     * Check if tokens exist (without decryption)
     */
    fun hasTokens(): Boolean {
        return encryptedPrefs.contains(KEY_ACCESS_TOKEN) &&
               encryptedPrefs.contains(KEY_REFRESH_TOKEN)
    }
    
    // ✅ USER PROFILE DATA (ENCRYPTED)
    
    /**
     * Save user ID (encrypted)
     */
    fun saveUserId(userId: String) {
        encryptedPrefs.edit().apply {
            putString(KEY_USER_ID, userId)
            apply()
        }
    }
    
    /**
     * Get user ID (decrypted)
     */
    fun getUserId(): String? {
        return encryptedPrefs.getString(KEY_USER_ID, null)
    }
    
    /**
     * Save user email (encrypted)
     */
    fun saveUserEmail(email: String) {
        encryptedPrefs.edit().apply {
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }
    
    /**
     * Get user email (decrypted)
     */
    fun getUserEmail(): String? {
        return encryptedPrefs.getString(KEY_USER_EMAIL, null)
    }
    
    // ✅ DEVICE IDENTITY
    
    /**
     * Save device ID (encrypted)
     * Used for binding tokens to specific device
     */
    fun saveDeviceId(deviceId: String) {
        encryptedPrefs.edit().apply {
            putString(KEY_DEVICE_ID, deviceId)
            apply()
        }
    }
    
    /**
     * Get device ID (decrypted)
     */
    fun getDeviceId(): String? {
        return encryptedPrefs.getString(KEY_DEVICE_ID, null)
    }
    
    // ✅ LOGOUT & DATA CLEARING
    
    /**
     * Clear all sensitive data on logout
     * 
     * Securely removes:
     * - Access tokens
     * - Refresh tokens
     * - User profile data
     * - Device ID
     */
    fun clearAllSensitiveData() {
        encryptedPrefs.edit().apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)
            remove(KEY_DEVICE_ID)
            apply()
        }
    }
    
    /**
     * Clear only authentication tokens
     * (Useful for token refresh scenarios)
     */
    fun clearAuthTokens() {
        encryptedPrefs.edit().apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            apply()
        }
    }
    
    /**
     * Force clear all data (for testing)
     * Warning: This is permanent and cannot be undone
     */
    fun clearAll() {
        encryptedPrefs.edit().clear().apply()
    }
}
