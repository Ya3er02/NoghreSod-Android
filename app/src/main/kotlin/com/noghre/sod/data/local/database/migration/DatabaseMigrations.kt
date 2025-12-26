package com.noghre.sod.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations for schema version updates.
 * Each migration must handle data transformation between versions.
 * 
 * Migration naming: MIGRATION_X_Y where X=fromVersion, Y=toVersion
 * 
 * @author Yaser
 * @version 1.0.0
 */
object DatabaseMigrations {
    
    /**
     * Migration from version 1 to 2:
     * - Add weight_grams column to products table
     * - Add material column to products table
     * - Add discount_percentage column to products table
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add new columns to products table with defaults
            database.execSQL(
                "ALTER TABLE products ADD COLUMN weight_grams REAL DEFAULT 0.0"
            )
            database.execSQL(
                "ALTER TABLE products ADD COLUMN material TEXT DEFAULT ''"
            )
            database.execSQL(
                "ALTER TABLE products ADD COLUMN discount_percentage INTEGER DEFAULT 0"
            )
        }
    }
    
    /**
     * Migration from version 2 to 3:
     * - Add rating_distribution column to products table for rating breakdown
     * - Add last_updated timestamp to products
     */
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE products ADD COLUMN rating_distribution TEXT DEFAULT '{}'"
            )
            database.execSQL(
                "ALTER TABLE products ADD COLUMN last_updated LONG DEFAULT ${System.currentTimeMillis()}"
            )
        }
    }
    
    /**
     * Migration from version 3 to 4:
     * - Create new orders_history table for archiving old orders
     * - Add order_status_history table for tracking order state changes
     */
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create order history tracking table
            database.execSQL(
                """CREATE TABLE IF NOT EXISTS order_status_history (
                    id TEXT PRIMARY KEY NOT NULL,
                    order_id TEXT NOT NULL,
                    old_status TEXT NOT NULL,
                    new_status TEXT NOT NULL,
                    timestamp LONG NOT NULL,
                    FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE
                )"""
            )
            
            // Create index for faster queries
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_order_id ON order_status_history(order_id)"
            )
        }
    }
    
    /**
     * Migration from version 4 to 5:
     * - Add user_reviews table for local caching of reviews
     * - Add review_images table for review images
     */
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """CREATE TABLE IF NOT EXISTS user_reviews (
                    id TEXT PRIMARY KEY NOT NULL,
                    product_id TEXT NOT NULL,
                    user_id TEXT NOT NULL,
                    rating REAL NOT NULL,
                    title TEXT,
                    comment TEXT,
                    helpful_count INTEGER DEFAULT 0,
                    is_verified_purchase INTEGER DEFAULT 0,
                    created_at TEXT,
                    FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE CASCADE,
                    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
                )"""
            )
            
            database.execSQL(
                """CREATE TABLE IF NOT EXISTS review_images (
                    id TEXT PRIMARY KEY NOT NULL,
                    review_id TEXT NOT NULL,
                    image_url TEXT NOT NULL,
                    FOREIGN KEY(review_id) REFERENCES user_reviews(id) ON DELETE CASCADE
                )"""
            )
            
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_product_reviews ON user_reviews(product_id)"
            )
        }
    }
    
    /**
     * Migration from version 5 to 6:
     * - Add wishlist/favorites tracking with timestamps
     * - Add price history for products
     */
    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """CREATE TABLE IF NOT EXISTS favorites (
                    id TEXT PRIMARY KEY NOT NULL,
                    user_id TEXT NOT NULL,
                    product_id TEXT NOT NULL,
                    added_at LONG NOT NULL,
                    UNIQUE(user_id, product_id),
                    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE CASCADE
                )"""
            )
            
            database.execSQL(
                """CREATE TABLE IF NOT EXISTS price_history (
                    id TEXT PRIMARY KEY NOT NULL,
                    product_id TEXT NOT NULL,
                    price REAL NOT NULL,
                    recorded_at LONG NOT NULL,
                    FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE CASCADE
                )"""
            )
        }
    }
    
    /**
     * Get all migration objects as array.
     * This is used in Database.Builder.addMigrations(...)
     */
    fun getAllMigrations(): Array<Migration> = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4,
        MIGRATION_4_5,
        MIGRATION_5_6
    )
    
    /**
     * Get migrations up to specific version.
     */
    fun getMigrationsUpTo(targetVersion: Int): Array<Migration> {
        return getAllMigrations().filter {
            it.endVersion <= targetVersion
        }.toTypedArray()
    }
}
