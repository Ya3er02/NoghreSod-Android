package com.noghre.sod.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations for schema updates.
 * Each migration should be added to AppDatabase.databaseBuilder().
 */

/**
 * Migration from database version 1 to 2.
 * Adds rating column to products table.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE products ADD COLUMN rating REAL NOT NULL DEFAULT 0.0"
        )
    }
}

/**
 * Migration from database version 2 to 3.
 * Adds wishlist table for saving favorite products.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create wishlist table
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS wishlist (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                product_id TEXT NOT NULL,
                user_id TEXT NOT NULL,
                added_at INTEGER NOT NULL,
                UNIQUE(product_id, user_id)
            )
            """.trimIndent()
        )
        
        // Create index for faster queries
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_wishlist_user_id ON wishlist(user_id)"
        )
    }
}

/**
 * Migration from database version 3 to 4.
 * Adds search_history table for tracking user searches.
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS search_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                query TEXT NOT NULL,
                user_id TEXT NOT NULL,
                searched_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_search_history_user_id ON search_history(user_id)"
        )
    }
}

/**
 * Migration from database version 4 to 5.
 * Adds review_count and discount columns to products.
 */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE products ADD COLUMN review_count INTEGER NOT NULL DEFAULT 0"
        )
        
        database.execSQL(
            "ALTER TABLE products ADD COLUMN discount_percentage REAL NOT NULL DEFAULT 0.0"
        )
    }
}

/**
 * List of all migrations in order.
 * Use when configuring Room database:
 * 
 * Room.databaseBuilder(...)
 *     .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
 *     .build()
 */
val ALL_MIGRATIONS = arrayOf(
    MIGRATION_1_2,
    MIGRATION_2_3,
    MIGRATION_3_4,
    MIGRATION_4_5
)