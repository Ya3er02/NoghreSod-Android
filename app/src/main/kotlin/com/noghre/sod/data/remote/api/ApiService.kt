package com.noghre.sod.data.remote.api

import com.noghre.sod.data.remote.dto.RequestDtos
import com.noghre.sod.data.remote.dto.ResponseDtos
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API Service for NoghreSod Backend.
 * 
* All API endpoints for:
 * - Products (list, search, details, categories)
 * - Shopping Cart
 * - Orders
 * - Authentication & User
 * - Payments
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
interface ApiService {

    // ============ PRODUCTS ============

    /**
     * Get products list with pagination and filters.
     */
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") pageSize: Int = 20,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("sort") sortBy: String? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null
    ): Response<ResponseDtos.ApiResponse<List<ResponseDtos.ProductResponseDto>>>

    /**
     * Search products by query.
     */
    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("category") category: String? = null
    ): Response<ResponseDtos.ApiResponse<List<ResponseDtos.ProductResponseDto>>>

    /**
     * Get products by category.
     */
    @GET("products/category/{categoryId}")
    suspend fun getProductsByCategory(
        @Path("categoryId") categoryId: String
    ): Response<ResponseDtos.ApiResponse<List<ResponseDtos.ProductResponseDto>>>

    /**
     * Get product details by ID.
     */
    @GET("products/{productId}")
    suspend fun getProductById(
        @Path("productId") productId: String
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.ProductResponseDto>>

    /**
     * Get all categories.
     */
    @GET("categories")
    suspend fun getCategories(): Response<ResponseDtos.ApiResponse<List<ResponseDtos.CategoryResponseDto>>>

    // ============ AUTHENTICATION ============

    /**
     * Login with mobile and password.
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: RequestDtos.LoginRequest
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.LoginResponseDto>>

    /**
     * Alternative login endpoint.
     */
    suspend fun login(
        @Query("mobile") mobile: String,
        @Query("password") password: String
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.LoginResponseDto>> {
        return login(RequestDtos.LoginRequest(mobile, password))
    }

    /**
     * Register new user.
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: RequestDtos.RegisterRequest
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.LoginResponseDto>>

    /**
     * Alternative register endpoint.
     */
    suspend fun register(
        @Query("name") name: String,
        @Query("mobile") mobile: String,
        @Query("email") email: String,
        @Query("password") password: String
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.LoginResponseDto>> {
        return register(
            RequestDtos.RegisterRequest(
                name = name,
                mobile = mobile,
                email = email,
                password = password
            )
        )
    }

    /**
     * Logout user.
     */
    @POST("auth/logout")
    suspend fun logout(): Response<ResponseDtos.ApiResponse<Unit>>

    /**
     * Refresh authentication token.
     */
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Query("refreshToken") refreshToken: String
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.TokenResponseDto>>

    /**
     * Request password reset.
     */
    @POST("auth/request-reset")
    suspend fun requestPasswordReset(
        @Query("mobile") mobile: String
    ): Response<ResponseDtos.ApiResponse<Unit>>

    // ============ USER PROFILE ============

    /**
     * Get user profile.
     */
    @GET("user/profile")
    suspend fun getUserProfile(): Response<ResponseDtos.ApiResponse<ResponseDtos.UserResponseDto>>

    /**
     * Update user profile.
     */
    @PUT("user/profile")
    suspend fun updateProfile(
        @Body request: RequestDtos.UpdateProfileRequest
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.UserResponseDto>>

    // ============ CART ============

    /**
     * Get cart items.
     */
    @GET("cart")
    suspend fun getCart(): Response<ResponseDtos.ApiResponse<ResponseDtos.CartResponseDto>>

    /**
     * Add item to cart.
     */
    @POST("cart/add")
    suspend fun addToCart(
        @Body request: RequestDtos.AddToCartRequest
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.CartResponseDto>>

    /**
     * Remove item from cart.
     */
    @DELETE("cart/{productId}")
    suspend fun removeFromCart(
        @Path("productId") productId: String
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.CartResponseDto>>

    /**
     * Update cart item quantity.
     */
    @PUT("cart/{productId}")
    suspend fun updateCartItem(
        @Path("productId") productId: String,
        @Body request: RequestDtos.UpdateCartItemRequest
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.CartResponseDto>>

    /**
     * Clear entire cart.
     */
    @DELETE("cart")
    suspend fun clearCart(): Response<ResponseDtos.ApiResponse<Unit>>

    // ============ ORDERS ============

    /**
     * Get user's orders.
     */
    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("limit") pageSize: Int = 10
    ): Response<ResponseDtos.ApiResponse<List<ResponseDtos.OrderResponseDto>>>

    /**
     * Get order details.
     */
    @GET("orders/{orderId}")
    suspend fun getOrderDetails(
        @Path("orderId") orderId: String
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.OrderResponseDto>>

    /**
     * Create new order.
     */
    @POST("orders")
    suspend fun createOrder(
        @Body request: RequestDtos.CreateOrderRequest
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.OrderResponseDto>>

    /**
     * Cancel order.
     */
    @POST("orders/{orderId}/cancel")
    suspend fun cancelOrder(
        @Path("orderId") orderId: String
    ): Response<ResponseDtos.ApiResponse<Unit>>

    // ============ PAYMENTS ============

    /**
     * Initiate payment.
     */
    @POST("payments/init")
    suspend fun initiatePayment(
        @Body request: RequestDtos.PaymentInitRequest
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.PaymentInitResponseDto>>

    /**
     * Verify payment.
     */
    @POST("payments/verify")
    suspend fun verifyPayment(
        @Body request: RequestDtos.PaymentVerifyRequest
    ): Response<ResponseDtos.ApiResponse<ResponseDtos.PaymentVerifyResponseDto>>

    // ============ WISHLIST ============

    /**
     * Get wishlist items.
     */
    @GET("wishlist")
    suspend fun getWishlist(): Response<ResponseDtos.ApiResponse<List<ResponseDtos.ProductResponseDto>>>

    /**
     * Add product to wishlist.
     */
    @POST("wishlist/{productId}")
    suspend fun addToWishlist(
        @Path("productId") productId: String
    ): Response<ResponseDtos.ApiResponse<Unit>>

    /**
     * Remove product from wishlist.
     */
    @DELETE("wishlist/{productId}")
    suspend fun removeFromWishlist(
        @Path("productId") productId: String
    ): Response<ResponseDtos.ApiResponse<Unit>>

    // ============ FAVORITES ============

    /**
     * Get favorite products.
     */
    @GET("favorites")
    suspend fun getFavorites(): Response<ResponseDtos.ApiResponse<List<ResponseDtos.ProductResponseDto>>>

    /**
     * Add product to favorites.
     */
    @POST("favorites/{productId}")
    suspend fun addToFavorites(
        @Path("productId") productId: String
    ): Response<ResponseDtos.ApiResponse<Unit>>

    /**
     * Remove product from favorites.
     */
    @DELETE("favorites/{productId}")
    suspend fun removeFromFavorites(
        @Path("productId") productId: String
    ): Response<ResponseDtos.ApiResponse<Unit>>
}
