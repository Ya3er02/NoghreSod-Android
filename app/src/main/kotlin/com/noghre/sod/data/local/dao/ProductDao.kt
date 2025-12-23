package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Product database operations.
 * Handles all CRUD operations on the products table.
 */
@Dao
interface ProductDao {

    /**
     * Insert multiple products, replacing existing entries with same ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    /**
     * Get product by ID.
     */
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): ProductEntity?

    /**
     * Get all products in a category.
     */
    @Query("SELECT * FROM products WHERE category_id = :categoryId ORDER BY cached_at DESC")
    fun getProductsByCategory(categoryId: String): Flow<List<ProductEntity>>

    /**
     * Get all favorite products.
     */
    @Query("SELECT * FROM products WHERE is_favorite = 1 ORDER BY cached_at DESC")
    fun getFavoriteProducts(): Flow<List<ProductEntity>>

    /**
     * Search products by name or description.
     */
    @Query("""
        SELECT * FROM products 
        WHERE name LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
        ORDER BY cached_at DESC
    """)
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    /**
     * Update a product.
     */
    @Update
    suspend fun updateProduct(product: ProductEntity)

    /**
     * Delete a product.
     */
    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    /**
     * Delete products older than specified timestamp.
     */
    @Query("DELETE FROM products WHERE cached_at < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)

    /**
     * Get recent products.
     */
    @Query("SELECT * FROM products ORDER BY cached_at DESC LIMIT :limit")
    fun getRecentProducts(limit: Int = 20): Flow<List<ProductEntity>>

    /**
     * Get all products count.
     */
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductsCount(): Int
}
