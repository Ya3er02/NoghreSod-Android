package com.noghre.sod.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.noghre.sod.BuildConfig
import com.noghre.sod.data.local.SecurePreferences
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.remote.api.AuthApiService
import com.noghre.sod.data.remote.interceptor.AuthInterceptor
import com.noghre.sod.data.remote.interceptor.CertificatePinningInterceptor
import com.noghre.sod.data.remote.interceptor.HeaderInterceptor
import com.noghre.sod.domain.manager.TokenManager
import javax.inject.Named
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

/**
 * Network Module (Hilt)
 * 
 * Provides all network-related dependencies:
 * - OkHttpClient (with interceptors)
 * - Retrofit instances
 * - API services
 * - Certificate pinning
 * - Error handling
 * 
 * Two Retrofit instances:
 * 1. Main ApiService (with auth)
 * 2. AuthApiService (without auth, for refresh)
 * 
 * @since 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val TAG = "NetworkModule"
    private const val TIMEOUT_SECONDS = 30L
    
    // ========================
    // GSON CONFIGURATION
    // ========================
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
    }
    
    // ========================
    // CERTIFICATE PINNING
    // ========================
    
    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            // Primary pin
            .add(
                "api.noghresod.ir",
                "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
            )
            // Backup pin
            .add(
                "api.noghresod.ir",
                "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="
            )
            // Allow subdomains
            .add(
                "*.noghresod.ir",
                "sha256/CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC="
            )
            .build()
    }
    
    // ========================
    // INTERCEPTORS
    // ========================
    
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    
    @Provides
    @Singleton
    fun provideHeaderInterceptor(): HeaderInterceptor {
        return HeaderInterceptor()
    }
    
    @Provides
    @Singleton
    fun provideCertificatePinningInterceptor(): CertificatePinningInterceptor {
        return CertificatePinningInterceptor()
    }
    
    @Provides
    @Singleton
    fun provideAuthInterceptor(
        securePreferences: SecurePreferences,
        tokenManager: TokenManager
    ): AuthInterceptor {
        return AuthInterceptor(securePreferences, tokenManager)
    }
    
    // ========================
    // OKHTTP CLIENTS
    // ========================
    
    /**
     * Main OkHttpClient with all interceptors
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        headerInterceptor: HeaderInterceptor,
        certificatePinningInterceptor: CertificatePinningInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        certificatePinner: CertificatePinner
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // Authentication
            .addInterceptor(authInterceptor)
            
            // Headers (must be after auth to avoid header conflicts)
            .addInterceptor(headerInterceptor)
            
            // Certificate pinning
            .addNetworkInterceptor(certificatePinningInterceptor)
            .certificatePinner(certificatePinner)
            
            // Logging
            .addInterceptor(httpLoggingInterceptor)
            
            // Timeouts
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            
            // Connection pool
            .connectionPool(okhttp3.ConnectionPool(8, 5, TimeUnit.MINUTES))
            
            // Build
            .build()
    }
    
    /**
     * Auth OkHttpClient without AuthInterceptor
     * Used for token refresh to prevent infinite loops
     */
    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthOkHttpClient(
        headerInterceptor: HeaderInterceptor,
        certificatePinningInterceptor: CertificatePinningInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        certificatePinner: CertificatePinner
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // Headers
            .addInterceptor(headerInterceptor)
            
            // Certificate pinning
            .addNetworkInterceptor(certificatePinningInterceptor)
            .certificatePinner(certificatePinner)
            
            // Logging
            .addInterceptor(httpLoggingInterceptor)
            
            // Timeouts (slightly less for auth requests)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            
            // Build
            .build()
    }
    
    // ========================
    // RETROFIT INSTANCES
    // ========================
    
    /**
     * Main Retrofit instance with authentication
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * Auth Retrofit instance without authentication
     */
    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(
        @Named("auth") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    // ========================
    // API SERVICES
    // ========================
    
    /**
     * Main API Service with authentication
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    
    /**
     * Auth API Service without authentication
     * Used for login, register, and token refresh
     */
    @Provides
    @Singleton
    fun provideAuthApiService(@Named("auth") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}
