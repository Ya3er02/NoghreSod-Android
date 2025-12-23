package com.noghre.sod.data.remote.api

import com.noghre.sod.data.dto.AddressDto
import com.noghre.sod.data.dto.ApiResponseDto
import com.noghre.sod.data.dto.AuthResponseDto
import com.noghre.sod.data.dto.CartDto
import com.noghre.sod.data.dto.CategoryDto
import com.noghre.sod.data.dto.OrderDto
import com.noghre.sod.data.dto.ProductDto
import com.noghre.sod.data.dto.UserDto
import com.noghre.sod.data.remote.api.request.AddToCartRequest
import com.noghre.sod.data.remote.api.request.CreateOrderRequest
import com.noghre.sod.data.remote.api.request.LoginRequest
import com.noghre.sod.data.remote.api.request.OtpRequest
import com.noghre.sod.data.remote.api.request.RegisterRequest
import com.noghre.sod.data.remote.api.request.UpdateCartItemRequest
import com.noghre.sod.data.remote.api.request.UpdateProfileRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API Service for NoghreSod e-commerce application.
 * Handles all REST API communication with the backend server.
 */
@Headers("Content-Type: application/json")
interface NoghreSodApiService {

    // ==================== AUTH ENDPOINTS ====================

    /**
     * Register new user.
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponseDto<AuthResponseDto>

    /**
     * Login user with phone and password.
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponseDto<AuthResponseDto>

    /**
     * Refresh authentication token.
     */
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponseDto<AuthResponseDto>

    /**
     * Logout current user.
     */
    @POST("auth/logout")
    suspend fun logout(): ApiResponseDto<Unit>

    /**
     * Verify OTP for phone authentication.
     */
    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpRequest): ApiResponseDto<AuthResponseDto>

    // ==================== PRODUCT ENDPOINTS ====================

    /**
     * Get products with pagination and filters.
     */
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("category_id") categoryId: String? = null,
        @Query("sort") sort: String? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null
    ): ApiResponseDto<List<ProductDto>>

    /**
     * Get product by ID.
     */
    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ApiResponseDto<ProductDto>

    /**
     * Search products by query.
     */
    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("page") page: Int = 1
    ): ApiResponseDto<List<ProductDto>>

    /**
     * Get featured products.
     */
    @GET("products/featured")
    suspend fun getFeaturedProducts(): ApiResponseDto<List<ProductDto>>

    /**
     * Get new arrival products.
     */
    @GET("products/new")
    suspend fun getNewArrivals(): ApiResponseDto<List<ProductDto>>

    // ==================== CATEGORY ENDPOINTS ====================

    /**
     * Get all categories.
     */
    @GET("categories")
    suspend fun getCategories(): ApiResponseDto<List<CategoryDto>>

    /**
     * Get category by ID.
     */
    @GET("categories/{id}")
    suspend fun getCategoryById(@Path("id") id: String): ApiResponseDto<CategoryDto>

    // ==================== CART ENDPOINTS ====================

    /**
     * Get current user's cart.
     */
    @GET("cart")
    suspend fun getCart(): ApiResponseDto<CartDto>

    /**
     * Add item to cart.
     */
    @POST("cart/items")
    suspend fun addToCart(@Body request: AddToCartRequest): ApiResponseDto<CartDto>

    /**
     * Update cart item quantity.
     */
    @PUT("cart/items/{itemId}")
    suspend fun updateCartItem(
        @Path("itemId") itemId: String,
        @Body request: UpdateCartItemRequest
    ): ApiResponseDto<CartDto>

    /**
     * Remove item from cart.
     */
    @DELETE("cart/items/{itemId}")
    suspend fun removeFromCart(@Path("itemId") itemId: String): ApiResponseDto<CartDto>

    /**
     * Clear entire cart.
     */
    @DELETE("cart")
    suspend fun clearCart(): ApiResponseDto<Unit>

    // ==================== ORDER ENDPOINTS ====================

    /**
     * Get user's orders with pagination.
     */
    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): ApiResponseDto<List<OrderDto>>

    /**
     * Get order by ID.
     */
    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): ApiResponseDto<OrderDto>

    /**
     * Create new order.
     */
    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): ApiResponseDto<OrderDto>

    /**
     * Cancel order.
     */
    @PUT("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: String): ApiResponseDto<OrderDto>

    // ==================== USER ENDPOINTS ====================

    /**
     * Get user profile.
     */
    @GET("user/profile")
    suspend fun getProfile(): ApiResponseDto<UserDto>

    /**
     * Update user profile.
     */
    @PUT("user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponseDto<UserDto>

    /**
     * Get user addresses.
     */
    @GET("user/addresses")
    suspend fun getAddresses(): ApiResponseDto<List<AddressDto>>

    /**
     * Add new address.
     */
    @POST("user/addresses")
    suspend fun addAddress(@Body address: AddressDto): ApiResponseDto<AddressDto>

    /**
     * Update address.
     */
    @PUT("user/addresses/{id}")
    suspend fun updateAddress(
        @Path("id") id: String,
        @Body address: AddressDto
    ): ApiResponseDto<AddressDto>

    /**
     * Delete address.
     */
    @DELETE("user/addresses/{id}")
    suspend fun deleteAddress(@Path("id") id: String): ApiResponseDto<Unit>

    // ==================== FAVORITES ENDPOINTS ====================

    /**
     * Get user's favorite products.
     */
    @GET("user/favorites")
    suspend fun getFavorites(): ApiResponseDto<List<ProductDto>>

    /**
     * Add product to favorites.
     */
    @POST("user/favorites/{productId}")
    suspend fun addToFavorites(@Path("productId") productId: String): ApiResponseDto<Unit>

    /**
     * Remove product from favorites.
     */
    @DELETE("user/favorites/{productId}")
    suspend fun removeFromFavorites(@Path("productId") productId: String): ApiResponseDto<Unit>
}

/**
 * Request body for refresh token.
 */
data class RefreshTokenRequest(
    val refreshToken: String
)
