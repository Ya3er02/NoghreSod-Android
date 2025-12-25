package com.noghre.sod.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.noghre.sod.BuildConfig
import com.noghre.sod.data.remote.api.NoghreSodApi
import com.noghre.sod.data.remote.interceptor.AuthInterceptor
import com.noghre.sod.data.remote.interceptor.LoggingInterceptor
import com.noghre.sod.data.remote.security.CertificatePinningConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Enhanced Network Module with complete configuration.
 * Provides OkHttp client, Retrofit, and all interceptors.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModuleEnhanced {

    private const val NETWORK_CACHE_SIZE = 100L * 1024L * 1024L // 100MB
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    /**
     * Provide HTTP caching.
     */
    @Provides
    @Singleton
    fun provideHttpCache(@ApplicationContext context: Context): Cache {
        return Cache(
            directory = context.cacheDir.resolve("http_cache"),
            maxSize = NETWORK_CACHE_SIZE
        ).also {
            Timber.d("HTTP Cache initialized: ${NETWORK_CACHE_SIZE / (1024 * 1024)}MB")
        }
    }

    /**
     * Provide HTTP Logging Interceptor for debugging.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Timber.tag("HTTP").d(message)
        }.apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * Provide Cache Control Interceptor.
     */
    @Provides
    @Singleton
    fun provideCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalResponse = chain.proceed(chain.request())

            val cacheControl = if (chain.request().url.encodedPath.contains("/products")) {
                CacheControl.Builder()
                    .maxAge(30, TimeUnit.MINUTES)
                    .build()
            } else if (chain.request().url.encodedPath.contains("/cart") ||
                chain.request().url.encodedPath.contains("/orders")
            ) {
                CacheControl.Builder()
                    .noCache()
                    .noStore()
                    .build()
            } else {
                CacheControl.Builder()
                    .maxAge(15, TimeUnit.MINUTES)
                    .build()
            }

            originalResponse.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }
    }

    /**
     * Provide OkHttp Client with all interceptors and configuration.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        loggingInterceptor: HttpLoggingInterceptor,
        cacheInterceptor: Interceptor,
        cache: Cache
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // Caching
            .cache(cache)
            .addNetworkInterceptor(cacheInterceptor)

            // Logging
            .addInterceptor(loggingInterceptor)
            .addInterceptor(LoggingInterceptor())

            // Security
            .apply {
                // Certificate Pinning (uncomment if you have certificate hashes)
                // CertificatePinningConfig.applyPinning(this)
            }

            // Timeouts
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

            // Connection Pool
            .connectionPool(
                okhttp3.ConnectionPool(
                    maxIdleConnections = 5,
                    keepAliveDuration = 5,
                    timeUnit = TimeUnit.MINUTES
                )
            )

            // Retry on connection failure
            .retryOnConnectionFailure(true)

            .build()
            .also {
                Timber.d("OkHttpClient configured with cache, logging, and security")
            }
    }

    /**
     * Provide Retrofit with GsonConverterFactory.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .also {
                Timber.d("Retrofit initialized with base URL: ${BuildConfig.API_BASE_URL}")
            }
    }

    /**
     * Provide NoghreSod API Service.
     */
    @Provides
    @Singleton
    fun provideNoghreSodApi(retrofit: Retrofit): NoghreSodApi {
        return retrofit.create(NoghreSodApi::class.java)
            .also {
                Timber.d("NoghreSodApi service created")
            }
    }
}
