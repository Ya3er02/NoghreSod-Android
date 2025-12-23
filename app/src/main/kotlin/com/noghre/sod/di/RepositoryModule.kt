package com.noghre.sod.di

import com.noghre.sod.data.local.AppDatabase
import com.noghre.sod.data.remote.ApiService
import com.noghre.sod.data.repository.ProductRepositoryImpl
import com.noghre.sod.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named
import javax.inject.Singleton

/**
 * Hilt dependency injection module for repository implementations.
 * Binds interfaces to their implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provides ProductRepository implementation.
     * Injects dependencies for offline-first functionality.
     */
    @Provides
    @Singleton
    fun provideProductRepository(
        apiService: ApiService,
        database: AppDatabase,
        @Named("io") ioDispatcher: CoroutineDispatcher
    ): ProductRepository = ProductRepositoryImpl(
        apiService = apiService,
        database = database,
        ioDispatcher = ioDispatcher
    )
}
