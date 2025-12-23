package com.noghre.sod.di

import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.repository.*
import com.noghre.sod.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(
        apiService: ApiService,
    ): ProductRepository {
        return ProductRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideCartRepository(
        apiService: ApiService,
    ): CartRepository {
        return CartRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(
        apiService: ApiService,
    ): OrderRepository {
        return OrderRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
    ): AuthRepository {
        return AuthRepositoryImpl(apiService)
    }
}
