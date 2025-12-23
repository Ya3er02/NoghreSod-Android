package com.noghre.sod.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import com.noghre.sod.data.dto.*

/**
 * Retrofit API Service Interface
 * Defines all API endpoints for Noghresod jewelry e-commerce platform
 * 
 * Base URL: https://api.noghresod.ir/v1/
 * Authentication: Bearer Token (Authorization header)
 */
interface ApiService {

    // ======================== AUTHENTICATION ENDPOINTS ========================

    /**
     * User login with phone and password
     * POST /auth/login
     * @param request LoginRequestDto with phone and password
     * @return LoginResponseDto containing token and user info
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    /**
     * Refresh expired token
     * POST /auth/refresh
     */
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenDto): Response<LoginResponseDto>

    /**
     * Verify phone number (for registration/verification)
     * POST /auth/verify-phone
     */
    @POST("auth/verify-phone")
    suspend fun verifyPhone(@Body request: VerifyPhoneDto): Response<VerifyCodeDto>

    /**
     * Confirm verification code
     * POST /auth/confirm-code
     */
    @POST("auth/confirm-code")
    suspend fun confirmVerificationCode(@Body request: VerifyCodeDto): Response<LoginResponseDto>

    /**
     * Logout user
     * POST /auth/logout
     */
    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    // ======================== PRODUCT ENDPOINTS ========================

    /**
     * Get all products with pagination
     * GET /products?page=1&limit=20
     */
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("sort") sort: String? = null
    ): Response<ProductListResponse>

    /**
     * Get products by category
     * GET /products/category/{categoryId}
     */
    @GET("products/category/{categoryId}")
    suspend fun getProductsByCategory(
        @Path("categoryId") categoryId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ProductListResponse>

    /**
     * Get single product by ID
     * GET /products/{id}
     */
    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: String): Response<ProductDto>

    /**
     * Search products
     * GET /products/search?q=keyword
     */
    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ProductListResponse>

    /**
     * Get featured/special products
     * GET /products/featured
     */
    @GET("products/featured")
    suspend fun getFeaturedProducts(
        @Query("limit") limit: Int = 10
    ): Response<ProductListResponse>

    /**
     * Get new products
     * GET /products/new
     */
    @GET("products/new")
    suspend fun getNewProducts(
        @Query("limit") limit: Int = 10
    ): Response<ProductListResponse>

    /**
     * Toggle product favorite status
     * POST /products/{id}/favorite
     */
    @POST("products/{id}/favorite")
    suspend fun toggleFavorite(@Path("id") productId: String): Response<Unit>

    // ======================== CATEGORY ENDPOINTS ========================

    /**
     * Get all categories
     * GET /categories
     */
    @GET("categories")
    suspend fun getCategories(): Response<CategoryListResponse>

    /**
     * Get category by ID
     * GET /categories/{id}
     */
    @GET("categories/{id}")
    suspend fun getCategoryById(@Path("id") categoryId: String): Response<CategoryDto>

    // ======================== CART ENDPOINTS ========================

    /**
     * Get current user's cart
     * GET /cart
     */
    @GET("cart")
    suspend fun getCart(): Response<CartSummaryDto>

    /**
     * Add item to cart
     * POST /cart/items
     */
    @POST("cart/items")
    suspend fun addToCart(@Body request: AddToCartRequestDto): Response<CartSummaryDto>

    /**
     * Update cart item quantity
     * PUT /cart/items/{id}
     */
    @PUT("cart/items/{id}")
    suspend fun updateCartItem(
        @Path("id") itemId: String,
        @Body request: UpdateCartItemDto
    ): Response<CartSummaryDto>

    /**
     * Remove item from cart
     * DELETE /cart/items/{id}
     */
    @DELETE("cart/items/{id}")
    suspend fun removeFromCart(@Path("id") itemId: String): Response<CartSummaryDto>

    /**
     * Clear entire cart
     * DELETE /cart
     */
    @DELETE("cart")
    suspend fun clearCart(): Response<Unit>

    /**
     * Apply coupon to cart
     * POST /cart/coupon
     */
    @POST("cart/coupon")
    suspend fun applyCoupon(@Body request: ApplyCouponRequestDto): Response<ApplyCouponResponseDto>

    // ======================== ORDER ENDPOINTS ========================

    /**
     * Create new order
     * POST /orders
     */
    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequestDto): Response<OrderResponseDto>

    /**
     * Get user's orders
     * GET /orders?page=1&limit=10
     */
    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<OrderListResponse>

    /**
     * Get order by ID
     * GET /orders/{id}
     */
    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") orderId: String): Response<OrderResponseDto>

    /**
     * Get order tracking info
     * GET /orders/{id}/tracking
     */
    @GET("orders/{id}/tracking")
    suspend fun trackOrder(@Path("id") orderId: String): Response<OrderTrackingDto>

    /**
     * Cancel order (if possible)
     * POST /orders/{id}/cancel
     */
    @POST("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") orderId: String): Response<OrderResponseDto>

    // ======================== PAYMENT ENDPOINTS ========================

    /**
     * Initialize payment (get payment URL)
     * POST /payment/init
     */
    @POST("payment/init")
    suspend fun initPayment(@Body request: InitPaymentRequestDto): Response<InitPaymentResponseDto>

    /**
     * Verify payment after user returns from gateway
     * POST /payment/verify
     */
    @POST("payment/verify")
    suspend fun verifyPayment(@Body request: VerifyPaymentRequestDto): Response<VerifyPaymentResponseDto>

    /**
     * Get payment status
     * GET /payment/{orderId}/status
     */
    @GET("payment/{orderId}/status")
    suspend fun getPaymentStatus(@Path("orderId") orderId: String): Response<PaymentStatusDto>

    // ======================== USER PROFILE ENDPOINTS ========================

    /**
     * Get current user's profile
     * GET /user/profile
     */
    @GET("user/profile")
    suspend fun getUserProfile(): Response<UserProfileDto>

    /**
     * Update user profile
     * PUT /user/profile
     */
    @PUT("user/profile")
    suspend fun updateUserProfile(@Body request: UpdateUserProfileDto): Response<UserProfileDto>

    /**
     * Change user password
     * POST /user/change-password
     */
    @POST("user/change-password")
    suspend fun changePassword(@Body request: Map<String, String>): Response<Unit>

    // ======================== PROMOTION ENDPOINTS ========================

    /**
     * Get all active banners
     * GET /banners
     */
    @GET("banners")
    suspend fun getBanners(): Response<BannerListResponse>

    /**
     * Get all active offers/deals
     * GET /offers
     */
    @GET("offers")
    suspend fun getOffers(): Response<OfferListResponse>

    /**
     * Validate coupon code
     * POST /coupon/validate
     */
    @POST("coupon/validate")
    suspend fun validateCoupon(@Body request: ValidateCouponRequestDto): Response<ValidateCouponResponseDto>

    // ======================== REVIEW & RATING ENDPOINTS ========================

    /**
     * Get product reviews
     * GET /products/{id}/reviews
     */
    @GET("products/{id}/reviews")
    suspend fun getProductReviews(
        @Path("id") productId: String,
        @Query("page") page: Int = 1
    ): Response<Map<String, Any>>

    /**
     * Submit product review
     * POST /products/{id}/reviews
     */
    @POST("products/{id}/reviews")
    suspend fun submitReview(
        @Path("id") productId: String,
        @Body review: Map<String, Any>
    ): Response<Unit>
}
