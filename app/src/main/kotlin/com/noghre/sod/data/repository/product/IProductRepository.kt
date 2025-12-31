package com.noghre.sod.data.repository.product

import com.noghre.sod.core.result.Result
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.ProductFilter
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for product operations.
 *
 * Handles:
 * - Fetching and caching products
 * - Product search and filtering
 * - Category browsing
 * - Product details
 * - Price information updates
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
interface IProductRepository {

    /**
     * Get all products with pagination.
     *
     * @param page Page number (0-indexed)
     * @param pageSize Number of items per page
     * @return Flow emitting Result with paginated Products
     */
    fun getProducts(
        page: Int = 0,
        pageSize: Int = 20
    ): Flow<Result<List<Product>>>

    /**
     * Get products by category.
     *
     * @param categoryId Category ID
     * @param page Page number
     * @param pageSize Number of items per page
     * @return Flow emitting Result with category Products
     */
    fun getProductsByCategory(
        categoryId: String,
        page: Int = 0,
        pageSize: Int = 20
    ): Flow<Result<List<Product>>>

    /**
     * Search products by query.
     *
     * @param query Search query
     * @param page Page number
     * @param pageSize Number of items per page
     * @return Flow emitting Result with search results
     */
    fun searchProducts(
        query: String,
        page: Int = 0,
        pageSize: Int = 20
    ): Flow<Result<List<Product>>>

    /**
     * Get products with advanced filtering.
     *
     * @param filter ProductFilter with multiple criteria
     * @param page Page number
     * @param pageSize Number of items per page
     * @return Flow emitting Result with filtered Products
     */
    fun filterProducts(
        filter: ProductFilter,
        page: Int = 0,
        pageSize: Int = 20
    ): Flow<Result<List<Product>>>

    /**
     * Get detailed product information.
     *
     * @param productId Product ID
     * @return Flow emitting Result with Product details
     */
    fun getProductDetail(productId: String): Flow<Result<Product>>

    /**
     * Get featured/highlighted products.
     *
     * @return Flow emitting Result with featured Products
     */
    fun getFeaturedProducts(): Flow<Result<List<Product>>>

    /**
     * Get products on sale.
     *
     * @param page Page number
     * @param pageSize Number of items per page
     * @return Flow emitting Result with sale Products
     */
    fun getSaleProducts(
        page: Int = 0,
        pageSize: Int = 20
    ): Flow<Result<List<Product>>>

    /**
     * Get related products for a given product.
     *
     * @param productId Product ID to find related items for
     * @return Flow emitting Result with related Products
     */
    fun getRelatedProducts(productId: String): Flow<Result<List<Product>>>

    /**
     * Get current market prices (real-time).
     *
     * @return Flow emitting Result with price information
     */
    fun getMarketPrices(): Flow<Result<Map<String, Double>>>
}
