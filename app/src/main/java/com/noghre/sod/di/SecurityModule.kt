package com.noghre.sod.di

import android.content.Context
import com.noghre.sod.security.SecureStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module for security-related components.
 * Provides singleton instances of security utilities.
 *
 * @author Yaser
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    /**
     * Provide SecureStorage singleton.
     * Ensures only one instance of SecureStorage exists throughout app lifecycle.
     *
     * @param context Application context
     * @return SecureStorage instance
     */
    @Provides
    @Singleton
    fun provideSecureStorage(
        @ApplicationContext context: Context
    ): SecureStorage {
        return SecureStorage(context)
    }
}
