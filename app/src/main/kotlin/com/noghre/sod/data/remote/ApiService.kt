package com.noghre.sod.data.remote

import retrofit2.http.*
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val image: String,
    val category: String,
    val rating: Double,
    val reviews: Int
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val avatar: String?
)

@Serializable
data class AuthRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserDto
)

interface ApiService {
    // Auth
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @POST("api/v1/auth/logout")
    suspend fun logout()

    // Products
    @GET("api/v1/products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null
    ): List<ProductDto>

    @GET("api/v1/products/{id}")
    suspend fun getProduct(@Path("id") id: String): ProductDto

    @GET("api/v1/products/search")
    suspend fun searchProducts(@Query("q") query: String): List<ProductDto>

    // User Profile
    @GET("api/v1/users/profile")
    suspend fun getUserProfile(): UserDto

    @PUT("api/v1/users/profile")
    suspend fun updateUserProfile(@Body user: UserDto): UserDto

    // Orders
    @GET("api/v1/orders")
    suspend fun getOrders(): List<OrderDto>

    @POST("api/v1/orders")
    suspend fun createOrder(@Body order: CreateOrderRequest): OrderDto

    @GET("api/v1/orders/{id}")
    suspend fun getOrder(@Path("id") id: String): OrderDto
}

@Serializable
data class OrderDto(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val total: Double,
    val status: String,
    val createdAt: String
)

@Serializable
data class OrderItem(
    val productId: String,
    val quantity: Int,
    val price: Double
)

@Serializable
data class CreateOrderRequest(
    val items: List<OrderItem>,
    val shippingAddress: String
)
