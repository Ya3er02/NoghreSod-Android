package com.noghre.sod.di

import android.util.Log
import com.noghre.sod.core.config.AppConfig
import com.noghre.sod.data.local.TokenManager
import com.noghre.sod.data.remote.ApiService
import com.noghre.sod.data.remote.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

/**
 * Hilt dependency injection module for network-related dependencies.
 * Provides OkHttpClient, Retrofit, and API service instances.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides HttpLoggingInterceptor for network request/response logging.
     * Only enabled in debug builds or when explicitly configured.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Log.d(AppConfig.Logging.LOG_TAG, message)
        }.apply {
            level = if (AppConfig.Logging.ENABLE_NETWORK_LOGGING) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * Provides AuthInterceptor for automatic token handling.
     */
    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenManager: TokenManager,
        apiService: ApiService
    ): AuthInterceptor {
        return AuthInterceptor(tokenManager, apiService)
    }

    /**
     * Provides additional interceptor for request/response preprocessing.
     */
    @Provides
    fun provideHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
    }

    /**
     * Provides configured OkHttpClient with all interceptors, timeouts, and security settings.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        headerInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // Interceptors
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            // Timeouts
            .connectTimeout(AppConfig.Api.CONNECT_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(AppConfig.Api.READ_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(AppConfig.Api.WRITE_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            // Retry policy
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Provides Json instance for serialization/deserialization.
     */
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = true
        }
    }

    /**
     * Provides Retrofit instance with OkHttpClient and converters.
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConfig.Api.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    /**
     * Provides ApiService instance from Retrofit.
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}

// Extension for creating MediaType from string
private fun String.toMediaType(): okhttp3.MediaType {
    return okhttp3.MediaType.parse(this) ?: okhttp3.MediaType.parse("application/json")!!
}
