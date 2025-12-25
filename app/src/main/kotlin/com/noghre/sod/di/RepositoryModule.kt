package com.noghre.sod.di

import com.noghre.sod.data.repository.IProductRepository
import com.noghre.sod.data.repository.ProductRepository
import com.noghre.sod.data.remote.api.NoghreSodApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for repository bindings.
 * Provides concrete implementations of repository interfaces.
 *
 * @author Yaser
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provides ProductRepository implementation.
     */
    @Provides
    @Singleton
    fun provideProductRepository(
        api: NoghreSodApi
    ): IProductRepository {
        return ProductRepository(api)
    }

    // Additional repositories will be added here
    // - CartRepository
    // - OrderRepository
    // - UserRepository
    // - PaymentRepository
    // - AuthRepository
}
