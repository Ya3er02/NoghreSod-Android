package com.noghresod.network

import android.content.Context
import com.noghresod.network.api.ApiService
import com.noghresod.network.api.ApiVersion
import com.noghresod.network.cache.AdvancedCacheManager
import com.noghresod.network.interceptor.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    // ==================== JSON Serialization ====================
    
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
        }
    }
    
    // ==================== Cache Manager ====================
    
    @Provides
    @Singleton
    fun provideCacheManager(
        @ApplicationContext context: Context
    ): AdvancedCacheManager {
        return AdvancedCacheManager(context)
    }
    
    // ==================== Interceptors ====================
    
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): AdvancedLoggingInterceptor {
        return AdvancedLoggingInterceptor()
    }
    
    @Provides
    @Singleton
    fun provideRateLimitInterceptor(): RateLimitInterceptor {
        return RateLimitInterceptor()
    }
    
    @Provides
    @Singleton
    fun provideRetryInterceptor(): RetryInterceptor {
        return RetryInterceptor(
            maxRetries = 3,
            initialDelay = 1000 // 1 second
        )
    }
    
    // ==================== OkHttp Client ====================
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: AdvancedLoggingInterceptor,
        rateLimitInterceptor: RateLimitInterceptor,
        retryInterceptor: RetryInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // ✅ Request/Response logging
            .addInterceptor(loggingInterceptor)
            
            // ✅ Rate limiting
            .addInterceptor(rateLimitInterceptor)
            
            // ✅ Network retry with exponential backoff
            .addNetworkInterceptor(retryInterceptor)
            
            // ✅ Request timeouts
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            
            // ✅ Connection pooling
            .connectionPool(
                okhttp3.ConnectionPool(
                    maxIdleConnections = 5,
                    keepAliveDuration = 5,
                    TimeUnit.MINUTES
                )
            )
            
            // ✅ Certificate pinning (optional, for production)
            // .certificatePinner(provideCertificatePinner())
            
            .build()
    }
    
    // ==================== Retrofit Instance ====================
    
    @Provides
    @Singleton
    fun provideRetrofit(
        json: Json,
        okHttpClient: OkHttpClient
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        
        return Retrofit.Builder()
            .baseUrl(ApiVersion.getBaseUrl(ApiVersion.CURRENT))
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
    
    // ==================== API Service ====================
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    
    // ==================== Optional: Versioned APIs ====================
    
    @Provides
    @Singleton
    @Named("apiV1")
    fun provideApiServiceV1(
        json: Json,
        okHttpClient: OkHttpClient
    ): ApiService {
        val contentType = "application/json".toMediaType()
        
        return Retrofit.Builder()
            .baseUrl(ApiVersion.getBaseUrl(ApiVersion.V1))
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ApiService::class.java)
    }
    
    @Provides
    @Singleton
    @Named("apiV2")
    fun provideApiServiceV2(
        json: Json,
        okHttpClient: OkHttpClient
    ): ApiService {
        val contentType = "application/json".toMediaType()
        
        return Retrofit.Builder()
            .baseUrl(ApiVersion.getBaseUrl(ApiVersion.V2))
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ApiService::class.java)
    }
}

/**
 * استفاده در ViewModels:
 * 
 * @HiltViewModel
 * class ProductViewModel @Inject constructor(
 *     private val apiService: ApiService,
 *     private val cacheManager: AdvancedCacheManager
 * ) : ViewModel() {
 *     
 *     fun getProducts(
 *         page: Int = 1,
 *         pageSize: Int = 20
 *     ) {
 *         viewModelScope.launch {
 *             try {
 *                 val response = apiService.getAllProducts(page, pageSize)
 *                 
 *                 if (response.isSuccessful) {
 *                     val data = response.body()?.data
 *                     if (data != null) {
 *                         // ✅ Cache products
 *                         cacheManager.putCache(
 *                             key = "products_${page}_${pageSize}",
 *                             data = data,
 *                             policy = CachePolicy.TimeToLive(5.minutes.inWholeMilliseconds)
 *                         )
 *                         
 *                         _uiState.value = UiState.Success(data)
 *                     }
 *                 } else {
 *                     // ⚠️ Handle error
 *                     _uiState.value = UiState.Error(response.code(), response.message())
 *                 }
 *             } catch (e: Exception) {
 *                 Timber.e(e, "Failed to fetch products")
 *                 _uiState.value = UiState.Error(exception = e)
 *             }
 *         }
 *     }
 * }
 */
