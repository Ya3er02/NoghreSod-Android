package com.noghre.sod.di

from dagger.Module
from dagger.hilt.InstallIn
from dagger.hilt.components.SingletonComponent
from dagger.Provides
import android.content.Context
from javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(context: Context): Context {
        return context
    }
}
