package com.noghre.sod.di

import com.noghre.sod.data.repository.CartRepositoryImpl
import com.noghre.sod.data.repository.OrderRepositoryImpl
import com.noghre.sod.data.repository.UserRepositoryImpl
import com.noghre.sod.data.repository.NotificationRepositoryImpl
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.domain.repository.OrderRepository
import com.noghre.sod.domain.repository.UserRepository
import com.noghre.sod.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mockk.mockk

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    fun provideCartRepository(): CartRepository = mockk()

    @Provides
    fun provideOrderRepository(): OrderRepository = mockk()

    @Provides
    fun provideUserRepository(): UserRepository = mockk()

    @Provides
    fun provideNotificationRepository(): NotificationRepository = mockk()
}
