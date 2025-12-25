package com.noghre.sod.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.local.dao.UserDao
import com.noghre.sod.data.local.dao.CartDao
import com.noghre.sod.data.local.dao.OrderDao
import com.noghre.sod.data.local.dao.AddressDao
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.local.entity.UserEntity
import com.noghre.sod.data.local.entity.CartItemEntity
import com.noghre.sod.data.local.entity.OrderEntity
import com.noghre.sod.data.local.entity.AddressEntity

/**
 * Room Database for NoghreSod application.
 * Provides offline-first data persistence.
 */
@Database(
    entities = [
        ProductEntity::class,
        UserEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        AddressEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun addressDao(): AddressDao

    companion object {
        const val DATABASE_NAME = "noghresod_db"
    }
}