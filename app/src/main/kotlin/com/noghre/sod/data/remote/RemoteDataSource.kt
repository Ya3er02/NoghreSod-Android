package com.noghre.sod.data.remote

import com.noghre.sod.data.dto.*
import com.noghre.sod.data.remote.api.ApiService
import retrofit2.HttpException
import java.io.IOException

/**
 * Remote Data Source Interface
 * Defines contract for all API operations
 */
interface RemoteDataSource {

    // ==================== Authentication ====================
    suspend fun login(phone: String, password: String): Result<UserTokenDto>
    suspend fun logout(): Result<Unit>
    suspend fun refreshToken(refreshToken: String): Result<UserTokenDto>

    // ==================== Products ====================
    suspend fun getProducts(page: Int = 1, limit: Int = 20): Result<List<ProductDto>>
    suspend fun getProductsByCategory(categoryId: String, page: Int = 1): Result<List<ProductDto>>
    suspend fun getProductById(productId: String): Result<ProductDto>
    suspend fun searchProducts(query: String, page: Int = 1): Result<List<ProductDto>>
    suspend fun getFeaturedProducts(limit: Int = 10): Result<List<ProductDto>>
    suspend fun getNewProducts(limit: Int = 10): Result<List<ProductDto>>
    suspend fun toggleProductFavorite(productId: String): Result<Unit>

    // ==================== Categories ====================
    suspend fun getCategories(): Result<List<CategoryDto>>

    // ==================== Cart ====================
    suspend fun getCart(): Result<CartSummaryDto>
    suspend fun addToCart(request: AddToCartRequestDto): Result<CartSummaryDto>
    suspend fun updateCartItem(itemId: String, request: UpdateCartItemDto): Result<CartSummaryDto>
    suspend fun removeFromCart(itemId: String): Result<CartSummaryDto>
    suspend fun clearCart(): Result<Unit>
    suspend fun applyCoupon(request: ApplyCouponRequestDto): Result<ApplyCouponResponseDto>

    // ==================== Orders ====================
    suspend fun createOrder(request: CreateOrderRequestDto): Result<OrderResponseDto>
    suspend fun getOrders(page: Int = 1, limit: Int = 10): Result<List<OrderResponseDto>>
    suspend fun getOrderById(orderId: String): Result<OrderResponseDto>
    suspend fun trackOrder(orderId: String): Result<OrderTrackingDto>
    suspend fun cancelOrder(orderId: String): Result<OrderResponseDto>

    // ==================== Payment ====================
    suspend fun initPayment(request: InitPaymentRequestDto): Result<InitPaymentResponseDto>
    suspend fun verifyPayment(request: VerifyPaymentRequestDto): Result<VerifyPaymentResponseDto>
    suspend fun getPaymentStatus(orderId: String): Result<PaymentStatusDto>

    // ==================== User Profile ====================
    suspend fun getUserProfile(): Result<UserProfileDto>
    suspend fun updateUserProfile(request: UpdateUserProfileDto): Result<UserProfileDto>
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>

    // ==================== Promotions ====================
    suspend fun getBanners(): Result<List<BannerDto>>
    suspend fun getOffers(): Result<List<OfferDto>>
    suspend fun validateCoupon(request: ValidateCouponRequestDto): Result<ValidateCouponResponseDto>
}

/**
 * Remote Data Source Implementation
 * Uses Retrofit ApiService for API communication
 * Handles error parsing and Result wrapping
 */
class RemoteDataSourceImpl(private val apiService: ApiService) : RemoteDataSource {

    // ==================== Authentication ====================

    override suspend fun login(phone: String, password: String): Result<UserTokenDto> {
        return safeApiCall {
            val request = LoginRequestDto(phone, password)
            val response = apiService.login(request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun logout(): Result<Unit> {
        return safeApiCall {
            val response = apiService.logout()
            if (response.isSuccessful) {
                Unit
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<UserTokenDto> {
        return safeApiCall {
            val request = RefreshTokenDto(refreshToken)
            val response = apiService.refreshToken(request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    // ==================== Products ====================

    override suspend fun getProducts(page: Int, limit: Int): Result<List<ProductDto>> {
        return safeApiCall {
            val response = apiService.getProducts(page, limit)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun getProductsByCategory(
        categoryId: String,
        page: Int
    ): Result<List<ProductDto>> {
        return safeApiCall {
            val response = apiService.getProductsByCategory(categoryId, page)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun getProductById(productId: String): Result<ProductDto> {
        return safeApiCall {
            val response = apiService.getProductById(productId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun searchProducts(query: String, page: Int): Result<List<ProductDto>> {
        return safeApiCall {
            val response = apiService.searchProducts(query, page)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun getFeaturedProducts(limit: Int): Result<List<ProductDto>> {
        return safeApiCall {
            val response = apiService.getFeaturedProducts(limit)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun getNewProducts(limit: Int): Result<List<ProductDto>> {
        return safeApiCall {
            val response = apiService.getNewProducts(limit)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun toggleProductFavorite(productId: String): Result<Unit> {
        return safeApiCall {
            val response = apiService.toggleFavorite(productId)
            if (response.isSuccessful) {
                Unit
            } else {
                throw HttpException(response)
            }
        }
    }

    // ==================== Categories ====================

    override suspend fun getCategories(): Result<List<CategoryDto>> {
        return safeApiCall {
            val response = apiService.getCategories()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw HttpException(response)
            }
        }
    }

    // ==================== Cart ====================

    override suspend fun getCart(): Result<CartSummaryDto> {
        return safeApiCall {
            val response = apiService.getCart()
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun addToCart(request: AddToCartRequestDto): Result<CartSummaryDto> {
        return safeApiCall {
            val response = apiService.addToCart(request)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun updateCartItem(
        itemId: String,
        request: UpdateCartItemDto
    ): Result<CartSummaryDto> {
        return safeApiCall {
            val response = apiService.updateCartItem(itemId, request)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun removeFromCart(itemId: String): Result<CartSummaryDto> {
        return safeApiCall {
            val response = apiService.removeFromCart(itemId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun clearCart(): Result<Unit> {
        return safeApiCall {
            val response = apiService.clearCart()
            if (response.isSuccessful) {
                Unit
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun applyCoupon(request: ApplyCouponRequestDto): Result<ApplyCouponResponseDto> {
        return safeApiCall {
            val response = apiService.applyCoupon(request)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    // ==================== Orders ====================

    override suspend fun createOrder(request: CreateOrderRequestDto): Result<OrderResponseDto> {
        return safeApiCall {
            val response = apiService.createOrder(request)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun getOrders(page: Int, limit: Int): Result<List<OrderResponseDto>> {
        return safeApiCall {
            val response = apiService.getOrders(page, limit)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun getOrderById(orderId: String): Result<OrderResponseDto> {
        return safeApiCall {
            val response = apiService.getOrderById(orderId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun trackOrder(orderId: String): Result<OrderTrackingDto> {
        return safeApiCall {
            val response = apiService.trackOrder(orderId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun cancelOrder(orderId: String): Result<OrderResponseDto> {
        return safeApiCall {
            val response = apiService.cancelOrder(orderId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    // ==================== Payment ====================

    override suspend fun initPayment(request: InitPaymentRequestDto): Result<InitPaymentResponseDto> {
        return safeApiCall {
            val response = apiService.initPayment(request)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun verifyPayment(request: VerifyPaymentRequestDto): Result<VerifyPaymentResponseDto> {
        return safeApiCall {
            val response = apiService.verifyPayment(request)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun getPaymentStatus(orderId: String): Result<PaymentStatusDto> {
        return safeApiCall {
            val response = apiService.getPaymentStatus(orderId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    // ==================== User Profile ====================

    override suspend fun getUserProfile(): Result<UserProfileDto> {
        return safeApiCall {
            val response = apiService.getUserProfile()
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun updateUserProfile(request: UpdateUserProfileDto): Result<UserProfileDto> {
        return safeApiCall {
            val response = apiService.updateUserProfile(request)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return safeApiCall {
            val request = mapOf(
                "old_password" to oldPassword,
                "new_password" to newPassword
            )
            val response = apiService.changePassword(request)
            if (response.isSuccessful) {
                Unit
            } else {
                throw HttpException(response)
            }
        }
    }

    // ==================== Promotions ====================

    override suspend fun getBanners(): Result<List<BannerDto>> {
        return safeApiCall {
            val response = apiService.getBanners()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun getOffers(): Result<List<OfferDto>> {
        return safeApiCall {
            val response = apiService.getOffers()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw HttpException(response)
            }
        }
    }

    override suspend fun validateCoupon(request: ValidateCouponRequestDto): Result<ValidateCouponResponseDto> {
        return safeApiCall {
            val response = apiService.validateCoupon(request)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        }
    }

    // ==================== Utility ====================

    /**
     * Safely wrap API calls with error handling
     */
    private inline fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            Result.failure(throwable)
        }
    }
}
