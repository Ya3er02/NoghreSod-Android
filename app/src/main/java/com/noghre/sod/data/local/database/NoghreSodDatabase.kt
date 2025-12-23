package com.noghre.sod.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.noghre.sod.data.local.dao.AddressDao
import com.noghre.sod.data.local.dao.CartDao
import com.noghre.sod.data.local.dao.CategoryDao
import com.noghre.sod.data.local.dao.OrderDao
import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.local.dao.UserDao
import com.noghre.sod.data.local.entity.AddressEntity
import com.noghre.sod.data.local.entity.CartEntity
import com.noghre.sod.data.local.entity.CategoryEntity
import com.noghre.sod.data.local.entity.OrderEntity
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.local.entity.UserEntity

/**
 * Room database for NoghreSod jewelry e-commerce application
 * Handles local caching and offline-first data storage
 */
@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        CartEntity::class,
        UserEntity::class,
        OrderEntity::class,
        AddressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NoghreSodDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun cartDao(): CartDao
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun addressDao(): AddressDao

    companion object {
        @Volatile
        private var INSTANCE: NoghreSodDatabase? = null

        fun getInstance(context: Context): NoghreSodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoghreSodDatabase::class.java,
                    "noghresod_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}