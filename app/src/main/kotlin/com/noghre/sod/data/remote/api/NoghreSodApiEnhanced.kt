package com.noghre.sod.data.remote.api

import com.noghre.sod.data.remote.dto.PaginatedResponse
import com.noghre.sod.data.remote.dto.ProductDto
import com.noghre.sod.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Header
import timber.log.Timber

/**
 * Enhanced Retrofit API service with pagination, filtering, and proper error handling.
 * All endpoints support:
 * - Automatic timeout configuration
 * - Request/response logging (via interceptors)
 * - Error response mapping
 */
interface NoghreSodApiEnhanced {

    // ============== PRODUCTS ============== //

    /**
     * Get paginated list of products with optional filtering.
     *
     * @param page Page number (starting from 1)
     * @param limit Items per page (default 20, max 100)
     * @param category Optional category filter
     * @param sortBy Field to sort by (name, price, rating, created_at)
     * @param sortOrder Ascending (asc) or Descending (desc)
     * @param minPrice Optional minimum price filter
     * @param maxPrice Optional maximum price filter
     * @param search Optional search term for name/description
     *
     * @return Response with paginated products and metadata
     */
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null,
        @Query("sort_by") sortBy: String? = "created_at",
        @Query("sort_order") sortOrder: String? = "desc",
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("search") search: String? = null
    ): Response<PaginatedResponse<ProductDto>>

    /**
     * Get single product by ID.
     *
     * @param productId Product ID
     * @return Response with product details
     */
    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") productId: String
    ): Response<ProductDto>

    /**
     * Search products with advanced filters.
     *
     * @param query Search query
     * @param page Page number
     * @param limit Items per page
     * @return Response with search results
     */
    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<ProductDto>>

    /**
     * Get products by category.
     *
     * @param category Category name/slug
     * @param page Page number
     * @param limit Items per page
     * @return Response with filtered products
     */
    @GET("categories/{category}/products")
    suspend fun getProductsByCategory(
        @Path("category") category: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<ProductDto>>

    // ============== USER AUTHENTICATION ============== //

    /**
     * User login endpoint.
     *
     * @param email User email
     * @param password User password
     * @return Response with authentication token and user info
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    /**
     * User registration endpoint.
     *
     * @param email User email
     * @param password User password
     * @param firstName First name
     * @param lastName Last name
     * @return Response with new user info
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    /**
     * Refresh authentication token.
     *
     * @param refreshToken Current refresh token
     * @return Response with new access token
     */
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<TokenResponse>

    /**
     * Logout user (revoke tokens).
     *
     * @return Response indicating logout status
     */
    @POST("auth/logout")
    suspend fun logout(): Response<LogoutResponse>

    // ============== USER PROFILE ============== //

    /**
     * Get current user profile.
     *
     * @param authorization Bearer token
     * @return Response with user profile
     */
    @GET("users/profile")
    suspend fun getUserProfile(
        @Header("Authorization") authorization: String
    ): Response<UserDto>

    /**
     * Update user profile.
     *
     * @param request Updated profile data
     * @param authorization Bearer token
     * @return Response with updated profile
     */
    @POST("users/profile")
    suspend fun updateUserProfile(
        @Body request: UpdateProfileRequest,
        @Header("Authorization") authorization: String
    ): Response<UserDto>
}

// ============== REQUEST/RESPONSE DTOs ============== //

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class UpdateProfileRequest(
    val firstName: String?,
    val lastName: String?,
    val phone: String?,
    val address: String?
)

data class AuthResponse(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

data class TokenResponse(
    val accessToken: String,
    val expiresIn: Long
)

data class LogoutResponse(
    val success: Boolean,
    val message: String
)
