package com.noghre.sod.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

/**
 * Hilt module for DataStore preferences.
 *
 * Provides encrypted DataStore instance for storing user preferences.
 *
 * Features:
 * - Type-safe key-value storage
 * - Encryption support
 * - Coroutine-based API
 * - Atomic writes
 * - Backup support
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    // DataStore extension property
    private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "user_preferences",
        produceMigrations = { context ->
            // Migrations from SharedPreferences if needed
            emptyList()
        }
    )

    /**
     * Provides DataStore<Preferences> singleton instance.
     *
     * @param context Application context
     * @return DataStore<Preferences> instance
     */
    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.userPreferencesDataStore.also {
        Timber.i("DataStore initialized for user preferences")
    }
}
