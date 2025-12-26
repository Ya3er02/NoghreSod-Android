package com.noghre.sod.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.noghre.sod.BuildConfig
import com.noghre.sod.data.remote.interceptor.AuthInterceptor
import com.noghre.sod.data.remote.interceptor.ErrorInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifier for Auth Interceptor.
 * Used to distinguish the auth interceptor from other interceptors.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorQualifier

/**
 * Qualifier for Error Interceptor.
 * Used to distinguish the error interceptor from other interceptors.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ErrorInterceptorQualifier

/**
 * Hilt module for network layer configuration.
 * Provides Retrofit, OkHttp, and custom interceptors.
 *
 * Configuration includes:
 * - Certificate Pinning for HTTPS security
 * - Gson with lenient parsing and date formatting
 * - HttpLoggingInterceptor for request/response logging (debug only)
 * - Custom AuthInterceptor for adding auth tokens
 * - Custom ErrorInterceptor for handling API errors
 * - OkHttpClient with timeouts and retry logic
 * - Retrofit with Gson converter factory
 *
 * @author Yaser
 * @version 1.1.0
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L
    
    // ✅ CERTIFICATE PINS (Issue #2)
    // These are public key hashes from SSL certificates
    // Get them from: https://tools.keycdn.com/certificate-key-validator
    private const val PIN_ISRG_ROOT_X1 = "r/mIkG3eEpVdm+u/8IAcLvkTw7ohWL+5s/stw7OTZHM="
    private const val PIN_LETS_ENCRYPT_R3 = "MK8KPuCvosKaQB0GY6xvh8cOFw1ltBmD89ZwIaJLcKk="
    private const val PIN_DST_ROOT_CA_X3 = "iQMGa1Z59FIEf86ystaWIcNqposECNq1UetQ2kvkMKI="
    
    /**
     * Provide CertificatePinner for SSL certificate validation.
     * Prevents man-in-the-middle attacks by pinning expected certificates.
     *
     * @return CertificatePinner instance
     */
    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            // Production API
            .add("api.noghresod.ir", "sha256/$PIN_ISRG_ROOT_X1")
            .add("api.noghresod.ir", "sha256/$PIN_LETS_ENCRYPT_R3")
            .add("api.noghresod.ir", "sha256/$PIN_DST_ROOT_CA_X3")  // Backup
            
            // Staging API
            .add("api-staging.noghresod.ir", "sha256/$PIN_ISRG_ROOT_X1")
            .add("api-staging.noghresod.ir", "sha256/$PIN_LETS_ENCRYPT_R3")
            .add("api-staging.noghresod.ir", "sha256/$PIN_DST_ROOT_CA_X3")  // Backup
            
            .build()
    }
    
    /**
     * Provide Gson instance for JSON serialization/deserialization.
     * Configured with lenient parsing and proper date formatting.
     *
     * @return Gson instance
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient() // Allow JSON parsing errors
            .serializeNulls() // Include null values in serialization
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") // ISO 8601 format
            .create()
    }
    
    /**
     * Provide HttpLoggingInterceptor for request/response logging.
     * In debug mode, logs full request/response bodies.
     * In release mode, no logging.
     *
     * @return HttpLoggingInterceptor instance
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG_LOGGING) {
                HttpLoggingInterceptor.Level.BODY // Log request/response bodies
            } else {
                HttpLoggingInterceptor.Level.NONE // No logging in release
            }
        }
    }
    
    /**
     * Provide AuthInterceptor for adding authentication tokens.
     * Used for intercepting requests and adding auth headers.
     *
     * @return Interceptor instance
     */
    @Provides
    @Singleton
    @AuthInterceptorQualifier
    fun provideAuthInterceptor(): Interceptor {
        return AuthInterceptor()
    }
    
    /**
     * Provide ErrorInterceptor for handling API errors.
     * Used for intercepting responses and handling error codes.
     *
     * @return Interceptor instance
     */
    @Provides
    @Singleton
    @ErrorInterceptorQualifier
    fun provideErrorInterceptor(): Interceptor {
        return ErrorInterceptor()
    }
    
    /**
     * Provide OkHttpClient with custom configuration.
     * Includes interceptors, timeouts, certificate pinning, and retry logic.
     *
     * @param httpLoggingInterceptor For request/response logging
     * @param authInterceptor For adding auth tokens
     * @param errorInterceptor For handling errors
     * @param certificatePinner For SSL certificate validation
     * @return Configured OkHttpClient
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        @AuthInterceptorQualifier authInterceptor: Interceptor,
        @ErrorInterceptorQualifier errorInterceptor: Interceptor,
        certificatePinner: CertificatePinner
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // ✅ Add certificate pinning (Issue #2)
            .certificatePinner(certificatePinner)
            
            // Add custom interceptors
            .addInterceptor(authInterceptor)
            .addInterceptor(errorInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            
            // Add default headers
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Accept-Language", "fa-IR")
                    .addHeader("User-Agent", "NoghreSod-Android/${BuildConfig.VERSION_NAME}")
                    .build()
                chain.proceed(request)
            }
            
            // Set timeouts
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            
            // Enable retry on connection failure
            .retryOnConnectionFailure(true)
            .build()
    }
    
    /**
     * Provide Retrofit instance for API calls.
     * Configured with OkHttpClient and Gson converter.
     *
     * @param okHttpClient The configured OkHttpClient
     * @param gson The configured Gson instance
     * @return Retrofit instance
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
}
