package com.noghre.sod.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import timber.log.Timber

/**
 * Secure preference manager using encrypted shared preferences.
 * Uses Android Security Crypto library for encryption.
 */
object SecurePreferenceManager {

    private const val PREF_NAME = "noghre_sod_secure"
    private const val AUTH_TOKEN_KEY = "auth_token"
    private const val USER_ID_KEY = "user_id"
    private const val REFRESH_TOKEN_KEY = "refresh_token"

    /**
     * Get encrypted shared preferences instance.
     */
    private fun getEncryptedPreferences(context: Context) = try {
        EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        Timber.e(e, "Error creating encrypted preferences")
        throw e
    }

    /**
     * Save authentication token securely.
     */
    fun saveToken(context: Context, token: String) {
        try {
            getEncryptedPreferences(context)
                .edit()
                .putString(AUTH_TOKEN_KEY, token)
                .apply()
            Timber.d("Token saved securely")
        } catch (e: Exception) {
            Timber.e(e, "Error saving token")
        }
    }

    /**
     * Retrieve authentication token.
     */
    fun getToken(context: Context): String? {
        return try {
            getEncryptedPreferences(context)
                .getString(AUTH_TOKEN_KEY, null)
        } catch (e: Exception) {
            Timber.e(e, "Error retrieving token")
            null
        }
    }

    /**
     * Save refresh token.
     */
    fun saveRefreshToken(context: Context, token: String) {
        try {
            getEncryptedPreferences(context)
                .edit()
                .putString(REFRESH_TOKEN_KEY, token)
                .apply()
        } catch (e: Exception) {
            Timber.e(e, "Error saving refresh token")
        }
    }

    /**
     * Retrieve refresh token.
     */
    fun getRefreshToken(context: Context): String? {
        return try {
            getEncryptedPreferences(context)
                .getString(REFRESH_TOKEN_KEY, null)
        } catch (e: Exception) {
            Timber.e(e, "Error retrieving refresh token")
            null
        }
    }

    /**
     * Save user ID.
     */
    fun saveUserId(context: Context, userId: String) {
        try {
            getEncryptedPreferences(context)
                .edit()
                .putString(USER_ID_KEY, userId)
                .apply()
        } catch (e: Exception) {
            Timber.e(e, "Error saving user ID")
        }
    }

    /**
     * Retrieve user ID.
     */
    fun getUserId(context: Context): String? {
        return try {
            getEncryptedPreferences(context)
                .getString(USER_ID_KEY, null)
        } catch (e: Exception) {
            Timber.e(e, "Error retrieving user ID")
            null
        }
    }

    /**
     * Clear all sensitive data.
     */
    fun clearAll(context: Context) {
        try {
            getEncryptedPreferences(context)
                .edit()
                .clear()
                .apply()
            Timber.d("All secure preferences cleared")
        } catch (e: Exception) {
            Timber.e(e, "Error clearing preferences")
        }
    }

    /**
     * Check if token exists.
     */
    fun hasToken(context: Context): Boolean {
        return getToken(context) != null
    }
}