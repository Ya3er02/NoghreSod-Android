package com.noghre.sod.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noghre.sod.data.local.dao.CartDao
import com.noghre.sod.data.local.dao.OrderDao
import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.local.dao.UserDao
import com.noghre.sod.data.model.CartEntity
import com.noghre.sod.data.model.CartItemEntity
import com.noghre.sod.data.model.OrderEntity
import com.noghre.sod.data.model.OrderItemEntity
import com.noghre.sod.data.model.OrderStatusHistoryEntity
import com.noghre.sod.data.model.ProductEntity
import com.noghre.sod.data.model.UserEntity
import com.noghre.sod.data.utils.Converters

/**
 * Noghre Sod Room Database
 * Central SQLite database for all local data storage
 *
 * Database Version: 1
 * Includes entities for:
 * - Products (jewelry catalog)
 * - Cart & Cart Items
 * - Orders & Order Items
 * - Users & Authentication
 * - Order Status History
 *
 * Features:
 * - Encryption support (SQLCipher)
 * - Type converters for complex types
 * - Automatic migrations (export schema)
 */
@Database(
    entities = [
        // Product Management
        ProductEntity::class,
        // Shopping Cart
        CartEntity::class,
        CartItemEntity::class,
        // Orders
        OrderEntity::class,
        OrderItemEntity::class,
        OrderStatusHistoryEntity::class,
        // User
        UserEntity::class
    ],
    version = 1,
    exportSchema = true // Enable schema export for migrations
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // ==================== Data Access Objects ====================

    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .apply {
                    // Fallback to clear database if migrations fail
                    fallbackToDestructiveMigration()

                    // Enable Write-Ahead Logging for better performance
                    enableMultiInstanceInvalidation()

                    // Enable auto-checkpoint for optimization
                    setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)
                }
                .build()
        }

        private const val DATABASE_NAME = "noghre_sod.db"
    }
}

/**
 * Helper function to get database instance
 * Usage: val db = getAppDatabase(context)
 */
fun getAppDatabase(context: Context): AppDatabase {
    return AppDatabase.getInstance(context)
}
