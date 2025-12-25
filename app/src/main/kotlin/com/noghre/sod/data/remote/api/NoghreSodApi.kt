package com.noghre.sod.data.remote.api

import com.noghre.sod.data.remote.dto.ProductDto
import com.noghre.sod.data.remote.dto.OrderDto
import com.noghre.sod.data.remote.dto.UserDto
import com.noghre.sod.data.remote.dto.CartDto
import com.noghre.sod.data.remote.dto.PaymentDto
import com.noghre.sod.data.remote.dto.LoginRequest
import com.noghre.sod.data.remote.dto.LoginResponse
import com.noghre.sod.data.remote.dto.RegisterRequest
import com.noghre.sod.data.remote.dto.ApiResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API interface for Noghresod backend.
 * Defines all REST endpoints for the application.
 *
 * @author Yaser
 * @version 1.0.0
 */
interface NoghreSodApi {

    // ============== AUTHENTICATION ==============

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<UserDto>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @POST("auth/refresh")
    suspend fun refreshToken(): Response<LoginResponse>

    // ============== PRODUCTS ==============

    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null,
        @Query("sort") sort: String? = null
    ): Response<ApiResponse<List<ProductDto>>>

    @GET("products/featured")
    suspend fun getFeaturedProducts(): Response<ApiResponse<List<ProductDto>>>

    @GET("products/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<ProductDto>>>

    @GET("products/{id}")
    suspend fun getProductDetail(
        @Path("id") productId: String
    ): Response<ApiResponse<ProductDto>>

    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<String>>>

    // ============== CART ==============

    @GET("cart")
    suspend fun getCart(): Response<ApiResponse<CartDto>>

    @POST("cart/items")
    suspend fun addToCart(
        @Body item: Map<String, Any>
    ): Response<ApiResponse<CartDto>>

    @PUT("cart/items/{id}")
    suspend fun updateCartItem(
        @Path("id") itemId: String,
        @Body data: Map<String, Any>
    ): Response<ApiResponse<CartDto>>

    @DELETE("cart/items/{id}")
    suspend fun removeFromCart(
        @Path("id") itemId: String
    ): Response<ApiResponse<CartDto>>

    @DELETE("cart")
    suspend fun clearCart(): Response<ApiResponse<Unit>>

    // ============== ORDERS ==============

    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<OrderDto>>>

    @GET("orders/{id}")
    suspend fun getOrderDetail(
        @Path("id") orderId: String
    ): Response<ApiResponse<OrderDto>>

    @POST("orders")
    suspend fun createOrder(
        @Body orderData: Map<String, Any>
    ): Response<ApiResponse<OrderDto>>

    @GET("orders/{id}/tracking")
    suspend fun getOrderTracking(
        @Path("id") orderId: String
    ): Response<ApiResponse<OrderDto>>

    // ============== PAYMENTS ==============

    @POST("payments")
    suspend fun processPayment(
        @Body paymentData: Map<String, Any>
    ): Response<ApiResponse<PaymentDto>>

    @GET("payments/{id}")
    suspend fun getPaymentStatus(
        @Path("id") paymentId: String
    ): Response<ApiResponse<PaymentDto>>

    // ============== USER PROFILE ==============

    @GET("users/profile")
    suspend fun getUserProfile(): Response<ApiResponse<UserDto>>

    @PUT("users/profile")
    suspend fun updateUserProfile(
        @Body userData: Map<String, Any>
    ): Response<ApiResponse<UserDto>>

    @POST("users/addresses")
    suspend fun addAddress(
        @Body addressData: Map<String, Any>
    ): Response<ApiResponse<UserDto>>

    @PUT("users/addresses/{id}")
    suspend fun updateAddress(
        @Path("id") addressId: String,
        @Body addressData: Map<String, Any>
    ): Response<ApiResponse<UserDto>>

    @DELETE("users/addresses/{id}")
    suspend fun deleteAddress(
        @Path("id") addressId: String
    ): Response<ApiResponse<Unit>>

    // ============== FAVORITES ==============

    @POST("favorites/{productId}")
    suspend fun addToFavorites(
        @Path("productId") productId: String
    ): Response<ApiResponse<Unit>>

    @DELETE("favorites/{productId}")
    suspend fun removeFromFavorites(
        @Path("productId") productId: String
    ): Response<ApiResponse<Unit>>

    @GET("favorites")
    suspend fun getFavorites(): Response<ApiResponse<List<ProductDto>>>
}
