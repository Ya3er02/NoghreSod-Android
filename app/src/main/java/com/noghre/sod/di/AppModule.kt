package com.noghre.sod.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.noghre.sod.BuildConfig
import com.noghre.sod.data.local.database.NoghreSodDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

/**
 * Hilt module for application-wide singleton dependencies.
 * Provides the main Room database instance.
 *
 * Database configuration:
 * - Name: noghresod.db
 * - Version: 1 (increment on schema changes)
 * - Migration strategy: Destructive in debug, proper migrations in release
 * - Callbacks: Logging on create and open
 *
 * @author Yaser
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provide the main Room database instance.
     * Configured with proper migration strategy based on build type.
     *
     * In DEBUG builds:
     * - Uses fallbackToDestructiveMigration() for easy development
     * - Database is recreated on schema changes
     *
     * In RELEASE builds:
     * - Crashes if migration fails so developers are aware
     * - Requires explicit migration strategies (TODO)
     * - Should use proper database migrations
     *
     * @param context Application context
     * @return NoghreSodDatabase instance
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
            .apply {
                if (BuildConfig.DEBUG) {
                    // In development, allow destructive migrations for faster iteration
                    fallbackToDestructiveMigration()
                    Timber.d("Room database: Using fallbackToDestructiveMigration")
                } else {
                    // In production, crash if migration fails so we know about it
                    // This forces developers to write proper migrations
                    // TODO: Add migration strategies here
                    // Example:
                    // addMigrations(MIGRATION_1_2, MIGRATION_2_3, ...)
                    Timber.d("Room database: Using strict migration strategy")
                }
            }
            // Add database callbacks for logging
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Timber.d("Room database created")
                    // TODO: Pre-populate database with initial data if needed
                    // Example: seed initial categories, settings, etc.
                }
                
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    Timber.d("Room database opened - version: ${db.version}")
                }
            })
            .build()
    }
}
