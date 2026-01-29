package com.noghre.sod.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.noghre.sod.core.config.AppConfig
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
 * @version 1.1.0
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ============== Gson Configuration ==============\n
    /**
     * Provides Gson instance with custom configuration.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .create()

    // ============== OkHttpClient Configuration ==============\n
    /**
     * Provides OkHttpClient with all interceptors and security settings.
     *
     * Configuration:
     * - Timeouts from AppConfig
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
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                // Timeouts
                connectTimeout(AppConfig.Api.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                readTimeout(AppConfig.Api.READ_TIMEOUT, TimeUnit.SECONDS)
                writeTimeout(AppConfig.Api.WRITE_TIMEOUT, TimeUnit.SECONDS)

                // Connection Pool
                connectionPool(okhttp3.ConnectionPool(8, 5, TimeUnit.MINUTES))

                // Interceptors (order matters)
                // 1. Retry (Application Interceptor - handles retries across network calls)
                addInterceptor(retryInterceptor)
                
                // 2. Logging (Application Interceptor - logs request/response)
                // Only add if logging is enabled
                if (AppConfig.Logging.ENABLE_NETWORK_LOGGING) {
                    addInterceptor(loggingInterceptor)
                }

                // 3. Auth (Network Interceptor - ensures headers are on the wire)
                // Note: Auth can also be an application interceptor, but putting it as network
                // ensures it runs after retry/logging for the actual call.
                // However, usually Auth is better as addInterceptor to survive redirects better.
                // Switching Auth to addInterceptor for stability.
                addInterceptor(authInterceptor)

                // SSL Certificate Pinning
                if (AppConfig.Security.ENABLE_SSL_PINNING) {
                    certificatePinningConfig.getPinningSpec()?.let {
                        certificatePinner(it)
                    }
                }

                // Follow redirects
                followRedirects(true)
                followSslRedirects(true)

                // Retry on connection failure
                retryOnConnectionFailure(true)
            }
            .build()
    }

    // ============== Retrofit Configuration ==============\n
    /**
     * Provides Retrofit instance configured with OkHttpClient.
     *
     * Base URL: [AppConfig.Api.BASE_URL]
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
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConfig.Api.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .also {
                Timber.i("Retrofit configured with base URL: ${AppConfig.Api.BASE_URL}")
            }
    }
}
