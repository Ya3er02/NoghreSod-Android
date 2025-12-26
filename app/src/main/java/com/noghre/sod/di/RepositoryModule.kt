package com.noghre.sod.di

import com.noghre.sod.data.repository.AuthRepositoryImpl
import com.noghre.sod.data.repository.CartRepositoryImpl
import com.noghre.sod.data.repository.OrderRepositoryImpl
import com.noghre.sod.data.repository.ProductRepositoryImpl
import com.noghre.sod.data.repository.UserRepositoryImpl
import com.noghre.sod.domain.repository.AuthRepository
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.domain.repository.OrderRepository
import com.noghre.sod.domain.repository.ProductRepository
import com.noghre.sod.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository implementations to interfaces.
 * Ensures clean architecture separation between domain and data layers.
 *
 * Each repository is bound as a Singleton to maintain single instance across app.
 * This allows for consistent data access and state management.
 *
 * Repositories included:
 * - ProductRepository: Product catalog and details
 * - UserRepository: User profile and account management
 * - CartRepository: Shopping cart operations
 * - OrderRepository: Order history and tracking
 * - AuthRepository: Authentication and authorization
 *
 * @author Yaser
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * Bind ProductRepository implementation to ProductRepository interface.
     * Used for fetching products, categories, and product details.
     */
    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository
    
    /**
     * Bind UserRepository implementation to UserRepository interface.
     * Used for user profile, settings, and account operations.
     */
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
    
    /**
     * Bind CartRepository implementation to CartRepository interface.
     * Used for shopping cart add/remove/update operations.
     */
    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository
    
    /**
     * Bind OrderRepository implementation to OrderRepository interface.
     * Used for order history, tracking, and management.
     */
    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository
    
    /**
     * Bind AuthRepository implementation to AuthRepository interface.
     * Used for login, registration, token management.
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}
