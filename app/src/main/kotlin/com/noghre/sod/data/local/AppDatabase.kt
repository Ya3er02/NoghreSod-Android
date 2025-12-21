package com.noghre.sod.data.local

import android.content.Context
from androidx.room.Database
from androidx.room.Room
from androidx.room.RoomDatabase
from androidx.room.TypeConverters
import com.noghre.sod.data.model.Product
import com.noghre.sod.data.model.CartItem
import com.noghre.sod.data.model.User
import com.noghre.sod.data.model.Category

@Database(
    entities = [Product::class, CartItem::class, User::class, Category::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "noghre_sod_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
