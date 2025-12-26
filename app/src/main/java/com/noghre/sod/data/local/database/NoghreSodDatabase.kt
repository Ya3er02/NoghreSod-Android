package com.noghre.sod.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noghre.sod.data.local.dao.*
import com.noghre.sod.data.local.entity.*
import timber.log.Timber

/**
 * Room database for Noghre Sod e-commerce app.
 * Manages all local data persistence.
 *
 * Database Info:
 * - Name: noghresod.db
 * - Version: 1
 * - Entities: 13 (products, users, addresses, carts, cart_items, orders, order_items,
 *             categories, payments, reviews, notifications)
 *
 * Features:
 * - Type converters for complex types (List, etc.)
 * - Foreign key constraints with cascading deletes
 * - Proper indexing for performance
 * - All DAOs for CRUD operations
 *
 * @author Yaser
 * @version 1.0.0
 */
@Database(
    entities = [
        ProductEntity::class,
        UserEntity::class,
        AddressEntity::class,
        CartEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CategoryEntity::class,
        PaymentEntity::class,
        ReviewEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false // Set to true in production for schema versioning
)
@TypeConverters(DatabaseConverters::class)
abstract class NoghreSodDatabase : RoomDatabase() {

    // ============== DAO ACCESSORS ==============

    /**
     * Get ProductDao for product operations.
     */
    abstract fun productDao(): ProductDao

    /**
     * Get UserDao for user and address operations.
     */
    abstract fun userDao(): UserDao

    /**
     * Get CartDao for cart operations.
     */
    abstract fun cartDao(): CartDao

    /**
     * Get OrderDao for order operations.
     */
    abstract fun orderDao(): OrderDao

    /**
     * Get CategoryDao for category operations.
     */
    abstract fun categoryDao(): CategoryDao

    /**
     * Get PaymentDao for payment operations.
     */
    abstract fun paymentDao(): PaymentDao

    /**
     * Get ReviewDao for review operations.
     */
    abstract fun reviewDao(): ReviewDao

    /**
     * Get NotificationDao for notification operations.
     */
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: NoghreSodDatabase? = null

        /**
         * Get or create database instance.
         * Thread-safe singleton using double-checked locking.
         *
         * @param context Application context
         * @return NoghreSodDatabase singleton instance
         */
        fun getInstance(context: Context): NoghreSodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoghreSodDatabase::class.java,
                    "noghresod.db"
                ).build()
                INSTANCE = instance
                Timber.d("Database instance created")
                instance
            }
        }
    }
}
