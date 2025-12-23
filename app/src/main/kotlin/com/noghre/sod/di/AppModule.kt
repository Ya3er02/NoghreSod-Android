package com.noghre.sod.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.noghre.sod.data.local.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton
import androidx.datastore.preferences.core.Preferences

/**
 * Hilt dependency injection module for application-level dependencies.
 * Provides context, DataStore, TokenManager, and Coroutine Dispatchers.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the application context.
     */
    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ): Context = context

    /**
     * Provides IO Dispatcher for background operations.
     */
    @Provides
    @Singleton
    @Named("io")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides Main Dispatcher for UI operations.
     */
    @Provides
    @Singleton
    @Named("main")
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    /**
     * Provides Default Dispatcher for computation-heavy operations.
     */
    @Provides
    @Singleton
    @Named("default")
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * Provides DataStore for preferences storage.
     * Migrates from SharedPreferences if they exist.
     */
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = null,
            migrations = listOf(
                SharedPreferencesMigration(context, "noghresod_prefs")
            ),
            scope = kotlinx.coroutines.CoroutineScope(
                kotlinx.coroutines.Dispatchers.IO + kotlinx.coroutines.Job()
            ),
            produceFile = { context.preferencesDataStoreFile("noghresod_preferences") }
        )
    }

    /**
     * Provides TokenManager for secure token storage and management.
     */
    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context
    ): TokenManager = TokenManager(context)
}
