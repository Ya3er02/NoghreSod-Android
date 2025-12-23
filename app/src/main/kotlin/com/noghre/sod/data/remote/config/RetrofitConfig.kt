package com.noghre.sod.data.remote.config

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import com.noghre.sod.data.remote.interceptor.AuthInterceptor
import com.noghre.sod.data.remote.interceptor.HeaderInterceptor
import java.util.concurrent.TimeUnit

/**
 * Retrofit Configuration
 * Builds and configures Retrofit instance with OkHttp client
 * Includes interceptors for authentication, headers, and logging
 */
object RetrofitConfig {

    const val BASE_URL = "https://api.noghresod.ir/v1/"
    const val TIMEOUT = 30L
    const val MAX_RETRIES = 3

    /**
     * Create JSON serialization configuration
     * Allows unknown fields for forward compatibility
     */
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    /**
     * Build OkHttp client with interceptors
     * @param tokenProvider Lambda to provide current auth token
     * @param enableLogging Whether to enable HTTP logging
     */
    fun createOkHttpClient(
        tokenProvider: () -> String? = { null },
        enableLogging: Boolean = false
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                // Add authentication interceptor
                addInterceptor(AuthInterceptor(tokenProvider))

                // Add default headers interceptor
                addInterceptor(HeaderInterceptor())

                // Add logging interceptor if enabled
                if (enableLogging) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }

                // Set timeouts
                connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                readTimeout(TIMEOUT, TimeUnit.SECONDS)
                writeTimeout(TIMEOUT, TimeUnit.SECONDS)

                // Configure connection pooling
                connectionPool(okhttp3.ConnectionPool(8, 5, TimeUnit.MINUTES))

                // Disable certificate pinning for now (enable in production)
                // addNetworkInterceptor(CertificatePinningInterceptor())
            }
            .build()
    }

    /**
     * Create Retrofit instance
     * @param tokenProvider Lambda to provide current auth token
     * @param enableLogging Whether to enable HTTP logging
     */
    fun createRetrofit(
        tokenProvider: () -> String? = { null },
        enableLogging: Boolean = false,
        baseUrl: String = BASE_URL
    ): Retrofit {
        val httpClient = createOkHttpClient(tokenProvider, enableLogging)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    /**
     * Create API service instance
     * @param tokenProvider Lambda to provide current auth token
     * @param enableLogging Whether to enable HTTP logging
     */
    inline fun <reified T> createApiService(
        tokenProvider: () -> String? = { null },
        enableLogging: Boolean = false
    ): T {
        return createRetrofit(tokenProvider, enableLogging)
            .create(T::class.java)
    }
}
