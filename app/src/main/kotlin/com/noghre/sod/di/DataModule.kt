package com.noghre.sod.di

import android.content.Context
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.noghre.sod.data.local.database.NoghreSodDatabase
import com.noghre.sod.data.local.prefs.TokenManager
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.interceptor.AuthInterceptor
import com.noghre.sod.data.remote.interceptor.ErrorInterceptor
import com.noghre.sod.data.remote.interceptor.NetworkInterceptor
import com.noghre.sod.data.remote.network.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt Module for Data Layer Dependency Injection.
 * Provides singleton instances for API, Database, and related components.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    private const val BASE_URL = "https://api.noghresod.com/api/" // Replace with actual URL
    private const val TIMEOUT_SECONDS = 30L

    /**
     * Provide Gson instance with custom configuration.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create()
    }

    /**
     * Provide EncryptedSharedPreferences for secure token storage.
     */
    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context
    ): android.content.SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "noghre_sod_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Provide TokenManager for authentication token handling.
     */
    @Provides
    @Singleton
    fun provideTokenManager(
        encryptedPrefs: android.content.SharedPreferences
    ): TokenManager {
        return TokenManager(encryptedPrefs)
    }

    /**
     * Provide NetworkMonitor for connectivity observation.
     */
    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor {
        return NetworkMonitor(context)
    }

    /**
     * Provide Room Database instance.
     */
    @Provides
    @Singleton
    fun provideNoghreSodDatabase(
        @ApplicationContext context: Context
    ): NoghreSodDatabase {
        return NoghreSodDatabase.getInstance(context)
    }

    /**
     * Provide OkHttpClient with interceptors.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        errorInterceptor: ErrorInterceptor,
        networkInterceptor: NetworkInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(networkInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(errorInterceptor)
            .addInterceptor(HttpLoggingInterceptor { message ->
                Timber.tag("OkHttp").d(message)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Provide Retrofit instance for API calls.
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Provide NoghreSodApiService.
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): NoghreSodApiService {
        return retrofit.create(NoghreSodApiService::class.java)
    }
}
