package com.noghre.sod.di

import com.noghre.sod.domain.repository.*
import com.noghre.sod.domain.usecase.auth.*
import com.noghre.sod.domain.usecase.cart.*
import com.noghre.sod.domain.usecase.order.*
import com.noghre.sod.domain.usecase.product.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // ==================== Product Use Cases ====================

    @Provides
    @Singleton
    fun provideGetAllProductsUseCase(
        productRepository: ProductRepository,
    ): GetAllProductsUseCase {
        return GetAllProductsUseCase(productRepository)
    }

    @Provides
    @Singleton
    fun provideGetProductByIdUseCase(
        productRepository: ProductRepository,
    ): GetProductByIdUseCase {
        return GetProductByIdUseCase(productRepository)
    }

    @Provides
    @Singleton
    fun provideSearchProductsUseCase(
        productRepository: ProductRepository,
    ): SearchProductsUseCase {
        return SearchProductsUseCase(productRepository)
    }

    @Provides
    @Singleton
    fun provideGetFeaturedProductsUseCase(
        productRepository: ProductRepository,
    ): GetFeaturedProductsUseCase {
        return GetFeaturedProductsUseCase(productRepository)
    }

    // ==================== Cart Use Cases ====================

    @Provides
    @Singleton
    fun provideGetCartUseCase(
        cartRepository: CartRepository,
    ): GetCartUseCase {
        return GetCartUseCase(cartRepository)
    }

    @Provides
    @Singleton
    fun provideAddToCartUseCase(
        cartRepository: CartRepository,
    ): AddToCartUseCase {
        return AddToCartUseCase(cartRepository)
    }

    // ==================== Order Use Cases ====================

    @Provides
    @Singleton
    fun provideCreateOrderUseCase(
        orderRepository: OrderRepository,
    ): CreateOrderUseCase {
        return CreateOrderUseCase(orderRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserOrdersUseCase(
        orderRepository: OrderRepository,
    ): GetUserOrdersUseCase {
        return GetUserOrdersUseCase(orderRepository)
    }

    // ==================== Auth Use Cases ====================

    @Provides
    @Singleton
    fun provideLoginUseCase(
        authRepository: AuthRepository,
    ): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(
        authRepository: AuthRepository,
    ): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        authRepository: AuthRepository,
    ): LogoutUseCase {
        return LogoutUseCase(authRepository)
    }
}
