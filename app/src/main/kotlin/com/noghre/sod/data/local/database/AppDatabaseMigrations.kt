package com.noghre.sod.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import timber.log.Timber

/**
 * Database migrations for Room database version upgrades.
 * Each migration should be idempotent and handle data loss gracefully.
 */

// Migration from version 1 to 2: Add product rating column
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.d("Running migration from version 1 to 2")
        
        // Add new column with default value
        database.execSQL(
            "ALTER TABLE products ADD COLUMN rating REAL NOT NULL DEFAULT 0.0"
        )
        
        Timber.d("Migration 1 to 2 completed successfully")
    }
}

// Migration from version 2 to 3: Add product discount column and create index
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.d("Running migration from version 2 to 3")
        
        // Add discount column
        database.execSQL(
            "ALTER TABLE products ADD COLUMN discount_percentage REAL NOT NULL DEFAULT 0.0"
        )
        
        // Create index for better search performance
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_product_category ON products(category)"
        )
        
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_product_name ON products(name)"
        )
        
        Timber.d("Migration 2 to 3 completed successfully")
    }
}

// Migration from version 3 to 4: Add user preferences table
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.d("Running migration from version 3 to 4")
        
        // Create new user preferences table
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS user_preferences (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                user_id TEXT NOT NULL UNIQUE,
                theme TEXT NOT NULL DEFAULT 'light',
                language TEXT NOT NULL DEFAULT 'en',
                notifications_enabled INTEGER NOT NULL DEFAULT 1,
                last_updated INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            )
            """
        )
        
        // Create index on user_id
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_user_preferences_user_id ON user_preferences(user_id)"
        )
        
        Timber.d("Migration 3 to 4 completed successfully")
    }
}

// Migration from version 4 to 5: Add product images table (one-to-many relationship)
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.d("Running migration from version 4 to 5")
        
        // Create product images table
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS product_images (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                product_id TEXT NOT NULL,
                image_url TEXT NOT NULL,
                is_primary INTEGER NOT NULL DEFAULT 0,
                position INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE CASCADE
            )
            """
        )
        
        // Create indexes
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_product_images_product_id ON product_images(product_id)"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_product_images_primary ON product_images(is_primary)"
        )
        
        Timber.d("Migration 4 to 5 completed successfully")
    }
}

// Migration from version 5 to 6: Add search history table
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.d("Running migration from version 5 to 6")
        
        // Create search history table
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS search_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                query TEXT NOT NULL UNIQUE,
                timestamp INTEGER NOT NULL DEFAULT 0,
                count INTEGER NOT NULL DEFAULT 1
            )
            """
        )
        
        // Create index for recent searches
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_search_history_timestamp ON search_history(timestamp DESC)"
        )
        
        Timber.d("Migration 5 to 6 completed successfully")
    }
}

// Collection of all migrations
val ALL_MIGRATIONS = arrayOf(
    MIGRATION_1_2,
    MIGRATION_2_3,
    MIGRATION_3_4,
    MIGRATION_4_5,
    MIGRATION_5_6
)

/**
 * Validates database integrity after migration.
 * Should be called after migration to ensure data consistency.
 */
fun validateDatabaseIntegrity(database: SupportSQLiteDatabase) {
    try {
        // Check if all required tables exist
        val cursor = database.query(
            "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name"
        )
        val tables = mutableSetOf<String>()
        while (cursor.moveToNext()) {
            tables.add(cursor.getString(0))
        }
        cursor.close()
        
        // Verify required tables
        val requiredTables = setOf("products", "users", "search_history")
        requiredTables.forEach { table ->
            if (table !in tables) {
                Timber.w("Expected table '$table' not found after migration")
            }
        }
        
        Timber.d("Database integrity validated. Tables: $tables")
    } catch (e: Exception) {
        Timber.e(e, "Error validating database integrity")
    }
}
