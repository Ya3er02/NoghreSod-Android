package com.noghre.sod.di

import com.noghre.sod.BuildConfig
import com.noghre.sod.data.payment.ZarinpalPaymentService
import com.noghre.sod.data.remote.api.ZarinpalApi
import com.noghre.sod.data.repository.PaymentRepositoryImpl
import com.noghre.sod.domain.repository.PaymentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Hilt module for payment-related dependencies
 * Provides all payment services and repositories
 */
@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {
    
    /**
     * Provide Zarinpal API service
     */
    @Singleton
    @Provides
    fun provideZarinpalApi(retrofit: Retrofit): ZarinpalApi {
        // Use sandbox in debug builds, production in release
        val baseUrl = if (BuildConfig.DEBUG) {
            ZarinpalApi.SANDBOX_URL
        } else {
            ZarinpalApi.BASE_URL
        }
        
        return retrofit
            .newBuilder()
            .baseUrl(baseUrl)
            .build()
            .create(ZarinpalApi::class.java)
    }
    
    /**
     * Provide Zarinpal payment service
     */
    @Singleton
    @Provides
    fun provideZarinpalPaymentService(
        api: ZarinpalApi
    ): ZarinpalPaymentService {
        return ZarinpalPaymentService(api)
    }
    
    /**
     * Provide Payment Repository implementation
     */
    @Singleton
    @Provides
    fun providePaymentRepository(
        zarinpalService: ZarinpalPaymentService
    ): PaymentRepository {
        return PaymentRepositoryImpl(
            zarinpalService = zarinpalService
        )
    }
}