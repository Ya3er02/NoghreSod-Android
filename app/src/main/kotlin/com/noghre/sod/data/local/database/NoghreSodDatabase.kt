package com.noghre.sod.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noghre.sod.data.local.dao.CartDao
import com.noghre.sod.data.local.dao.CategoryDao
import com.noghre.sod.data.local.dao.FavoriteDao
import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.local.dao.SearchHistoryDao
import com.noghre.sod.data.local.dao.UserDao
import com.noghre.sod.data.local.entity.CartItemEntity
import com.noghre.sod.data.local.entity.CategoryEntity
import com.noghre.sod.data.local.entity.FavoriteEntity
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.local.entity.SearchHistoryEntity
import com.noghre.sod.data.local.entity.UserEntity

/**
 * Room Database for NoghreSod application.
 * Manages all local data storage with version control for migrations.
 */
@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        CartItemEntity::class,
        FavoriteEntity::class,
        SearchHistoryEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NoghreSodDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun cartDao(): CartDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "noghre_sod.db"
        private var INSTANCE: NoghreSodDatabase? = null

        /**
         * Get database singleton instance.
         */
        fun getInstance(context: Context): NoghreSodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoghreSodDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
