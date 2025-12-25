package com.noghre.sod.data.remote.api

import retrofit2.Response
import retrofit2.http.*
import com.noghre.sod.data.remote.dto.common.ResponseDto
import com.noghre.sod.data.remote.dto.common.PaginatedResponseDto
import com.noghre.sod.data.remote.dto.common.PaginationInfo
import com.noghre.sod.data.remote.dto.response.*
import com.noghre.sod.data.remote.dto.request.*
import java.util.UUID

/**
 * Main API Service
 * 
 * All endpoints are protected with authentication.
 * Uses Response<T> wrapper to access HTTP status codes and headers.
 */
interface ApiService {
    
    // ==================== Products ====================
    
    /**
     * Get paginated list of products
     * Supports both page-based and cursor-based pagination
     */
    @GET("products")
    suspend fun getAllProducts(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null,
        @Query("category") category: String? = null,
        @Query("minPrice") minPrice: Long? = null,
        @Query("maxPrice") maxPrice: Long? = null,
        @Query("inStock") inStock: Boolean? = null
    ): Response<ResponseDto<PaginatedResponseDto<ProductDto>>>
    
    /**
     * Get product details
     */
    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") productId: String
    ): Response<ResponseDto<ProductDetailDto>>
    
    /**
     * Search products
     */
    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("filters") filters: String? = null
    ): Response<ResponseDto<PaginatedResponseDto<ProductDto>>>
    
    /**
     * Get featured products
     * Cached for 5 minutes
     */
    @GET("products/featured")
    suspend fun getFeaturedProducts(
        @Query("limit") limit: Int = 10
    ): Response<ResponseDto<List<ProductDto>>>
    
    /**
     * Get new products
     * Cached for 1 hour
     */
    @GET("products/new")
    suspend fun getNewProducts(
        @Query("limit") limit: Int = 10
    ): Response<ResponseDto<List<ProductDto>>>
    
    // ==================== Categories ====================
    
    /**
     * Get all categories
     * Cached for 2 hours
     */
    @GET("categories")
    suspend fun getAllCategories(): Response<ResponseDto<List<CategoryDto>>>
    
    /**
     * Get category by ID
     */
    @GET("categories/{id}")
    suspend fun getCategoryById(
        @Path("id") categoryId: String
    ): Response<ResponseDto<CategoryDto>>
    
    // ==================== Cart ====================
    
    /**
     * Get current user's cart
     */
    @GET("cart")
    suspend fun getCart(): Response<ResponseDto<CartDto>>
    
    /**
     * Add item to cart
     */
    @POST("cart/items")
    suspend fun addToCart(
        @Body request: AddToCartRequestDto
    ): Response<ResponseDto<CartDto>>
    
    /**
     * Update cart item quantity
     */
    @PATCH("cart/items/{itemId}")
    suspend fun updateCartItem(
        @Path("itemId") itemId: String,
        @Body request: UpdateCartItemRequestDto
    ): Response<ResponseDto<CartDto>>
    
    /**
     * Remove item from cart
     */
    @DELETE("cart/items/{itemId}")
    suspend fun removeFromCart(
        @Path("itemId") itemId: String
    ): Response<ResponseDto<CartDto>>
    
    /**
     * Clear entire cart
     */
    @DELETE("cart")
    suspend fun clearCart(): Response<ResponseDto<Unit>>
    
    // ==================== Orders ====================
    
    /**
     * Create new order
     * @param idempotencyKey Unique key to prevent duplicate orders
     */
    @POST("orders")
    suspend fun createOrder(
        @Body request: CreateOrderRequestDto,
        @Header("Idempotency-Key") idempotencyKey: String = UUID.randomUUID().toString()
    ): Response<ResponseDto<OrderDto>>
    
    /**
     * Get order by ID
     */
    @GET("orders/{id}")
    suspend fun getOrderById(
        @Path("id") orderId: String,
        @Query("includeItems") includeItems: Boolean = true
    ): Response<ResponseDto<OrderDto>>
    
    /**
     * Get user's orders with pagination
     */
    @GET("orders")
    suspend fun getUserOrders(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("status") status: String? = null,
        @Query("fromDate") fromDate: String? = null,
        @Query("toDate") toDate: String? = null
    ): Response<ResponseDto<PaginatedResponseDto<OrderDto>>>
    
    /**
     * Cancel order
     */
    @POST("orders/{id}/cancel")
    suspend fun cancelOrder(
        @Path("id") orderId: String,
        @Body request: CancelOrderRequestDto
    ): Response<ResponseDto<OrderDto>>
    
    // ==================== User Profile ====================
    
    /**
     * Get user profile
     */
    @GET("users/me")
    suspend fun getUserProfile(): Response<ResponseDto<UserDto>>
    
    /**
     * Update user profile
     */
    @PUT("users/me")
    suspend fun updateUserProfile(
        @Body request: UpdateProfileRequestDto
    ): Response<ResponseDto<UserDto>>
    
    /**
     * Get user addresses
     */
    @GET("users/me/addresses")
    suspend fun getUserAddresses(): Response<ResponseDto<List<AddressDto>>>
    
    /**
     * Add new address
     */
    @POST("users/me/addresses")
    suspend fun addAddress(
        @Body request: AddAddressRequestDto
    ): Response<ResponseDto<AddressDto>>
    
    /**
     * Update address
     */
    @PUT("users/me/addresses/{id}")
    suspend fun updateAddress(
        @Path("id") addressId: String,
        @Body request: AddAddressRequestDto
    ): Response<ResponseDto<AddressDto>>
    
    /**
     * Delete address
     */
    @DELETE("users/me/addresses/{id}")
    suspend fun deleteAddress(
        @Path("id") addressId: String
    ): Response<ResponseDto<Unit>>
    
    // ==================== Wishlist ====================
    
    /**
     * Get user's wishlist
     */
    @GET("users/me/wishlist")
    suspend fun getWishlist(): Response<ResponseDto<List<ProductDto>>>
    
    /**
     * Add product to wishlist
     */
    @POST("users/me/wishlist/{productId}")
    suspend fun addToWishlist(
        @Path("productId") productId: String
    ): Response<ResponseDto<Unit>>
    
    /**
     * Remove product from wishlist
     */
    @DELETE("users/me/wishlist/{productId}")
    suspend fun removeFromWishlist(
        @Path("productId") productId: String
    ): Response<ResponseDto<Unit>>
    
    // ==================== Reviews ====================
    
    /**
     * Get product reviews
     */
    @GET("products/{productId}/reviews")
    suspend fun getProductReviews(
        @Path("productId") productId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<ResponseDto<PaginatedResponseDto<ReviewDto>>>
    
    /**
     * Submit product review
     */
    @POST("products/{productId}/reviews")
    suspend fun submitReview(
        @Path("productId") productId: String,
        @Body request: SubmitReviewRequestDto
    ): Response<ResponseDto<ReviewDto>>
}
