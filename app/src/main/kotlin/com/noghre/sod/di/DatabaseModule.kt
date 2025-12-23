package com.noghre.sod.di

import android.content.Context
import com.noghre.sod.data.local.AppDatabase
import com.noghre.sod.data.local.dao.CategoryDao
import com.noghre.sod.data.local.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module for database-related dependencies.
 * Provides Room database instance and all DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides singleton instance of AppDatabase.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = AppDatabase.getInstance(context)

    /**
     * Provides ProductDao from AppDatabase.
     */
    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()

    /**
     * Provides CategoryDao from AppDatabase.
     */
    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()
}
