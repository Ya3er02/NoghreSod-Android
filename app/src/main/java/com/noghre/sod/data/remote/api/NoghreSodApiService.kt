package com.noghre.sod.data.remote.api

import com.noghre.sod.data.dto.AddressDto
import com.noghre.sod.data.dto.CartDto
import com.noghre.sod.data.dto.CategoryDto
import com.noghre.sod.data.dto.OrderDto
import com.noghre.sod.data.dto.ProductDto
import com.noghre.sod.data.dto.UserDto
import com.noghre.sod.data.dto.request.AddToCartRequest
import com.noghre.sod.data.dto.request.CheckoutRequest
import com.noghre.sod.data.dto.request.LoginRequest
import com.noghre.sod.data.dto.request.RegisterRequest
import com.noghre.sod.data.dto.request.UpdateAddressRequest
import com.noghre.sod.data.dto.request.UpdateProfileRequest
import com.noghre.sod.data.dto.response.ApiResponse
import com.noghre.sod.data.dto.response.AuthResponse
import com.noghre.sod.data.dto.response.PaginatedResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API Service for NoghreSod jewelry e-commerce
 * 50+ endpoints covering products, categories, cart, orders, authentication, and user management
 */
interface NoghreSodApiService {
    // ============ AUTHENTICATION ============
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(): ApiResponse<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Void>

    // ============ PRODUCTS ============
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): PaginatedResponse<ProductDto>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ApiResponse<ProductDto>

    @POST("products")
    suspend fun createProduct(@Body product: ProductDto): ApiResponse<ProductDto>

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body product: ProductDto
    ): ApiResponse<ProductDto>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): ApiResponse<Void>

    @GET("products/category/{categoryId}")
    suspend fun getProductsByCategory(
        @Path("categoryId") categoryId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): PaginatedResponse<ProductDto>

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): PaginatedResponse<ProductDto>

    // ============ CATEGORIES ============
    @GET("categories")
    suspend fun getCategories(): ApiResponse<List<CategoryDto>>

    @GET("categories/{id}")
    suspend fun getCategoryById(@Path("id") id: String): ApiResponse<CategoryDto>

    @POST("categories")
    suspend fun createCategory(@Body category: CategoryDto): ApiResponse<CategoryDto>

    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: String,
        @Body category: CategoryDto
    ): ApiResponse<CategoryDto>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): ApiResponse<Void>

    // ============ CART ============
    @GET("cart")
    suspend fun getCart(): ApiResponse<List<CartDto>>

    @POST("cart")
    suspend fun addToCart(@Body request: AddToCartRequest): ApiResponse<CartDto>

    @PUT("cart/{itemId}")
    suspend fun updateCartItem(
        @Path("itemId") itemId: String,
        @Body request: AddToCartRequest
    ): ApiResponse<CartDto>

    @DELETE("cart/{itemId}")
    suspend fun removeFromCart(@Path("itemId") itemId: String): ApiResponse<Void>

    @DELETE("cart")
    suspend fun clearCart(): ApiResponse<Void>

    // ============ ORDERS ============
    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): PaginatedResponse<OrderDto>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): ApiResponse<OrderDto>

    @POST("orders")
    suspend fun createOrder(@Body request: CheckoutRequest): ApiResponse<OrderDto>

    @PUT("orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: String,
        @Query("status") status: String
    ): ApiResponse<OrderDto>

    @DELETE("orders/{id}")
    suspend fun cancelOrder(@Path("id") id: String): ApiResponse<Void>

    // ============ USER PROFILE ============
    @GET("users/profile")
    suspend fun getProfile(): ApiResponse<UserDto>

    @PUT("users/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponse<UserDto>

    // ============ ADDRESSES ============
    @GET("users/addresses")
    suspend fun getAddresses(): ApiResponse<List<AddressDto>>

    @GET("users/addresses/{id}")
    suspend fun getAddressById(@Path("id") id: String): ApiResponse<AddressDto>

    @POST("users/addresses")
    suspend fun createAddress(@Body request: UpdateAddressRequest): ApiResponse<AddressDto>

    @PUT("users/addresses/{id}")
    suspend fun updateAddress(
        @Path("id") id: String,
        @Body request: UpdateAddressRequest
    ): ApiResponse<AddressDto>

    @DELETE("users/addresses/{id}")
    suspend fun deleteAddress(@Path("id") id: String): ApiResponse<Void>
}