package com.noghre.sod.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.noghre.sod.core.config.ApiEndpoints
import com.noghre.sod.core.network.AuthInterceptor
import com.noghre.sod.core.network.CertificatePinningConfig
import com.noghre.sod.core.network.LoggingInterceptor
import com.noghre.sod.core.network.RetryInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for Network dependencies.
 *
 * Provides Retrofit, OkHttpClient, and all interceptors.
 *
 * Features:
 * - SSL Certificate Pinning for security
 * - Token-based Authentication
 * - Automatic Request Retry
 * - Request/Response Logging
 * - Timeout Configuration
 * - Connection Pooling
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ============== Gson Configuration ==============

    /**
     * Provides Gson instance with custom configuration.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .create()

    // ============== OkHttpClient Configuration ==============

    /**
     * Provides OkHttpClient with all interceptors and security settings.
     *
     * Configuration:
     * - Connection Timeout: 30 seconds
     * - Read Timeout: 30 seconds
     * - Write Timeout: 30 seconds
     * - Certificate Pinning: Enabled
     * - Interceptors: Auth, Logging, Retry
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: LoggingInterceptor,
        retryInterceptor: RetryInterceptor,
        certificatePinningConfig: CertificatePinningConfig
    ): OkHttpClient = OkHttpClient.Builder()
        .apply {
            // Timeouts
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)

            // Connection Pool
            connectionPool(okhttp3.ConnectionPool(8, 5, TimeUnit.MINUTES))

            // Interceptors (order matters)
            // Retry interceptor first (outermost)
            addInterceptor(retryInterceptor)
            // Logging interceptor
            addInterceptor(loggingInterceptor)
            // Auth interceptor (must be after logging)
            addNetworkInterceptor(authInterceptor)

            // SSL Certificate Pinning
            certificatePinningConfig.getPinningSpec()?.let {
                certificatePinner(it)
            }

            // Follow redirects
            followRedirects(true)
            followSslRedirects(true)

            // Retry on connection failure
            retryOnConnectionFailure(true)
        }
        .build()

    // ============== Retrofit Configuration ==============

    /**
     * Provides Retrofit instance configured with OkHttpClient.
     *
     * Base URL: [ApiEndpoints.BASE_URL]
     * Converter Factory: Gson
     *
     * @param okHttpClient Configured OkHttpClient
     * @param gson Gson instance
     * @return Retrofit instance
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit = Retrofit.Builder()
        .baseUrl(ApiEndpoints.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .also {
            Timber.i("Retrofit configured with base URL: ${ApiEndpoints.BASE_URL}")
        }
}
