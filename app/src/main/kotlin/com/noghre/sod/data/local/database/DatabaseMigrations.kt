package com.noghre.sod.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import timber.log.Timber

/**
 * Database migration strategies for Room database versioning.
 */
object DatabaseMigrations {

    /**
     * Migration from version 1 to 2: Add payment status column to orders.
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            Timber.d("Executing migration 1 -> 2")
            database.execSQL(
                "ALTER TABLE orders ADD COLUMN payment_status TEXT NOT NULL DEFAULT 'PENDING'"
            )
        }
    }

    /**
     * Migration from version 2 to 3: Add discount field to products.
     */
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            Timber.d("Executing migration 2 -> 3")
            database.execSQL(
                "ALTER TABLE products ADD COLUMN discount_percentage REAL NOT NULL DEFAULT 0.0"
            )
        }
    }

    /**
     * Migration from version 3 to 4: Add last_updated timestamp to all entities.
     */
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            Timber.d("Executing migration 3 -> 4")
            
            database.execSQL(
                "ALTER TABLE products ADD COLUMN last_updated INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}"
            )
            database.execSQL(
                "ALTER TABLE users ADD COLUMN last_updated INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}"
            )
            database.execSQL(
                "ALTER TABLE orders ADD COLUMN last_updated INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}"
            )
        }
    }

    /**
     * Migration from version 4 to 5: Create backup table for deleted orders.
     */
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            Timber.d("Executing migration 4 -> 5")
            
            // Create backup table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS orders_backup (" +
                "id TEXT NOT NULL PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "total_amount REAL NOT NULL," +
                "order_status TEXT NOT NULL," +
                "created_at TEXT NOT NULL," +
                "backup_timestamp INTEGER NOT NULL" +
                ")"
            )
        }
    }

    /**
     * Get all migrations for the database.
     */
    fun getAllMigrations(): Array<Migration> {
        return arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5
        )
    }
}
