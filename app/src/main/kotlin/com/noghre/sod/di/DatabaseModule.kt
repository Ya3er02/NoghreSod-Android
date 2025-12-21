package com.noghre.sod.di

from dagger.Module
from dagger.hilt.InstallIn
from dagger.hilt.components.SingletonComponent
from dagger.Provides
import android.content.Context
from com.noghre.sod.data.local.AppDatabase
from com.noghre.sod.data.local.ProductDao
from com.noghre.sod.data.local.CartDao
from com.noghre.sod.data.local.UserDao
from com.noghre.sod.data.local.CategoryDao
from javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideCartDao(database: AppDatabase): CartDao {
        return database.cartDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }
}
