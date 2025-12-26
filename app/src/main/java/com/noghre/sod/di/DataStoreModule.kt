package com.noghre.sod.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifier for User Settings DataStore.
 * Used for storing user preferences and UI settings.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserSettingsDataStore

/**
 * Qualifier for Auth DataStore.
 * Used for storing authentication tokens and session data.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthDataStore

/**
 * Qualifier for Cache DataStore.
 * Used for storing temporary cache data.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CacheDataStore

/**
 * Qualifier for Encrypted SharedPreferences.
 * Used for storing sensitive data like passwords.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EncryptedPreferencesQualifier

// DataStore extensions for easy access
private val Context.userSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_settings"
)

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "auth_data"
)

private val Context.cacheDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "cache_data"
)

/**
 * Hilt module for DataStore instances.
 * Provides separate DataStores for different data types.
 *
 * DataStores provided:
 * - UserSettingsDataStore: For user preferences and UI settings
 * - AuthDataStore: For authentication tokens and session data
 * - CacheDataStore: For temporary cache data
 * - EncryptedSharedPreferences: For sensitive encrypted data
 *
 * DataStore advantages over SharedPreferences:
 * - Type-safe
 * - Coroutine-based (non-blocking)
 * - Atomic writes
 * - Migration support
 * - Transactional consistency
 *
 * @author Yaser
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    
    /**
     * Provide UserSettingsDataStore for storing user preferences.
     * Used for storing UI settings, language preferences, theme selection, etc.
     *
     * @param context Application context
     * @return DataStore<Preferences> for user settings
     */
    @Provides
    @Singleton
    @UserSettingsDataStore
    fun provideUserSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.userSettingsDataStore
    }
    
    /**
     * Provide AuthDataStore for storing authentication data.
     * Used for storing auth tokens, refresh tokens, and session information.
     *
     * @param context Application context
     * @return DataStore<Preferences> for auth data
     */
    @Provides
    @Singleton
    @AuthDataStore
    fun provideAuthDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.authDataStore
    }
    
    /**
     * Provide CacheDataStore for storing temporary cache data.
     * Used for caching API responses and computed values.
     *
     * @param context Application context
     * @return DataStore<Preferences> for cache data
     */
    @Provides
    @Singleton
    @CacheDataStore
    fun provideCacheDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.cacheDataStore
    }
    
    /**
     * Provide EncryptedSharedPreferences for sensitive data storage.
     * Uses Android Security crypto library for encryption.
     *
     * Encryption details:
     * - Master Key Scheme: AES256_GCM
     * - Key Encryption: AES256_SIV
     * - Value Encryption: AES256_GCM
     *
     * @param context Application context
     * @return Encrypted SharedPreferences instance
     */
    @Provides
    @Singleton
    @EncryptedPreferencesQualifier
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        // Create master encryption key
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        // Create encrypted shared preferences
        return EncryptedSharedPreferences.create(
            context,
            "noghresod_encrypted_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
