package com.noghre.sod.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noghre.sod.data.local.converter.DateTypeConverter
import com.noghre.sod.data.local.converter.ListTypeConverter
import com.noghre.sod.data.local.dao.*
import com.noghre.sod.data.local.entity.*

@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        UserEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        AddressEntity::class,
        FavoriteEntity::class,
        ReviewEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    DateTypeConverter::class,
    ListTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun addressDao(): AddressDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun reviewDao(): ReviewDao
    abstract fun notificationDao(): NotificationDao
}
