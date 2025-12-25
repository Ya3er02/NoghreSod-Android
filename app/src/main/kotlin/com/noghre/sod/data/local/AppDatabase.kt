package com.noghre.sod.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.noghre.sod.data.local.entity.AddressEntity
import com.noghre.sod.data.local.entity.CartItemEntity
import com.noghre.sod.data.local.entity.OrderEntity
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.local.entity.UserEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.google.gson.Gson
import androidx.room.TypeConverter

// ============================================
// 💾 Type Converters
// ============================================

class Converters {
    private val gson = Gson()
    
    /**
     * Convert List<String> to JSON string
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return if (value == null) "[]" else gson.toJson(value)
    }
    
    /**
     * Convert JSON string to List<String>
     */
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList()
        else gson.fromJson(value, Array<String>::class.java).toList()
    }
    
    /**
     * Convert Map<String, String> to JSON
     */
    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String {
        return if (value == null) "{}" else gson.toJson(value)
    }
    
    /**
     * Convert JSON to Map<String, String>
     */
    @TypeConverter
    fun toStringMap(value: String): Map<String, String> {
        return if (value.isEmpty()) emptyMap()
        else gson.fromJson(value, Map::class.java) as Map<String, String>
    }
    
    /**
     * Convert LocalDateTime to String
     */
    @TypeConverter
    fun fromLocalDateTime(value: java.time.LocalDateTime?): String? {
        return value?.toString()
    }
    
    /**
     * Convert String to LocalDateTime
     */
    @TypeConverter
    fun toLocalDateTime(value: String?): java.time.LocalDateTime? {
        return if (value == null) null else java.time.LocalDateTime.parse(value)
    }
    
    /**
     * Convert LocalDate to String
     */
    @TypeConverter
    fun fromLocalDate(value: java.time.LocalDate?): String? {
        return value?.toString()
    }
    
    /**
     * Convert String to LocalDate
     */
    @TypeConverter
    fun toLocalDate(value: String?): java.time.LocalDate? {
        return if (value == null) null else java.time.LocalDate.parse(value)
    }
}

// ============================================
// 💾 Database Migrations
// ============================================

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new column for product rating
        database.execSQL(
            "ALTER TABLE products ADD COLUMN rating REAL NOT NULL DEFAULT 0.0"
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create wishlist table
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS wishlist (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                product_id TEXT NOT NULL,
                added_at INTEGER NOT NULL,
                UNIQUE(product_id)
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new columns for product enhancements
        database.execSQL(
            "ALTER TABLE products ADD COLUMN is_featured INTEGER NOT NULL DEFAULT 0"
        )
        database.execSQL(
            "ALTER TABLE products ADD COLUMN discount_percentage REAL"
        )
        database.execSQL(
            "ALTER TABLE products ADD COLUMN last_updated INTEGER NOT NULL DEFAULT 0"
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create new order tracking table
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS order_tracking (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                order_id TEXT NOT NULL,
                status TEXT NOT NULL,
                location TEXT,
                timestamp INTEGER NOT NULL,
                FOREIGN KEY(order_id) REFERENCES orders(id)
            )
            """.trimIndent()
        )
    }
}

// ============================================
// 💾 Room Database
// ============================================

@Database(
    entities = [
        ProductEntity::class,
        UserEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        AddressEntity::class
    ],
    version = 5,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Get Product DAO
     */
    abstract fun productDao(): ProductDao
    
    /**
     * Get User DAO
     */
    abstract fun userDao(): UserDao
    
    /**
     * Get Cart DAO
     */
    abstract fun cartDao(): CartDao
    
    /**
     * Get Order DAO
     */
    abstract fun orderDao(): OrderDao
    
    /**
     * Get Address DAO
     */
    abstract fun addressDao(): AddressDao
    
    companion object {
        const val DATABASE_NAME = "noghresod_database"
    }
}

// ============================================
// 🌧️ Hilt Database Module
// ============================================

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provide singleton AppDatabase instance
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        // Add migrations
        .addMigrations(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5
        )
        // Only for development - use migrations in production
        .fallbackToDestructiveMigration() // Remove in production!
        .build()
    }
    
    /**
     * Provide ProductDao
     */
    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }
    
    /**
     * Provide UserDao
     */
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
    
    /**
     * Provide CartDao
     */
    @Provides
    @Singleton
    fun provideCartDao(database: AppDatabase): CartDao {
        return database.cartDao()
    }
    
    /**
     * Provide OrderDao
     */
    @Provides
    @Singleton
    fun provideOrderDao(database: AppDatabase): OrderDao {
        return database.orderDao()
    }
    
    /**
     * Provide AddressDao
     */
    @Provides
    @Singleton
    fun provideAddressDao(database: AppDatabase): AddressDao {
        return database.addressDao()
    }
}
