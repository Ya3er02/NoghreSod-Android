package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * Product Data Access Object (DAO)
 * Handles all product-related database operations
 */
@Dao
interface ProductDao {

    // ==================== INSERT Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    // ==================== QUERY Operations ====================

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductByIdFlow(productId: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query(
        """SELECT * FROM products 
           WHERE categoryId = :categoryId 
           ORDER BY createdAt DESC"""
    )
    fun getProductsByCategory(categoryId: String): Flow<List<ProductEntity>>

    @Query(
        """SELECT * FROM products 
           WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' 
           OR LOWER(nameFA) LIKE '%' || LOWER(:query) || '%'
           ORDER BY createdAt DESC"""
    )
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query(
        """SELECT * FROM products 
           WHERE purity = :purity 
           ORDER BY createdAt DESC"""
    )
    fun getProductsByPurity(purity: String): Flow<List<ProductEntity>>

    @Query(
        """SELECT * FROM products 
           WHERE isFavorite = 1 
           ORDER BY createdAt DESC"""
    )
    fun getFavoriteProducts(): Flow<List<ProductEntity>>

    @Query(
        """SELECT * FROM products 
           WHERE isNew = 1 
           ORDER BY createdAt DESC 
           LIMIT :limit"""
    )
    fun getNewProducts(limit: Int = 10): Flow<List<ProductEntity>>

    @Query(
        """SELECT * FROM products 
           WHERE discount > 0 
           ORDER BY discount DESC 
           LIMIT :limit"""
    )
    fun getDiscountedProducts(limit: Int = 10): Flow<List<ProductEntity>>

    @Query(
        """SELECT * FROM products 
           WHERE stock > 0 
           ORDER BY createdAt DESC"""
    )
    fun getInStockProducts(): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    @Query(
        """SELECT * FROM products 
           ORDER BY rating DESC 
           LIMIT :limit"""
    )
    fun getTopRatedProducts(limit: Int = 10): Flow<List<ProductEntity>>

    // ==================== UPDATE Operations ====================

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Update
    suspend fun updateProducts(products: List<ProductEntity>)

    @Query("UPDATE products SET isFavorite = :isFavorite WHERE id = :productId")
    suspend fun updateFavoriteStatus(productId: String, isFavorite: Boolean)

    @Query("UPDATE products SET stock = :stock WHERE id = :productId")
    suspend fun updateStock(productId: String, stock: Int)

    @Query(
        """UPDATE products 
           SET lastSyncedAt = :timestamp 
           WHERE id = :productId"""
    )
    suspend fun updateSyncTime(productId: String, timestamp: Long)

    // ==================== DELETE Operations ====================

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProductById(productId: String)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    // ==================== SYNC Operations ====================

    @Query(
        """SELECT * FROM products 
           WHERE lastSyncedAt < :timestamp 
           ORDER BY updatedAt DESC"""
    )
    suspend fun getOutdatedProducts(timestamp: Long): List<ProductEntity>
}
