package com.noghre.sod.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.noghre.sod.data.local.dao.CategoryDao
import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.local.entity.CategoryEntity
import com.noghre.sod.data.local.entity.ProductEntity

/**
 * Room database for NoghreSod application.
 * Manages local caching of products, categories, and other data.
 *
 * Version: 1
 * Entities: ProductEntity, CategoryEntity
 *
 * Database migration strategy:
 * - Version 1 (current): Initial schema with products and categories
 * - Future versions: Add migrations if schema changes
 */
@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Returns the ProductDao instance.
     */
    abstract fun productDao(): ProductDao

    /**
     * Returns the CategoryDao instance.
     */
    abstract fun categoryDao(): CategoryDao

    companion object {
        private const val DATABASE_NAME = "noghresod.db"

        @Volatile
        private var instance: AppDatabase? = null

        /**
         * Gets the singleton instance of AppDatabase.
         * Uses double-checked locking for thread safety.
         */
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        /**
         * Builds the Room database with proper configuration.
         * Includes migrations and callback for database creation.
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                // Allow queries on main thread (only for development)
                // .allowMainThreadQueries()
                
                // Add fallback to destructive migration for development
                // Disable in production!
                // .fallbackToDestructiveMigration()
                
                // Callback for database creation/open
                .addCallback(DatabaseCallback())
                
                .build()
        }

        /**
         * Database callback for initial setup and migrations.
         */
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Database created - you can insert default data here if needed
                // For example: pre-populate categories
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Database opened - initialize if needed
                // Check foreign key constraints are enabled
                db.execSQL("PRAGMA foreign_keys=ON")
            }
        }
    }
}

// ============ DATABASE MIGRATIONS (for future versions) ============

/**
 * Migration from version 1 to version 2 (example for future use).
 * Uncomment and update when you need to change the schema.
 * 
 * Example:
 * val MIGRATION_1_2 = object : Migration(1, 2) {
 *     override fun migrate(database: SupportSQLiteDatabase) {
 *         // Add new table or modify existing tables
 *         database.execSQL("ALTER TABLE products ADD COLUMN newColumn TEXT")
 *     }
 * }
 * 
 * Then add to Room builder:
 * .addMigrations(MIGRATION_1_2)
 */
