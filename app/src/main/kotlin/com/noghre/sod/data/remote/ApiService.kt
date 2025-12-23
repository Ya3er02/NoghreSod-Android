package com.noghre.sod.data.remote

import com.noghre.sod.core.config.ApiEndpoints
import com.noghre.sod.data.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service interface for all backend endpoints.
 * All endpoints are aligned with NoghreSod backend API specification.
 *
 * The API service automatically handles:
 * - Token injection via AuthInterceptor
 * - Error conversion via SafeApiCall
 * - Base URL management via Retrofit configuration
 */
interface ApiService {

    // ============ PRODUCTS ENDPOINTS ============

    /**
     * Fetches paginated list of products.
     *
     * @param page Page number (1-indexed)
     * @param limit Number of items per page
     * @param category Optional category filter
     * @param sort Optional sort parameter (e.g., "price_asc", "price_desc")
     * @return Paginated response of products
     */
    @GET(ApiEndpoints.Products.LIST)
    suspend fun getProducts(
        @Query(ApiEndpoints.QueryParams.PAGE) page: Int = 1,
        @Query(ApiEndpoints.QueryParams.LIMIT) limit: Int = 20,
        @Query(ApiEndpoints.QueryParams.CATEGORY) category: String? = null,
        @Query(ApiEndpoints.QueryParams.SORT) sort: String? = null
    ): Response<PaginatedResponse<ProductDto>>

    /**
     * Fetches a single product by ID.
     *
     * @param id Product ID
     * @return Product details
     */
    @GET(ApiEndpoints.Products.DETAIL)
    suspend fun getProductDetail(
        @Path("id") id: String
    ): Response<ApiResponse<ProductDto>>

    /**
     * Searches products by query.
     *
     * @param query Search query string
     * @param page Page number
     * @param limit Items per page
     * @return Paginated search results
     */
    @GET(ApiEndpoints.Products.SEARCH)
    suspend fun searchProducts(
        @Query(ApiEndpoints.QueryParams.QUERY) query: String,
        @Query(ApiEndpoints.QueryParams.PAGE) page: Int = 1,
        @Query(ApiEndpoints.QueryParams.LIMIT) limit: Int = 20
    ): Response<PaginatedResponse<ProductDto>>

    /**
     * Fetches products by category.
     *
     * @param categoryId Category ID
     * @param page Page number
     * @param limit Items per page
     * @return Paginated products for category
     */
    @GET(ApiEndpoints.Products.BY_CATEGORY)
    suspend fun getProductsByCategory(
        @Path("categoryId") categoryId: String,
        @Query(ApiEndpoints.QueryParams.PAGE) page: Int = 1,
        @Query(ApiEndpoints.QueryParams.LIMIT) limit: Int = 20
    ): Response<PaginatedResponse<ProductDto>>

    // ============ CATEGORIES ENDPOINTS ============

    /**
     * Fetches all product categories.
     *
     * @return List of categories
     */
    @GET(ApiEndpoints.Categories.LIST)
    suspend fun getCategories(): Response<ApiResponse<List<CategoryDto>>>

    /**
     * Fetches a single category by ID.
     *
     * @param id Category ID
     * @return Category details
     */
    @GET(ApiEndpoints.Categories.DETAIL)
    suspend fun getCategoryDetail(
        @Path("id") id: String
    ): Response<ApiResponse<CategoryDto>>

    // ============ PRICING ENDPOINTS ============

    /**
     * Fetches current live silver price.
     *
     * @return Current silver price information
     */
    @GET(ApiEndpoints.Prices.LIVE)
    suspend fun getLivePrice(): Response<ApiResponse<SilverPriceDto>>

    /**
     * Fetches historical silver price data.
     *
     * @param days Number of days to fetch history for (default 30)
     * @return List of historical prices
     */
    @GET(ApiEndpoints.Prices.HISTORY)
    suspend fun getPriceHistory(
        @Query(ApiEndpoints.QueryParams.DAYS) days: Int = 30
    ): Response<ApiResponse<List<SilverPriceDto>>>

    /**
     * Calculates final price based on weight and purity.
     *
     * @param request Calculation request with weight and purity
     * @return Calculated price
     */
    @POST(ApiEndpoints.Prices.CALCULATE)
    suspend fun calculatePrice(
        @Body request: PriceCalculationRequest
    ): Response<ApiResponse<PriceCalculationResponse>>

    // ============ AUTHENTICATION ENDPOINTS ============

    /**
     * Logs in a user with email and password.
     *
     * @param request Login credentials
     * @return Authentication response with tokens
     */
    @POST(ApiEndpoints.Auth.LOGIN)
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthResponse>>

    /**
     * Registers a new user.
     *
     * @param request Registration details
     * @return Authentication response with tokens
     */
    @POST(ApiEndpoints.Auth.REGISTER)
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<AuthResponse>>

    /**
     * Refreshes access token using refresh token.
     *
     * @param request Refresh token request
     * @return New access token
     */
    @POST(ApiEndpoints.Auth.REFRESH)
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    /**
     * Logs out the current user.
     * Revokes the refresh token on server.
     *
     * @return Logout response
     */
    @POST(ApiEndpoints.Auth.LOGOUT)
    suspend fun logout(): Response<ApiResponse<Unit>>

    // ============ USER ENDPOINTS ============

    /**
     * Fetches current user profile.
     *
     * @return User profile data
     */
    @GET(ApiEndpoints.Users.PROFILE)
    suspend fun getUserProfile(): Response<ApiResponse<UserDto>>

    /**
     * Updates user profile information.
     *
     * @param request Updated user information
     * @return Updated user profile
     */
    @PUT(ApiEndpoints.Users.UPDATE_PROFILE)
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<UserDto>>

    /**
     * Changes user password.
     *
     * @param request Current and new passwords
     * @return Success response
     */
    @POST(ApiEndpoints.Users.CHANGE_PASSWORD)
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<ApiResponse<Unit>>
}
