package com.noghre.sod.domain.repository

import androidx.paging.PagingData
import com.noghre.sod.core.result.Result
import com.noghre.sod.domain.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for product-related operations.
 * Defines the contract that data layer must implement.
 * Abstracts network and database operations from business logic.
 */
interface ProductRepository {

    /**
     * Gets a paginated list of all products.
     * Uses offline-first strategy: loads from cache first, then syncs with server.
     *
     * @param page Page number (1-indexed)
     * @param limit Number of items per page
     * @param category Optional category filter
     * @param sort Optional sort parameter
     * @return Flow of Result containing paginated products
     */
    fun getProducts(
        page: Int = 1,
        limit: Int = 20,
        category: String? = null,
        sort: String? = null
    ): Flow<Result<List<Product>>>

    /**
     * Gets paginated products using Paging 3.
     * Supports infinite scrolling with automatic page loading.
     *
     * @param category Optional category filter
     * @return Flow of PagingData for Paging 3 integration
     */
    fun getProductsPaged(
        category: String? = null
    ): Flow<PagingData<Product>>

    /**
     * Gets a single product by ID.
     * Loads from cache first, then fetches from server if not found or cached data is stale.
     *
     * @param id Product ID
     * @return Flow of Result containing product
     */
    fun getProductDetail(id: String): Flow<Result<Product>>

    /**
     * Searches products by query string.
     * Searches both local cache and server.
     *
     * @param query Search query string
     * @param page Page number
     * @param limit Items per page
     * @return Flow of Result containing search results
     */
    fun searchProducts(
        query: String,
        page: Int = 1,
        limit: Int = 20
    ): Flow<Result<List<Product>>>

    /**
     * Gets products by category.
     * Offline-first approach with cache invalidation.
     *
     * @param categoryId Category ID
     * @param page Page number
     * @param limit Items per page
     * @return Flow of Result containing category products
     */
    fun getProductsByCategory(
        categoryId: String,
        page: Int = 1,
        limit: Int = 20
    ): Flow<Result<List<Product>>>

    /**
     * Gets products within a price range.
     *
     * @param minPrice Minimum price in Rials
     * @param maxPrice Maximum price in Rials
     * @return Flow of Result containing filtered products
     */
    fun getProductsByPriceRange(
        minPrice: Double,
        maxPrice: Double
    ): Flow<Result<List<Product>>>

    /**
     * Manually refresh products cache.
     * Forces fetch from server regardless of cache validity.
     *
     * @return Flow of Result indicating refresh status
     */
    fun refreshProducts(): Flow<Result<Unit>>

    /**
     * Clears all cached products.
     *
     * @return Flow of Result indicating clear operation
     */
    suspend fun clearCache(): Result<Unit>
}
