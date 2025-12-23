package com.noghre.sod.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.noghre.sod.data.local.dao.*
import com.noghre.sod.data.local.entity.*

/**
 * Room Database for Noghresod application
 * Handles local data persistence for products, orders, cart, and user information
 */
@Database(
    entities = [
        // Product entities
        ProductEntity::class,
        ProductReviewEntity::class,
        FavoriteProductEntity::class,
        // Cart entities
        CartEntity::class,
        CartItemEntity::class,
        SavedCartEntity::class,
        // Order entities
        OrderEntity::class,
        OrderTrackingEntity::class,
        ReturnRequestEntity::class,
        // User entities
        UserEntity::class,
        AddressEntity::class,
        UserPreferencesEntity::class,
        AuthTokenEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "noghresod_database"
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            return instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME,
            )
                .fallbackToDestructiveMigration() // For development
                .build()
                .also { instance = it }
        }
    }
}
