package com.noghre.sod.core.di

import com.noghre.sod.data.local.db.cart.CartDao
import com.noghre.sod.data.local.db.order.OrderDao
import com.noghre.sod.data.local.db.product.ProductDao
import com.noghre.sod.data.local.preferences.PreferencesManager
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.repository.AuthRepositoryImpl
import com.noghre.sod.data.repository.CartRepositoryImpl
import com.noghre.sod.data.repository.OrderRepositoryImpl
import com.noghre.sod.data.repository.ProductRepositoryImpl
import com.noghre.sod.domain.repository.AuthRepository
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.domain.repository.OrderRepository
import com.noghre.sod.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt DI module for repository bindings.
 * 
* Provides all repository implementations to the dependency injection container.
 * All repositories are singletons for app lifecycle.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provide ProductRepository singleton.
     */
    @Singleton
    @Provides
    fun provideProductRepository(
        apiService: ApiService,
        productDao: ProductDao
    ): ProductRepository {
        return ProductRepositoryImpl(apiService, productDao)
    }

    /**
     * Provide CartRepository singleton.
     */
    @Singleton
    @Provides
    fun provideCartRepository(
        cartDao: CartDao
    ): CartRepository {
        return CartRepositoryImpl(cartDao)
    }

    /**
     * Provide OrderRepository singleton.
     */
    @Singleton
    @Provides
    fun provideOrderRepository(
        apiService: ApiService,
        orderDao: OrderDao
    ): OrderRepository {
        return OrderRepositoryImpl(apiService, orderDao)
    }

    /**
     * Provide AuthRepository singleton.
     */
    @Singleton
    @Provides
    fun provideAuthRepository(
        apiService: ApiService,
        preferencesManager: PreferencesManager
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, preferencesManager)
    }
}
