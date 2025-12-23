package com.noghre.sod.di

import android.content.Context
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.remote.client.RetrofitClient
import com.noghre.sod.data.remote.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        @ApplicationContext context: Context,
    ): AuthInterceptor {
        val sharedPreferences = context.getSharedPreferences(
            "NoghresodPreferences",
            Context.MODE_PRIVATE
        )
        return AuthInterceptor(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideApiService(
        @ApplicationContext context: Context,
        authInterceptor: AuthInterceptor,
    ): ApiService {
        return RetrofitClient.createApiService(context, authInterceptor)
    }
}
