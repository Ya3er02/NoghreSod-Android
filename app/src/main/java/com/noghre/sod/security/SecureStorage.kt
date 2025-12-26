package com.noghre.sod.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure storage utility for sensitive data using EncryptedSharedPreferences.
 * All data is encrypted with AES-256-GCM.
 *
 * @author Yaser
 * @version 1.0.0
 */
@Singleton
class SecureStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val PREFS_NAME = "noghresod_secure_prefs"

        // Keys for different data types
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_PHONE_NUMBER = "phone_number"
        private const val KEY_PAYMENT_INFO = "payment_info"
        private const val KEY_LAST_LOGIN = "last_login"
        private const val KEY_DEVICE_ID = "device_id"
    }

    /**
     * Save authentication token.
     *
     * @param token JWT or bearer token
     */
    fun saveAuthToken(token: String) {
        encryptedPrefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    /**
     * Get authentication token.
     *
     * @return Stored token or null
     */
    fun getAuthToken(): String? {
        return encryptedPrefs.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Save refresh token for token renewal.
     *
     * @param token Refresh token
     */
    fun saveRefreshToken(token: String) {
        encryptedPrefs.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    /**
     * Get refresh token.
     *
     * @return Stored refresh token or null
     */
    fun getRefreshToken(): String? {
        return encryptedPrefs.getString(KEY_REFRESH_TOKEN, null)
    }

    /**
     * Save user ID.
     *
     * @param userId User identifier
     */
    fun saveUserId(userId: String) {
        encryptedPrefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    /**
     * Get user ID.
     *
     * @return Stored user ID or null
     */
    fun getUserId(): String? {
        return encryptedPrefs.getString(KEY_USER_ID, null)
    }

    /**
     * Save phone number (PII).
     *
     * @param phoneNumber User's phone number
     */
    fun savePhoneNumber(phoneNumber: String) {
        encryptedPrefs.edit().putString(KEY_PHONE_NUMBER, phoneNumber).apply()
    }

    /**
     * Get phone number.
     *
     * @return Encrypted phone number or null
     */
    fun getPhoneNumber(): String? {
        return encryptedPrefs.getString(KEY_PHONE_NUMBER, null)
    }

    /**
     * Save last login timestamp.
     *
     * @param timestamp Last login time
     */
    fun saveLastLogin(timestamp: Long) {
        encryptedPrefs.edit().putLong(KEY_LAST_LOGIN, timestamp).apply()
    }

    /**
     * Get last login timestamp.
     *
     * @return Last login time or 0
     */
    fun getLastLogin(): Long {
        return encryptedPrefs.getLong(KEY_LAST_LOGIN, 0L)
    }

    /**
     * Save device ID for tracking.
     *
     * @param deviceId Device identifier
     */
    fun saveDeviceId(deviceId: String) {
        encryptedPrefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
    }

    /**
     * Get device ID.
     *
     * @return Stored device ID or null
     */
    fun getDeviceId(): String? {
        return encryptedPrefs.getString(KEY_DEVICE_ID, null)
    }

    /**
     * Clear specific key from storage.
     *
     * @param key Key to remove
     */
    fun removeKey(key: String) {
        encryptedPrefs.edit().remove(key).apply()
    }

    /**
     * Clear all secure data (on logout).
     * WARNING: This removes all stored credentials.
     */
    fun clearAll() {
        encryptedPrefs.edit().clear().apply()
    }

    /**
     * Check if user is authenticated.
     *
     * @return true if auth token exists
     */
    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }

    /**
     * Check if data exists for key.
     *
     * @param key Key to check
     * @return true if key exists
     */
    fun hasKey(key: String): Boolean {
        return encryptedPrefs.contains(key)
    }
}
