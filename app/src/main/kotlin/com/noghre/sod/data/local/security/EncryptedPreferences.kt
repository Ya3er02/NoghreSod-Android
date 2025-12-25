package com.noghre.sod.data.local.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure wrapper for storing sensitive user data.
 * Uses Android Keystore for encryption.
 */
@Singleton
class EncryptedPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFERENCES_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // ============== TOKEN MANAGEMENT ==============

    fun saveAccessToken(token: String) {
        try {
            encryptedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
            Timber.d("Access token saved securely")
        } catch (e: Exception) {
            Timber.e(e, "Error saving access token")
        }
    }

    fun getAccessToken(): String? {
        return try {
            encryptedPreferences.getString(KEY_ACCESS_TOKEN, null)
        } catch (e: Exception) {
            Timber.e(e, "Error retrieving access token")
            null
        }
    }

    fun clearAccessToken() {
        try {
            encryptedPreferences.edit().remove(KEY_ACCESS_TOKEN).apply()
        } catch (e: Exception) {
            Timber.e(e, "Error clearing access token")
        }
    }

    // ============== REFRESH TOKEN MANAGEMENT ==============

    fun saveRefreshToken(token: String) {
        try {
            encryptedPreferences.edit().putString(KEY_REFRESH_TOKEN, token).apply()
            Timber.d("Refresh token saved securely")
        } catch (e: Exception) {
            Timber.e(e, "Error saving refresh token")
        }
    }

    fun getRefreshToken(): String? {
        return try {
            encryptedPreferences.getString(KEY_REFRESH_TOKEN, null)
        } catch (e: Exception) {
            Timber.e(e, "Error retrieving refresh token")
            null
        }
    }

    fun clearRefreshToken() {
        try {
            encryptedPreferences.edit().remove(KEY_REFRESH_TOKEN).apply()
        } catch (e: Exception) {
            Timber.e(e, "Error clearing refresh token")
        }
    }

    // ============== USER DATA ==============

    fun saveUserId(userId: String) {
        try {
            encryptedPreferences.edit().putString(KEY_USER_ID, userId).apply()
        } catch (e: Exception) {
            Timber.e(e, "Error saving user ID")
        }
    }

    fun getUserId(): String? {
        return try {
            encryptedPreferences.getString(KEY_USER_ID, null)
        } catch (e: Exception) {
            Timber.e(e, "Error retrieving user ID")
            null
        }
    }

    fun saveUserEmail(email: String) {
        try {
            encryptedPreferences.edit().putString(KEY_USER_EMAIL, email).apply()
        } catch (e: Exception) {
            Timber.e(e, "Error saving user email")
        }
    }

    fun getUserEmail(): String? {
        return try {
            encryptedPreferences.getString(KEY_USER_EMAIL, null)
        } catch (e: Exception) {
            Timber.e(e, "Error retrieving user email")
            null
        }
    }

    // ============== PAYMENT DATA ==============

    fun savePaymentMethodId(methodId: String) {
        try {
            encryptedPreferences.edit().putString(KEY_PAYMENT_METHOD, methodId).apply()
            Timber.d("Payment method saved securely")
        } catch (e: Exception) {
            Timber.e(e, "Error saving payment method")
        }
    }

    fun getPaymentMethodId(): String? {
        return try {
            encryptedPreferences.getString(KEY_PAYMENT_METHOD, null)
        } catch (e: Exception) {
            Timber.e(e, "Error retrieving payment method")
            null
        }
    }

    // ============== CLEAR ALL ==============

    fun clearAllSecureData() {
        try {
            encryptedPreferences.edit().clear().apply()
            Timber.d("All secure data cleared")
        } catch (e: Exception) {
            Timber.e(e, "Error clearing secure data")
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "noghresod_encrypted_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_PAYMENT_METHOD = "payment_method"
    }
}
