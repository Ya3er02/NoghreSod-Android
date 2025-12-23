package com.noghre.sod.data.di

import com.noghre.sod.data.repository.CartRepositoryImpl
import com.noghre.sod.data.repository.OrderRepositoryImpl
import com.noghre.sod.data.repository.UserRepositoryImpl
import com.noghre.sod.data.repository.NotificationRepositoryImpl
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.domain.repository.OrderRepository
import com.noghre.sod.domain.repository.UserRepository
import com.noghre.sod.domain.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository

    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository
}
