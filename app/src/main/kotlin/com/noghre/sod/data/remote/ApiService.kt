package com.noghre.sod.data.remote

from retrofit2.http.*
import com.noghre.sod.data.dto.ApiResponse
import com.noghre.sod.data.dto.PaginatedResponse
import com.noghre.sod.data.dto.ProductDto
import com.noghre.sod.data.dto.OrderDto
import com.noghre.sod.data.model.User

/**
 * Retrofit API service interface for NoghreSod Marketplace.
 */
interface ApiService {

    // ===== Products =====
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("category") category: String? = null
    ): ApiResponse<PaginatedResponse<ProductDto>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ApiResponse<ProductDto>

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("page") page: Int = 1
    ): ApiResponse<PaginatedResponse<ProductDto>>

    @GET("categories")
    suspend fun getCategories(): ApiResponse<List<String>>

    // ===== Cart =====
    @GET("cart")
    suspend fun getCart(): ApiResponse<List<ProductDto>>

    @POST("cart/add")
    suspend fun addToCart(
        @Body request: AddToCartRequest
    ): ApiResponse<String>

    @POST("cart/remove/{productId}")
    suspend fun removeFromCart(@Path("productId") productId: String): ApiResponse<String>

    @POST("cart/update/{productId}")
    suspend fun updateCartItem(
        @Path("productId") productId: String,
        @Body request: UpdateCartRequest
    ): ApiResponse<String>

    @POST("cart/clear")
    suspend fun clearCart(): ApiResponse<String>

    // ===== Orders =====
    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1
    ): ApiResponse<PaginatedResponse<OrderDto>>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): ApiResponse<OrderDto>

    @POST("orders/create")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): ApiResponse<OrderDto>

    @POST("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: String): ApiResponse<String>

    // ===== User Profile =====
    @GET("user/profile")
    suspend fun getUserProfile(): ApiResponse<User>

    @PUT("user/profile")
    suspend fun updateUserProfile(
        @Body request: UpdateUserRequest
    ): ApiResponse<User>

    @GET("user/wishlist")
    suspend fun getWishlist(): ApiResponse<List<ProductDto>>

    @POST("user/wishlist/add")
    suspend fun addToWishlist(@Body request: WishlistRequest): ApiResponse<String>

    @POST("user/wishlist/remove")
    suspend fun removeFromWishlist(@Body request: WishlistRequest): ApiResponse<String>
}

// Request models
data class AddToCartRequest(
    val productId: String,
    val quantity: Int
)

data class UpdateCartRequest(
    val quantity: Int
)

data class CreateOrderRequest(
    val items: List<OrderItemRequest>,
    val shippingAddress: String,
    val paymentMethodId: String
)

data class OrderItemRequest(
    val productId: String,
    val quantity: Int
)

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null
)

data class WishlistRequest(
    val productId: String
)
