package com.noghre.sod.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.noghre.sod.BuildConfig
import com.noghre.sod.data.local.database.NoghreSodDatabase
import com.noghre.sod.data.remote.api.NoghreSodApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// Extension for DataStore
private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "noghresod_preferences",
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(context, "noghresod_prefs")
        )
    }
)

/**
 * Hilt dependency injection module for application-level singletons.
 * Provides database, API, and network configurations.
 *
 * @author Yaser
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides OkHttpClient with interceptors and configuration.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "NoghreSod-Android/${BuildConfig.APP_VERSION}")

                // Add auth token if available (implement from DataStore)
                requestBuilder.build()
                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Provides HTTP logging interceptor.
     */
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

    /**
     * Provides Retrofit instance.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides Noghresod API service.
     */
    @Provides
    @Singleton
    fun provideNoghreSodApi(retrofit: Retrofit): NoghreSodApi {
        return retrofit.create(NoghreSodApi::class.java)
    }

    /**
     * Provides Room database instance.
     */
    @Provides
    @Singleton
    fun provideNoghreSodDatabase(
        @ApplicationContext context: Context
    ): NoghreSodDatabase {
        return Room.databaseBuilder(
            context,
            NoghreSodDatabase::class.java,
            "noghresod.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Provides DataStore preferences.
     */
    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.preferencesDataStore
    }
}
