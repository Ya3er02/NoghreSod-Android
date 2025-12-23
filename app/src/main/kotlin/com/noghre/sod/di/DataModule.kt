package com.noghre.sod.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.noghre.sod.data.local.LocalDataSource
import com.noghre.sod.data.local.LocalDataSourceImpl
import com.noghre.sod.data.local.database.AppDatabase
import com.noghre.sod.data.local.database.getAppDatabase
import com.noghre.sod.data.remote.RemoteDataSource
import com.noghre.sod.data.remote.RemoteDataSourceImpl
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.remote.config.RetrofitConfig
import javax.inject.Singleton

/**
 * Hilt Dependency Injection Module
 * Provides singleton instances of data layer components:
 * - Room Database
 * - Retrofit API Service
 * - Local Data Source
 * - Remote Data Source
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    /**
     * Provide singleton AppDatabase instance
     * Uses Room to create SQLite database
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return getAppDatabase(context)
    }

    /**
     * Provide singleton ApiService instance
     * Creates Retrofit service with default configuration
     */
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitConfig.createApiService(
            tokenProvider = { null }, // Token will be provided from preferences
            enableLogging = true // Enable HTTP logging in debug mode
        )
    }

    /**
     * Provide LocalDataSource implementation
     * Uses AppDatabase DAOs for local persistence
     */
    @Provides
    @Singleton
    fun provideLocalDataSource(
        database: AppDatabase
    ): LocalDataSource {
        return LocalDataSourceImpl(database)
    }

    /**
     * Provide RemoteDataSource implementation
     * Uses ApiService for API communication
     */
    @Provides
    @Singleton
    fun provideRemoteDataSource(
        apiService: ApiService
    ): RemoteDataSource {
        return RemoteDataSourceImpl(apiService)
    }
}
