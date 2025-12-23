package com.noghre.sod.data.remote.client

import android.content.Context
import com.noghre.sod.BuildConfig
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.remote.interceptor.AuthInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import java.util.concurrent.TimeUnit

/**
 * Factory for creating Retrofit instances with proper configuration
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.noghresod.com/v1/" // Update with actual API URL
    private const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB
    private const val CONNECTION_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    fun createApiService(
        context: Context,
        authInterceptor: AuthInterceptor,
    ): ApiService {
        val httpClient = createOkHttpClient(context, authInterceptor)
        return createRetrofit(httpClient).create(ApiService::class.java)
    }

    private fun createOkHttpClient(
        context: Context,
        authInterceptor: AuthInterceptor,
    ): OkHttpClient {
        val cache = Cache(context.cacheDir, CACHE_SIZE)

        val httpClientBuilder = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)

        // Add logging interceptor only in debug builds
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            httpClientBuilder.addInterceptor(loggingInterceptor)
        }

        return httpClientBuilder.build()
    }

    private fun createRetrofit(httpClient: OkHttpClient): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
