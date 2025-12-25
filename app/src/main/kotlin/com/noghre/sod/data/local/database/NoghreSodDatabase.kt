package com.noghre.sod.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.local.entity.CartEntity
import com.noghre.sod.data.local.entity.CartItemEntity
import com.noghre.sod.data.local.entity.OrderEntity
import com.noghre.sod.data.local.entity.OrderItemEntity
import com.noghre.sod.data.local.entity.UserEntity
import com.noghre.sod.data.local.entity.AddressEntity
import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.local.dao.CartDao
import com.noghre.sod.data.local.dao.OrderDao
import com.noghre.sod.data.local.dao.UserDao

/**
 * Room database for Noghresod Android app.
 * Contains all local database entities and DAOs.
 *
 * @author Yaser
 * @version 1.0.0
 */
@Database(
    entities = [
        ProductEntity::class,
        CartEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        UserEntity::class,
        AddressEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class NoghreSodDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao
}
