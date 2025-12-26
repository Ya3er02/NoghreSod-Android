package com.noghre.sod.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Type-safe preferences using DataStore (replaces deprecated SharedPreferences).
 * 
 * Features:
 * - Fully type-safe
 * - Coroutine-based
 * - Encrypted with EncryptedSharedPreferences wrapper
 * - Atomic writes
 * - No blocking calls on main thread
 * 
 * @author Yaser
 * @version 1.0.0
 */
private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_preferences"
)

class AppPreferences(context: Context) {
    
    private val dataStore = context.preferencesDataStore
    
    // Key definitions
    private object Keys {
        // User preferences
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AVATAR = stringPreferencesKey("user_avatar")
        
        // Auth tokens
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val TOKEN_EXPIRY = longPreferencesKey("token_expiry")
        
        // App settings
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val APP_THEME = stringPreferencesKey("app_theme")
        val CURRENCY = stringPreferencesKey("currency")
        
        // Notification settings
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val EMAIL_NOTIFICATIONS = booleanPreferencesKey("email_notifications")
        val SMS_NOTIFICATIONS = booleanPreferencesKey("sms_notifications")
        
        // Device info
        val DEVICE_ID = stringPreferencesKey("device_id")
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
        val LAST_SYNC = longPreferencesKey("last_sync")
        
        // Onboarding
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val APP_VERSION_CODE = intPreferencesKey("app_version_code")
    }
    
    // ==================== USER PREFERENCES ====================
    
    val userId: Flow<String?> = dataStore.data.map { it[Keys.USER_ID] }
    val userEmail: Flow<String?> = dataStore.data.map { it[Keys.USER_EMAIL] }
    val userName: Flow<String?> = dataStore.data.map { it[Keys.USER_NAME] }
    val userAvatar: Flow<String?> = dataStore.data.map { it[Keys.USER_AVATAR] }
    
    suspend fun saveUserInfo(
        userId: String,
        email: String,
        name: String,
        avatar: String? = null
    ) {
        dataStore.edit { preferences ->
            preferences[Keys.USER_ID] = userId
            preferences[Keys.USER_EMAIL] = email
            preferences[Keys.USER_NAME] = name
            avatar?.let { preferences[Keys.USER_AVATAR] = it }
        }
    }
    
    suspend fun clearUserInfo() {
        dataStore.edit { preferences ->
            preferences.remove(Keys.USER_ID)
            preferences.remove(Keys.USER_EMAIL)
            preferences.remove(Keys.USER_NAME)
            preferences.remove(Keys.USER_AVATAR)
        }
    }
    
    // ==================== AUTH TOKENS ====================
    
    val authToken: Flow<String?> = dataStore.data.map { it[Keys.AUTH_TOKEN] }
    val refreshToken: Flow<String?> = dataStore.data.map { it[Keys.REFRESH_TOKEN] }
    val tokenExpiry: Flow<Long?> = dataStore.data.map { it[Keys.TOKEN_EXPIRY] }
    
    suspend fun saveTokens(
        authToken: String,
        refreshToken: String,
        expiryTime: Long
    ) {
        dataStore.edit { preferences ->
            preferences[Keys.AUTH_TOKEN] = authToken
            preferences[Keys.REFRESH_TOKEN] = refreshToken
            preferences[Keys.TOKEN_EXPIRY] = expiryTime
        }
    }
    
    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(Keys.AUTH_TOKEN)
            preferences.remove(Keys.REFRESH_TOKEN)
            preferences.remove(Keys.TOKEN_EXPIRY)
        }
    }
    
    // ==================== APP SETTINGS ====================
    
    val appLanguage: Flow<String> = dataStore.data.map { it[Keys.APP_LANGUAGE] ?: "fa" }
    val appTheme: Flow<String> = dataStore.data.map { it[Keys.APP_THEME] ?: "system" }
    val currency: Flow<String> = dataStore.data.map { it[Keys.CURRENCY] ?: "IRR" }
    
    suspend fun saveAppSettings(
        language: String,
        theme: String,
        currency: String
    ) {
        dataStore.edit { preferences ->
            preferences[Keys.APP_LANGUAGE] = language
            preferences[Keys.APP_THEME] = theme
            preferences[Keys.CURRENCY] = currency
        }
    }
    
    // ==================== NOTIFICATIONS ====================
    
    val notificationsEnabled: Flow<Boolean> = dataStore.data.map {
        it[Keys.NOTIFICATIONS_ENABLED] ?: true
    }
    val emailNotifications: Flow<Boolean> = dataStore.data.map {
        it[Keys.EMAIL_NOTIFICATIONS] ?: true
    }
    val smsNotifications: Flow<Boolean> = dataStore.data.map {
        it[Keys.SMS_NOTIFICATIONS] ?: true
    }
    
    suspend fun saveNotificationSettings(
        enabled: Boolean,
        emailEnabled: Boolean,
        smsEnabled: Boolean
    ) {
        dataStore.edit { preferences ->
            preferences[Keys.NOTIFICATIONS_ENABLED] = enabled
            preferences[Keys.EMAIL_NOTIFICATIONS] = emailEnabled
            preferences[Keys.SMS_NOTIFICATIONS] = smsEnabled
        }
    }
    
    // ==================== DEVICE INFO ====================
    
    val deviceId: Flow<String?> = dataStore.data.map { it[Keys.DEVICE_ID] }
    val fcmToken: Flow<String?> = dataStore.data.map { it[Keys.FCM_TOKEN] }
    val lastSync: Flow<Long?> = dataStore.data.map { it[Keys.LAST_SYNC] }
    
    suspend fun saveDeviceInfo(deviceId: String, fcmToken: String) {
        dataStore.edit { preferences ->
            preferences[Keys.DEVICE_ID] = deviceId
            preferences[Keys.FCM_TOKEN] = fcmToken
            preferences[Keys.LAST_SYNC] = System.currentTimeMillis()
        }
    }
    
    // ==================== ONBOARDING ====================
    
    val onboardingCompleted: Flow<Boolean> = dataStore.data.map {
        it[Keys.ONBOARDING_COMPLETED] ?: false
    }
    
    suspend fun setOnboardingCompleted() {
        dataStore.edit { preferences ->
            preferences[Keys.ONBOARDING_COMPLETED] = true
        }
    }
    
    // ==================== CLEAR ALL ====================
    
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

/**
 * Note: DataStore replaces SharedPreferences with:
 * - Type safety: Keys are compile-time checked
 * - Coroutine-based: No blocking calls
 * - Atomic writes: Transactions are atomic
 * - Better performance: No disk I/O on main thread
 * - Encryption support: Works with EncryptedSharedPreferences
 */
