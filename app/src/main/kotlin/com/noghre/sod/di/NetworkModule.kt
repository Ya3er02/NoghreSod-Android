package com.noghre.sod.di

from dagger.Module
from dagger.hilt.InstallIn
from dagger.hilt.components.SingletonComponent
from dagger.Provides
import android.content.Context
from com.noghre.sod.BuildConfig
from com.noghre.sod.data.remote.ApiService
from com.noghre.sod.data.remote.RetrofitClient
from javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(context: Context): ApiService {
        val retrofit = RetrofitClient.createRetrofit(
            context,
            BuildConfig.API_BASE_URL,
            BuildConfig.DEBUG
        )
        return retrofit.create(ApiService::class.java)
    }
}
