package com.noghre.sod.data.remote

import retrofit2.Response
import retrofit2.http.*
import com.noghre.sod.data.dto.*
import com.noghre.sod.core.config.ApiEndpoints

/**
 * Retrofit API service interface for NoghreSod Marketplace.
 * All endpoints are aligned with the backend API structure.
 *
 * Base URL: https://api.noghresod.com/
 */
interface ApiService {

    // ===== Authentication Endpoints =====
    /**
     * Login with email and password
     * POST /auth/login
     */
    @POST(ApiEndpoints.Auth.LOGIN)
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthResponse>>

    /**
     * Register a new user
     * POST /auth/register
     */
    @POST(ApiEndpoints.Auth.REGISTER)
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<AuthResponse>>

    /**
     * Refresh access token
     * POST /auth/refresh
     */
    @POST(ApiEndpoints.Auth.REFRESH_TOKEN)
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<ApiResponse<AuthResponse>>

    /**
     * Logout user
     * POST /auth/logout
     */
    @POST(ApiEndpoints.Auth.LOGOUT)
    suspend fun logout(): Response<ApiResponse<Unit>>

    // ===== Product Endpoints =====
    /**
     * Get paginated list of products
     * GET /api/products?page={page}&limit={limit}&category={category}&sort={sort}
     */
    @GET(ApiEndpoints.Products.LIST)
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null,
        @Query("sort") sort: String? = null
    ): Response<ApiResponse<PaginatedResponse<ProductDto>>>

    /**
     * Get product by ID
     * GET /api/products/{id}
     */
    @GET(ApiEndpoints.Products.DETAIL)
    suspend fun getProductById(
        @Path("id") id: String
    ): Response<ApiResponse<ProductDto>>

    /**
     * Search products
     * GET /api/products/search?q={query}&page={page}&limit={limit}
     */
    @GET(ApiEndpoints.Products.SEARCH)
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<PaginatedResponse<ProductDto>>>

    /**
     * Get trending products
     * GET /api/products/trending
     */
    @GET(ApiEndpoints.Products.TRENDING)
    suspend fun getTrendingProducts(
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<ProductDto>>>

    /**
     * Get featured products
     * GET /api/products/featured
     */
    @GET(ApiEndpoints.Products.FEATURED)
    suspend fun getFeaturedProducts(
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<ProductDto>>>

    // ===== Category Endpoints =====
    /**
     * Get all categories
     * GET /api/categories
     */
    @GET(ApiEndpoints.Categories.LIST)
    suspend fun getCategories(): Response<ApiResponse<List<CategoryDto>>>

    /**
     * Get category by ID
     * GET /api/categories/{id}
     */
    @GET(ApiEndpoints.Categories.DETAIL)
    suspend fun getCategoryById(
        @Path("id") id: String
    ): Response<ApiResponse<CategoryDto>>

    // ===== Silver Price Endpoints =====
    /**
     * Get current live silver price
     * GET /api/prices/live
     */
    @GET(ApiEndpoints.Prices.LIVE_PRICE)
    suspend fun getLiveSilverPrice(): Response<ApiResponse<SilverPriceDto>>

    /**
     * Get silver price history
     * GET /api/prices/history?days={days}
     */
    @GET(ApiEndpoints.Prices.PRICE_HISTORY)
    suspend fun getPriceHistory(
        @Query("days") days: Int = 30
    ): Response<ApiResponse<List<SilverPriceDto>>>

    /**
     * Calculate price based on weight and silver price
     * POST /api/prices/calculate
     */
    @POST(ApiEndpoints.Prices.CALCULATE_PRICE)
    suspend fun calculatePrice(
        @Body request: PriceCalculationRequest
    ): Response<ApiResponse<PriceCalculationResponse>>

    /**
     * Get price trends
     * GET /api/prices/trends
     */
    @GET(ApiEndpoints.Prices.PRICE_TRENDS)
    suspend fun getPriceTrends(
        @Query("days") days: Int = 30
    ): Response<ApiResponse<PriceTrendsDto>>

    // ===== User Profile Endpoints =====
    /**
     * Get user profile
     * GET /api/user/profile
     */
    @GET(ApiEndpoints.User.PROFILE)
    suspend fun getUserProfile(): Response<ApiResponse<UserDto>>

    /**
     * Update user profile
     * PUT /api/user/profile
     */
    @PUT(ApiEndpoints.User.UPDATE_PROFILE)
    suspend fun updateUserProfile(
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<UserDto>>

    /**
     * Change password
     * POST /api/user/change-password
     */
    @POST(ApiEndpoints.User.CHANGE_PASSWORD)
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<ApiResponse<Unit>>

    // ===== Wishlist Endpoints =====
    /**
     * Get user wishlist
     * GET /api/user/wishlist
     */
    @GET(ApiEndpoints.Wishlist.LIST)
    suspend fun getWishlist(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<PaginatedResponse<ProductDto>>>

    /**
     * Add product to wishlist
     * POST /api/user/wishlist/add
     */
    @POST(ApiEndpoints.Wishlist.ADD)
    suspend fun addToWishlist(
        @Body request: WishlistRequest
    ): Response<ApiResponse<Unit>>

    /**
     * Remove product from wishlist
     * POST /api/user/wishlist/remove
     */
    @POST(ApiEndpoints.Wishlist.REMOVE)
    suspend fun removeFromWishlist(
        @Body request: WishlistRequest
    ): Response<ApiResponse<Unit>>

    /**
     * Check if product is in wishlist
     * GET /api/user/wishlist/check/{productId}
     */
    @GET(ApiEndpoints.Wishlist.CHECK)
    suspend fun isInWishlist(
        @Path("productId") productId: String
    ): Response<ApiResponse<Boolean>>
}

// ===== Request DTOs =====
data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String? = null
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class UpdateProfileRequest(
    val name: String? = null,
    val phone: String? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

data class WishlistRequest(
    val productId: String
)

data class PriceCalculationRequest(
    val weight: Double,
    val purity: String = "950" // 950, 900, 800, 750
)

data class PriceCalculationResponse(
    val weight: Double,
    val purity: String,
    val pricePerGram: Double,
    val totalPrice: Double,
    val currency: String = "IRR"
)
