package com.noghre.sod.data.remote.api

import com.noghre.sod.data.remote.dto.*
import retrofit2.http.*

/**
 * Retrofit API Service interface for Noghresod
 * Defines all endpoints for the jewelry e-commerce application
 */
interface ApiService {

    // ==================== Authentication Endpoints ====================

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): ResponseDto<AuthTokenDto>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): ResponseDto<AuthTokenDto>

    @POST("auth/login-phone")
    suspend fun loginWithPhone(@Body request: LoginPhoneRequestDto): ResponseDto<AuthTokenDto>

    @POST("auth/request-otp")
    suspend fun requestOTP(@Body request: PhoneRequestDto): ResponseDto<Unit>

    @POST("auth/verify-otp")
    suspend fun verifyOTP(@Body request: VerifyOTPRequestDto): ResponseDto<Boolean>

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): ResponseDto<AuthTokenDto>

    @POST("auth/logout")
    suspend fun logout(): ResponseDto<Unit>

    // ==================== Product Endpoints ====================

    @GET("products")
    suspend fun getAllProducts(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): ResponseDto<PaginatedResponseDto<ProductDto>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: String): ResponseDto<ProductDetailDto>

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): ResponseDto<PaginatedResponseDto<ProductDto>>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): ResponseDto<PaginatedResponseDto<ProductDto>>

    @GET("products/featured")
    suspend fun getFeaturedProducts(): ResponseDto<List<ProductDto>>

    @GET("products/{id}/reviews")
    suspend fun getProductReviews(
        @Path("id") productId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): ResponseDto<PaginatedResponseDto<ProductReviewDto>>

    @POST("products/{id}/reviews")
    suspend fun addProductReview(
        @Path("id") productId: String,
        @Body request: AddReviewRequestDto,
    ): ResponseDto<ProductReviewDto>

    // ==================== Cart Endpoints ====================

    @GET("cart")
    suspend fun getCart(): ResponseDto<CartDto>

    @POST("cart/items")
    suspend fun addToCart(@Body request: AddToCartRequestDto): ResponseDto<CartItemDto>

    @PUT("cart/items/{itemId}")
    suspend fun updateCartItem(
        @Path("itemId") itemId: String,
        @Body request: UpdateCartItemRequestDto,
    ): ResponseDto<CartItemDto>

    @DELETE("cart/items/{itemId}")
    suspend fun removeFromCart(@Path("itemId") itemId: String): ResponseDto<Unit>

    @DELETE("cart")
    suspend fun clearCart(): ResponseDto<Unit>

    @POST("cart/discount")
    suspend fun applyDiscountCode(@Body request: ApplyDiscountRequestDto): ResponseDto<CartDto>

    @DELETE("cart/discount")
    suspend fun removeDiscountCode(): ResponseDto<CartDto>

    @POST("cart/validate")
    suspend fun validateCart(): ResponseDto<CartValidationDto>

    // ==================== Order Endpoints ====================

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequestDto): ResponseDto<OrderDto>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") orderId: String): ResponseDto<OrderDto>

    @GET("orders")
    suspend fun getUserOrders(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): ResponseDto<PaginatedResponseDto<OrderDto>>

    @POST("orders/{id}/cancel")
    suspend fun cancelOrder(
        @Path("id") orderId: String,
        @Body request: CancelOrderRequestDto,
    ): ResponseDto<OrderDto>

    @GET("orders/{id}/tracking")
    suspend fun getOrderTracking(@Path("id") orderId: String): ResponseDto<OrderTrackingDto>

    @POST("orders/{id}/return")
    suspend fun requestReturn(
        @Path("id") orderId: String,
        @Body request: RequestReturnDto,
    ): ResponseDto<ReturnRequestDto>

    @POST("orders/{id}/verify-payment")
    suspend fun verifyPayment(
        @Path("id") orderId: String,
        @Body request: VerifyPaymentRequestDto,
    ): ResponseDto<PaymentVerificationDto>

    // ==================== User Endpoints ====================

    @GET("user/profile")
    suspend fun getUserProfile(): ResponseDto<UserDto>

    @PUT("user/profile")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequestDto): ResponseDto<UserDto>

    @POST("user/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): ResponseDto<Unit>

    @POST("user/request-password-reset")
    suspend fun requestPasswordReset(@Body request: PasswordResetRequestDto): ResponseDto<Unit>

    @POST("user/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): ResponseDto<Unit>

    @GET("user/addresses")
    suspend fun getShippingAddresses(): ResponseDto<List<AddressDto>>

    @POST("user/addresses")
    suspend fun addShippingAddress(@Body request: AddressDto): ResponseDto<AddressDto>

    @PUT("user/addresses/{id}")
    suspend fun updateShippingAddress(
        @Path("id") addressId: String,
        @Body request: AddressDto,
    ): ResponseDto<AddressDto>

    @DELETE("user/addresses/{id}")
    suspend fun deleteShippingAddress(@Path("id") addressId: String): ResponseDto<Unit>

    @POST("user/addresses/{id}/default")
    suspend fun setDefaultShippingAddress(@Path("id") addressId: String): ResponseDto<Unit>

    @PUT("user/preferences")
    suspend fun updateUserPreferences(@Body request: UserPreferencesDto): ResponseDto<UserPreferencesDto>

    @GET("user/favorites")
    suspend fun getFavoriteProducts(): ResponseDto<List<ProductDto>>

    @POST("user/favorites/{id}")
    suspend fun addToFavorites(@Path("id") productId: String): ResponseDto<Unit>

    @DELETE("user/favorites/{id}")
    suspend fun removeFromFavorites(@Path("id") productId: String): ResponseDto<Unit>

    @GET("user/security")
    suspend fun getSecuritySettings(): ResponseDto<SecuritySettingsDto>
}
