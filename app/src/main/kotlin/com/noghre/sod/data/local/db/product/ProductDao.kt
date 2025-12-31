package com.noghre.sod.data.local.db.product

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Product entities.
 * 
* Handles all database operations for products.
 * Supports caching, filtering, and searching.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Dao
interface ProductDao {

    /**
     * Insert a single product.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    /**
     * Insert multiple products.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    /**
     * Get all products as Flow.
     */
    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    /**
     * Get all products (non-Flow).
     */
    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    suspend fun getAllProductsSync(): List<ProductEntity>

    /**
     * Get product by ID.
     */
    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: String): ProductEntity?

    /**
     * Get products with pagination.
     */
    @Query(
        """
        SELECT * FROM products 
        ORDER BY :sortBy DESC 
        LIMIT :pageSize OFFSET :offset
        """
    )
    fun getProductsPaginated(
        offset: Int,
        pageSize: Int,
        sortBy: String = "createdAt"
    ): Flow<List<ProductEntity>>

    /**
     * Search products by name or description.
     */
    @Query(
        """
        SELECT * FROM products 
        WHERE name LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        ORDER BY createdAt DESC
        """
    )
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    /**
     * Get products by category.
     */
    @Query(
        """
        SELECT * FROM products 
        WHERE categoryId = :categoryId 
        ORDER BY createdAt DESC
        """
    )
    fun getProductsByCategory(categoryId: String): Flow<List<ProductEntity>>

    /**
     * Get products within price range.
     */
    @Query(
        """
        SELECT * FROM products 
        WHERE price BETWEEN :minPrice AND :maxPrice 
        ORDER BY price ASC
        """
    )
    fun getProductsByPriceRange(
        minPrice: Double,
        maxPrice: Double
    ): Flow<List<ProductEntity>>

    /**
     * Get products by weight (silver jewelry).
     */
    @Query(
        """
        SELECT * FROM products 
        WHERE weight BETWEEN :minWeight AND :maxWeight 
        ORDER BY weight ASC
        """
    )
    fun getProductsByWeightRange(
        minWeight: Double,
        maxWeight: Double
    ): Flow<List<ProductEntity>>

    /**
     * Get featured products.
     */
    @Query(
        """
        SELECT * FROM products 
        WHERE isFeatured = 1 
        ORDER BY createdAt DESC
        """
    )
    fun getFeaturedProducts(): Flow<List<ProductEntity>>

    /**
     * Get new products.
     */
    @Query(
        """
        SELECT * FROM products 
        WHERE createdAt > datetime('now', '-30 days') 
        ORDER BY createdAt DESC
        """
    )
    fun getNewProducts(): Flow<List<ProductEntity>>

    /**
     * Get best-selling products.
     */
    @Query(
        """
        SELECT * FROM products 
        ORDER BY soldCount DESC 
        LIMIT :limit
        """
    )
    fun getBestSellingProducts(limit: Int = 10): Flow<List<ProductEntity>>

    /**
     * Update product.
     */
    @Update
    suspend fun updateProduct(product: ProductEntity)

    /**
     * Delete product by ID.
     */
    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: String)

    /**
     * Delete all products.
     */
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    /**
     * Get total product count.
     */
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    /**
     * Get products count by category.
     */
    @Query("SELECT COUNT(*) FROM products WHERE categoryId = :categoryId")
    suspend fun getCategoryProductCount(categoryId: String): Int
}
