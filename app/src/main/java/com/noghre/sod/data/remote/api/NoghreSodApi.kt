package com.noghre.sod.data.remote.api

import com.noghre.sod.data.remote.dto.*
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API interface for Noghre Sod e-commerce API.
 * All endpoints return Response<T> which should be wrapped with safeApiCall().
 *
 * Features:
 * - Complete CRUD operations for all resources
 * - Pagination support with metadata
 * - Real-time streaming with Flow
 * - Type-safe request/response handling
 * - Proper HTTP method semantics
 *
 * @author Yaser
 * @version 1.0.0
 */
interface NoghreSodApi {

    // ============== AUTHENTICATION ==============

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<UserDto>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @POST("auth/refresh-token")
    suspend fun refreshToken(): Response<ApiResponse<LoginResponse>>

    // ============== PRODUCTS ==============

    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null,
        @Query("sort") sort: String? = null
    ): Response<PaginatedResponse<ProductDto>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: String): Response<ApiResponse<ProductDto>>

    @GET("products/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<ProductDto>>

    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<CategoryDto>>>

    @GET("categories/{id}/products")
    suspend fun getProductsByCategory(
        @Path("id") categoryId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<ProductDto>>

    // ============== REVIEWS & RATINGS ==============

    @GET("products/{id}/reviews")
    suspend fun getProductReviews(
        @Path("id") productId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<PaginatedResponse<ReviewDto>>

    @POST("products/{id}/reviews")
    suspend fun submitReview(
        @Path("id") productId: String,
        @Body request: SubmitReviewRequest
    ): Response<ApiResponse<ReviewDto>>

    @PUT("reviews/{id}")
    suspend fun updateReview(
        @Path("id") reviewId: String,
        @Body request: SubmitReviewRequest
    ): Response<ApiResponse<ReviewDto>>

    @DELETE("reviews/{id}")
    suspend fun deleteReview(@Path("id") reviewId: String): Response<ApiResponse<Unit>>

    // ============== CART ==============

    @GET("cart")
    suspend fun getCart(): Response<ApiResponse<CartDto>>

    @POST("cart/items")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<ApiResponse<CartDto>>

    @PUT("cart/items/{id}")
    suspend fun updateCartItem(
        @Path("id") itemId: String,
        @Body request: UpdateCartItemRequest
    ): Response<ApiResponse<CartDto>>

    @DELETE("cart/items/{id}")
    suspend fun removeCartItem(@Path("id") itemId: String): Response<ApiResponse<CartDto>>

    @DELETE("cart")
    suspend fun clearCart(): Response<ApiResponse<Unit>>

    @GET("cart/stream")
    fun getCartStream(): Flow<Response<ApiResponse<CartDto>>>

    // ============== ORDERS ==============

    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<OrderDto>>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") orderId: String): Response<ApiResponse<OrderDto>>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<OrderDto>>

    @PUT("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") orderId: String): Response<ApiResponse<OrderDto>>

    @GET("orders/{id}/tracking/stream")
    fun getOrderTrackingStream(@Path("id") orderId: String): Flow<Response<ApiResponse<OrderTrackingDto>>>

    // ============== PAYMENTS ==============

    @POST("payments")
    suspend fun processPayment(@Body request: ProcessPaymentRequest): Response<ApiResponse<PaymentDto>>

    @GET("payments/{id}")
    suspend fun getPaymentStatus(@Path("id") paymentId: String): Response<ApiResponse<PaymentDto>>

    // ============== USER PROFILE ==============

    @GET("users/profile")
    suspend fun getUserProfile(): Response<ApiResponse<UserDto>>

    @PUT("users/profile")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserDto>>

    @GET("users/addresses")
    suspend fun getUserAddresses(): Response<ApiResponse<List<AddressDto>>>

    @POST("users/addresses")
    suspend fun addAddress(@Body request: AddAddressRequest): Response<ApiResponse<UserDto>>

    @PUT("users/addresses/{id}")
    suspend fun updateAddress(
        @Path("id") addressId: String,
        @Body request: AddAddressRequest
    ): Response<ApiResponse<UserDto>>

    @DELETE("users/addresses/{id}")
    suspend fun deleteAddress(@Path("id") addressId: String): Response<ApiResponse<Unit>>

    // ============== NOTIFICATIONS ==============

    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<NotificationDto>>

    @PUT("notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") notificationId: String): Response<ApiResponse<Unit>>

    @PUT("notifications/read-all")
    suspend fun markAllNotificationsAsRead(): Response<ApiResponse<Unit>>

    @POST("notifications/token")
    suspend fun registerFcmToken(@Body request: RegisterFcmTokenRequest): Response<ApiResponse<Unit>>

    @DELETE("notifications/{id}")
    suspend fun deleteNotification(@Path("id") notificationId: String): Response<ApiResponse<Unit>>

    // ============== FAVORITES ==============

    @GET("favorites")
    suspend fun getFavorites(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<ProductDto>>

    @POST("favorites/{id}")
    suspend fun addToFavorites(@Path("id") productId: String): Response<ApiResponse<Unit>>

    @DELETE("favorites/{id}")
    suspend fun removeFromFavorites(@Path("id") productId: String): Response<ApiResponse<Unit>>
}
