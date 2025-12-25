package com.noghresod.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * ✅ Safe database migrations
 * ⚠️ NEVER use fallbackToDestructiveMigration()
 */
object DatabaseMigrations {
    
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add discount column to products
            database.execSQL(
                """ALTER TABLE products ADD COLUMN discount_percentage REAL NOT NULL DEFAULT 0.0"""
            )
            
            // Add indices for faster queries
            database.execSQL(
                """CREATE INDEX IF NOT EXISTS idx_products_category ON products(category)"""
            )
            database.execSQL(
                """CREATE INDEX IF NOT EXISTS idx_products_price ON products(price)"""
            )
        }
    }
    
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create product images table
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS product_images (
                    id TEXT PRIMARY KEY NOT NULL,
                    product_id TEXT NOT NULL,
                    url TEXT NOT NULL,
                    is_primary INTEGER NOT NULL DEFAULT 0,
                    sort_order INTEGER NOT NULL DEFAULT 0,
                    created_at INTEGER NOT NULL,
                    FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE CASCADE
                )
                """.trimIndent()
            )
            
            // Create index for faster lookups
            database.execSQL(
                """CREATE INDEX IF NOT EXISTS idx_product_images_product_id ON product_images(product_id)"""
            )
            
            // Migrate existing images from products table
            database.execSQL(
                """
                INSERT INTO product_images (id, product_id, url, is_primary, sort_order, created_at)
                SELECT id || '_img', id, thumbnail_image, 1, 0, created_at
                FROM products
                WHERE thumbnail_image IS NOT NULL
                """.trimIndent()
            )
        }
    }
    
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create new products table with updated schema
            database.execSQL(
                """
                CREATE TABLE products_new (
                    id TEXT PRIMARY KEY NOT NULL,
                    name TEXT NOT NULL,
                    name_fa TEXT NOT NULL,
                    price INTEGER NOT NULL,
                    category TEXT NOT NULL,
                    rating REAL NOT NULL DEFAULT 0.0,
                    in_stock INTEGER NOT NULL DEFAULT 1,
                    discount_percentage REAL NOT NULL DEFAULT 0.0,
                    weight REAL NOT NULL DEFAULT 0.0,
                    description TEXT NOT NULL DEFAULT '',
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL
                )
                """.trimIndent()
            )
            
            // Copy data from old table
            database.execSQL(
                """
                INSERT INTO products_new 
                (id, name, name_fa, price, category, rating, in_stock, discount_percentage, weight, description, created_at, updated_at)
                SELECT 
                    id, name, nameInFarsi, price, category, rating, inStock, 
                    COALESCE(discount_percentage, 0.0), 
                    0.0, 
                    COALESCE(description, ''),
                    createdAt, 
                    COALESCE(updatedAt, createdAt)
                FROM products
                """.trimIndent()
            )
            
            // Drop old table
            database.execSQL("DROP TABLE products")
            
            // Rename new table
            database.execSQL("ALTER TABLE products_new RENAME TO products")
            
            // Recreate indices
            database.execSQL(
                """CREATE INDEX idx_products_category ON products(category)"""
            )
            database.execSQL(
                """CREATE INDEX idx_products_price ON products(price)"""
            )
            database.execSQL(
                """CREATE INDEX idx_products_created_at ON products(created_at)"""
            )
        }
    }
    
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create orders table
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS orders (
                    id TEXT PRIMARY KEY NOT NULL,
                    order_number TEXT NOT NULL UNIQUE,
                    user_id TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'pending',
                    payment_status TEXT NOT NULL DEFAULT 'unpaid',
                    total INTEGER NOT NULL,
                    shipping_address TEXT NOT NULL,
                    created_at INTEGER NOT NULL,
                    estimated_delivery INTEGER,
                    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
                )
                """.trimIndent()
            )
            
            // Create order items table
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS order_items (
                    id TEXT PRIMARY KEY NOT NULL,
                    order_id TEXT NOT NULL,
                    product_id TEXT NOT NULL,
                    product_name TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    price INTEGER NOT NULL,
                    FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE,
                    FOREIGN KEY(product_id) REFERENCES products(id)
                )
                """.trimIndent()
            )
            
            // Create indices
            database.execSQL(
                """CREATE INDEX idx_orders_user_id ON orders(user_id)"""
            )
            database.execSQL(
                """CREATE INDEX idx_orders_status ON orders(status)"""
            )
            database.execSQL(
                """CREATE INDEX idx_order_items_order_id ON order_items(order_id)"""
            )
        }
    }
    
    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create cache table for offline storage
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS cache (
                    key TEXT PRIMARY KEY NOT NULL,
                    value TEXT NOT NULL,
                    expiry_time INTEGER NOT NULL,
                    policy TEXT NOT NULL,
                    created_at INTEGER NOT NULL
                )
                """.trimIndent()
            )
            
            // Create index for fast lookups
            database.execSQL(
                """CREATE INDEX idx_cache_expiry_time ON cache(expiry_time)"""
            )
        }
    }
    
    /**
     * برگرداندن تمام migrations
     */
    fun getAllMigrations(): Array<Migration> {
        return arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6
        )
    }
}

/**
 * استفاده در AppDatabase
 * 
 * @Database(entities = [ProductEntity::class, ...], version = 6)
 * abstract class AppDatabase : RoomDatabase() {
 *     companion object {
 *         @Volatile
 *         private var instance: AppDatabase? = null
 *         
 *         fun getInstance(context: Context): AppDatabase {
 *             return instance ?: synchronized(this) {
 *                 Room.databaseBuilder(
 *                     context.applicationContext,
 *                     AppDatabase::class.java,
 *                     "noghresod.db"
 *                 )
 *                     .addMigrations(*DatabaseMigrations.getAllMigrations())
 *                     .fallbackToDestructiveMigrationOnDowngrade() // ⚠️ فقط برای downgrade
 *                     .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
 *                     .addCallback(DatabaseCallback())
 *                     .build()
 *                     .also { instance = it }
 *             }
 *         }
 *     }
 * }
 */
