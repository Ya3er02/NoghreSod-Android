package com.noghre.sod.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for products.
 * Provides all database operations for ProductEntity.
 * All queries return reactive Flow for automatic UI updates.
 */
@Dao
interface ProductDao {

    /**
     * Inserts or replaces a single product.
     * Used when fetching individual product details.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    /**
     * Inserts or replaces multiple products.
     * Used when fetching paginated product lists.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    /**
     * Updates a product that already exists.
     */
    @Update
    suspend fun updateProduct(product: ProductEntity)

    /**
     * Deletes a product by ID.
     */
    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    /**
     * Deletes all products (useful for clearing cache).
     */
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    /**
     * Gets a single product by ID as a Flow.
     * Automatically emits updates when product changes.
     */
    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductFlow(id: String): Flow<ProductEntity?>

    /**
     * Gets a single product by ID (one-time fetch).
     */
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProduct(id: String): ProductEntity?

    /**
     * Gets all products as a Flow with pagination support.
     * Ordered by creation date (newest first).
     */
    @Query("""
        SELECT * FROM products
        ORDER BY createdAt DESC
    """)
    fun getAllProductsFlow(): Flow<List<ProductEntity>>

    /**
     * Gets paginated list of all products.
     * Used with Paging 3 for infinite scrolling.
     */
    @Query("""
        SELECT * FROM products
        ORDER BY createdAt DESC
    """)
    fun pagingSource(): PagingSource<Int, ProductEntity>

    /**
     * Gets products by category with pagination.
     */
    @Query("""
        SELECT * FROM products
        WHERE categoryId = :categoryId
        ORDER BY createdAt DESC
    """)
    fun getProductsByCategoryFlow(categoryId: String): Flow<List<ProductEntity>>

    /**
     * Gets products by category for paging.
     */
    @Query("""
        SELECT * FROM products
        WHERE categoryId = :categoryId
        ORDER BY createdAt DESC
    """)
    fun pagingSourceByCategory(categoryId: String): PagingSource<Int, ProductEntity>

    /**
     * Searches products by name (Persian or English).
     */
    @Query("""
        SELECT * FROM products
        WHERE name LIKE '%' || :query || '%'
        OR nameEn LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    /**
     * Searches products for paging.
     */
    @Query("""
        SELECT * FROM products
        WHERE name LIKE '%' || :query || '%'
        OR nameEn LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchProductsPaging(query: String): PagingSource<Int, ProductEntity>

    /**
     * Gets products within a price range.
     */
    @Query("""
        SELECT * FROM products
        WHERE price BETWEEN :minPrice AND :maxPrice
        ORDER BY price ASC
    """)
    fun getProductsByPriceRange(
        minPrice: Double,
        maxPrice: Double
    ): Flow<List<ProductEntity>>

    /**
     * Gets the count of all products.
     * Useful for checking if cache is empty.
     */
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    /**
     * Gets the most recently cached product.
     * Useful for checking overall cache freshness.
     */
    @Query("SELECT MAX(cachedAt) FROM products")
    suspend fun getMostRecentCacheTime(): Long?

    /**
     * Clears old cached products (older than X hours).
     * Used for cache cleanup to free storage.
     */
    @Query("""
        DELETE FROM products
        WHERE cachedAt < :cutoffTime
    """)
    suspend fun deleteCachedBefore(cutoffTime: Long)
}
