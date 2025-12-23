package com.noghre.sod.domain.repository

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for product data operations
 * Abstracts the data layer from domain and presentation layers
 */
interface ProductRepository {
    /**
     * Get all products
     */
    fun getAllProducts(
        page: Int = 0,
        pageSize: Int = 20,
    ): Flow<Result<List<ProductSummary>>>

    /**
     * Get product by ID
     */
    fun getProductById(productId: String): Flow<Result<ProductDetail>>

    /**
     * Search products
     */
    fun searchProducts(
        query: String,
        page: Int = 0,
        pageSize: Int = 20,
    ): Flow<Result<List<ProductSummary>>>

    /**
     * Get products by category
     */
    fun getProductsByCategory(
        category: ProductCategory,
        page: Int = 0,
        pageSize: Int = 20,
    ): Flow<Result<List<ProductSummary>>>

    /**
     * Get products by purity
     */
    fun getProductsByPurity(
        purity: PurityType,
        page: Int = 0,
        pageSize: Int = 20,
    ): Flow<Result<List<ProductSummary>>>

    /**
     * Get featured products for homepage
     */
    fun getFeaturedProducts(): Flow<Result<List<ProductSummary>>>

    /**
     * Get related products
     */
    fun getRelatedProducts(
        productId: String,
        limit: Int = 5,
    ): Flow<Result<List<ProductSummary>>>

    /**
     * Get product recommendations
     */
    fun getRecommendations(
        productId: String? = null,
        limit: Int = 10,
    ): Flow<Result<List<ProductSummary>>>

    /**
     * Add product to favorites
     */
    suspend fun addToFavorites(productId: String): Result<Unit>

    /**
     * Remove product from favorites
     */
    suspend fun removeFromFavorites(productId: String): Result<Unit>

    /**
     * Get user favorite products
     */
    fun getFavoriteProducts(): Flow<Result<List<ProductSummary>>>

    /**
     * Check if product is in favorites
     */
    fun isFavorite(productId: String): Flow<Result<Boolean>>

    /**
     * Add review to product
     */
    suspend fun addReview(
        productId: String,
        rating: Float,
        comment: String? = null,
    ): Result<ProductReview>

    /**
     * Get product reviews
     */
    fun getProductReviews(
        productId: String,
        page: Int = 0,
        pageSize: Int = 20,
    ): Flow<Result<List<ProductReview>>>

    /**
     * Filter products
     */
    fun filterProducts(
        categories: List<ProductCategory>? = null,
        purities: List<PurityType>? = null,
        minPrice: Long? = null,
        maxPrice: Long? = null,
        minWeight: Double? = null,
        maxWeight: Double? = null,
        minRating: Float? = null,
        page: Int = 0,
        pageSize: Int = 20,
    ): Flow<Result<List<ProductSummary>>>

    /**
     * Sort products
     */
    fun sortProducts(
        sortBy: ProductSortOption,
        order: SortOrder = SortOrder.DESCENDING,
        page: Int = 0,
        pageSize: Int = 20,
    ): Flow<Result<List<ProductSummary>>>
}

/**
 * Product review model
 */
data class ProductReview(
    val reviewId: String,
    val productId: String,
    val userId: String,
    val userName: String,
    val rating: Float,
    val comment: String?,
    val images: List<String> = emptyList(),
    val helpful: Int = 0,
    val unhelpful: Int = 0,
    val verified: Boolean = false,
    val createdAt: java.time.LocalDateTime,
)

/**
 * Product sort options
 */
enum class ProductSortOption {
    NEWEST,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    RATING,
    POPULARITY,
    WEIGHT,
}

/**
 * Sort order
 */
enum class SortOrder {
    ASCENDING,
    DESCENDING,
}
