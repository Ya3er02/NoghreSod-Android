package com.noghre.sod.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.noghre.sod.data.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * ProductDao - Data Access Object for product database operations.
 * 
 * Handles all database queries for products including caching and filtering.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Dao
interface ProductDao {
    
    /**
     * Insert a single product
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)
    
    /**
     * Insert multiple products
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)
    
    /**
     * Update an existing product
     */
    @Update
    suspend fun updateProduct(product: ProductEntity)
    
    /**
     * Update multiple products
     */
    @Update
    suspend fun updateProducts(products: List<ProductEntity>)
    
    /**
     * Delete a product
     */
    @Delete
    suspend fun deleteProduct(product: ProductEntity)
    
    /**
     * Delete all products
     */
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
    
    /**
     * Get product by ID
     */
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?
    
    /**
     * Get product by ID as Flow for real-time updates
     */
    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductByIdFlow(productId: String): Flow<ProductEntity?>
    
    /**
     * Get all products
     */
    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAllProducts(): List<ProductEntity>
    
    /**
     * Get all products as Flow
     */
    @Query("SELECT * FROM products ORDER BY updatedAt DESC")
    fun getAllProductsFlow(): Flow<List<ProductEntity>>
    
    /**
     * Get featured products
     */
    @Query("SELECT * FROM products WHERE isFeatured = 1 ORDER BY updatedAt DESC LIMIT :limit")
    suspend fun getFeaturedProducts(limit: Int = 10): List<ProductEntity>
    
    /**
     * Get new products
     */
    @Query("SELECT * FROM products WHERE isNew = 1 ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getNewProducts(limit: Int = 10): List<ProductEntity>
    
    /**
     * Get products on sale
     */
    @Query("SELECT * FROM products WHERE isOnSale = 1 ORDER BY updatedAt DESC LIMIT :limit")
    suspend fun getOnSaleProducts(limit: Int = 20): List<ProductEntity>
    
    /**
     * Search products by name or description
     */
    @Query("""
        SELECT * FROM products 
        WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    suspend fun searchProducts(query: String): List<ProductEntity>
    
    /**
     * Search products as Flow
     */
    @Query("""
        SELECT * FROM products 
        WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchProductsFlow(query: String): Flow<List<ProductEntity>>
    
    /**
     * Get products by category
     */
    @Query("SELECT * FROM products WHERE category = :category ORDER BY name ASC")
    suspend fun getProductsByCategory(category: String): List<ProductEntity>
    
    /**
     * Filter products by price range
     */
    @Query("""
        SELECT * FROM products 
        WHERE price BETWEEN :minPrice AND :maxPrice
        ORDER BY price ASC
    """)
    suspend fun getProductsByPriceRange(minPrice: Double, maxPrice: Double): List<ProductEntity>
    
    /**
     * Filter by multiple criteria (category, price, rating)
     */
    @Query("""
        SELECT * FROM products 
        WHERE (:category IS NULL OR category = :category)
        AND (:minPrice = 0.0 OR price >= :minPrice)
        AND (:maxPrice = 0.0 OR price <= :maxPrice)
        AND (:minRating = 0.0 OR rating >= :minRating)
        AND (:inStockOnly = 0 OR inStock = 1)
        ORDER BY CASE 
            WHEN :sortBy = 'price_asc' THEN price
            WHEN :sortBy = 'price_desc' THEN price
            WHEN :sortBy = 'rating' THEN rating
            ELSE updatedAt
        END
    """)
    suspend fun filterProducts(
        category: String? = null,
        minPrice: Double = 0.0,
        maxPrice: Double = 0.0,
        minRating: Double = 0.0,
        inStockOnly: Boolean = false,
        sortBy: String = "latest"
    ): List<ProductEntity>
    
    /**
     * Get in-stock products only
     */
    @Query("SELECT * FROM products WHERE inStock = 1 ORDER BY name ASC")
    suspend fun getInStockProducts(): List<ProductEntity>
    
    /**
     * Get products with high rating
     */
    @Query("SELECT * FROM products WHERE rating >= :minRating ORDER BY rating DESC")
    suspend fun getHighRatedProducts(minRating: Double = 4.0): List<ProductEntity>
    
    /**
     * Get products by hallmark (for silver jewelry)
     */
    @Query("SELECT * FROM products WHERE hallmark = :hallmark ORDER BY name ASC")
    suspend fun getProductsByHallmark(hallmark: String): List<ProductEntity>
    
    /**
     * Get products by material
     */
    @Query("SELECT * FROM products WHERE material = :material ORDER BY name ASC")
    suspend fun getProductsByMaterial(material: String): List<ProductEntity>
    
    /**
     * Get products by gem type
     */
    @Query("SELECT * FROM products WHERE gemType = :gemType ORDER BY name ASC")
    suspend fun getProductsByGemType(gemType: String): List<ProductEntity>
    
    /**
     * Get total product count
     */
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
    
    /**
     * Get unsync products
     */
    @Query("SELECT * FROM products WHERE isSynced = 0")
    suspend fun getUnsyncedProducts(): List<ProductEntity>
    
    /**
     * Mark products as synced
     */
    @Query("UPDATE products SET isSynced = 1 WHERE id IN (:productIds)")
    suspend fun markAsSynced(productIds: List<String>)
}
