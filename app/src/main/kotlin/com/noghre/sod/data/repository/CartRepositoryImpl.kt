package com.noghre.sod.data.repository

import com.noghre.sod.core.error.*
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.remote.dto.AddToCartRequestDto
import com.noghre.sod.data.remote.dto.CartItemDto
import com.noghre.sod.domain.model.*
import com.noghre.sod.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

/**
 * 🛒 Cart Repository Implementation
 * 
 * Handles shopping cart operations with comprehensive error handling.
 * All operations return Result<T> with proper error classification.
 */
class CartRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val exceptionHandler: GlobalExceptionHandler
) : CartRepository {

    /**
     * 📝 Get user's shopping cart
     */
    override fun getCart(): Flow<Result<Cart>> = flow {
        try {
            emit(Result.Loading)
            Timber.d("[CART] Fetching cart")
            
            val response = apiService.getCart()
            
            if (response.isSuccessful) {
                if (response.data != null) {
                    val cart = response.data.toCart()
                    Timber.d("[CART] Cart loaded: ${cart.items.size} items")
                    emit(Result.Success(cart))
                } else {
                    Timber.w("[CART] Cart response is empty")
                    emit(Result.Error(AppError.Network(
                        message = "پاسخ سرور نامعتبر",
                        statusCode = 200
                    )))
                }
            } else {
                Timber.w("[CART] HTTP error: ${response.code()}")
                emit(Result.Error(AppError.Network(
                    message = response.message ?: "بارگذاری سبد ناموفق",
                    statusCode = response.code()
                )))
            }
        } catch (e: java.net.UnknownHostException) {
            Timber.e(e, "[CART] Network error")
            emit(Result.Error(AppError.Network(
                message = "بدون دسترسی به اینترنت",
                statusCode = null
            )))
        } catch (e: java.net.SocketTimeoutException) {
            Timber.e(e, "[CART] Network timeout")
            emit(Result.Error(AppError.Network(
                message = "زمان اتصال تمام شد",
                statusCode = null
            )))
        } catch (e: Exception) {
            Timber.e(e, "[CART] Unexpected error")
            emit(Result.Error(exceptionHandler.handleException(e)))
        }
    }

    /**
     * ➕ Add product to cart
     */
    override suspend fun addToCart(
        productId: String,
        product: Product,
        quantity: Int,
    ): Result<CartItem> {
        return try {
            Timber.d("[CART] Adding to cart: product=$productId, qty=$quantity")
            
            // Validate inputs
            if (productId.isBlank()) {
                Timber.w("[CART] Invalid product ID")
                return Result.Error(AppError.Validation(
                    message = "شناسه محصول نامعتبر",
                    field = "productId"
                ))
            }
            
            if (quantity <= 0) {
                Timber.w("[CART] Invalid quantity")
                return Result.Error(AppError.Validation(
                    message = "تعداد باید بیشتر از صفر باشد",
                    field = "quantity"
                ))
            }
            
            val request = AddToCartRequestDto(productId, quantity)
            val response = apiService.addToCart(request)
            
            if (response.isSuccessful) {
                if (response.data != null) {
                    val cartItem = response.data.toCartItem()
                    Timber.d("[CART] Added to cart successfully")
                    Result.Success(cartItem)
                } else {
                    Timber.w("[CART] Add to cart response is empty")
                    Result.Error(AppError.Network(
                        message = "پاسخ سرور نامعتبر",
                        statusCode = 200
                    ))
                }
            } else {
                Timber.w("[CART] Add to cart failed: ${response.code()}")
                Result.Error(when (response.code()) {
                    400 -> AppError.Validation(
                        message = "درخواست نامعتبر",
                        field = "product"
                    )
                    404 -> AppError.Network(
                        message = "محصول یافت نشد",
                        statusCode = 404
                    )
                    else -> AppError.Network(
                        message = response.message ?: "اضافه به سبد ناموفق",
                        statusCode = response.code()
                    )
                })
            }
        } catch (e: Exception) {
            Timber.e(e, "[CART] Add to cart error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * ❌ Remove item from cart
     */
    override suspend fun removeFromCart(itemId: String): Result<Unit> {
        return try {
            Timber.d("[CART] Removing from cart: item=$itemId")
            
            if (itemId.isBlank()) {
                Timber.w("[CART] Invalid item ID")
                return Result.Error(AppError.Validation(
                    message = "شناسه مورد نامعتبر",
                    field = "itemId"
                ))
            }
            
            val response = apiService.removeFromCart(itemId)
            
            if (response.isSuccessful) {
                Timber.d("[CART] Item removed successfully")
                Result.Success(Unit)
            } else {
                Timber.w("[CART] Remove failed: ${response.code()}")
                Result.Error(AppError.Network(
                    message = response.message ?: "حذف از سبد ناموفق",
                    statusCode = response.code()
                ))
            }
        } catch (e: Exception) {
            Timber.e(e, "[CART] Remove from cart error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 📊 Get cart summary
     */
    override fun getCartSummary(): Flow<Result<CartSummary>> = getCart { cart ->
        CartSummary(
            itemCount = cart.items.size,
            totalPrice = cart.totalPrice,
            discountAmount = cart.discountAmount,
        )
    }

    /**
     * ✅ Validate cart contents
     */
    override suspend fun validateCart(): Result<Boolean> {
        return try {
            Timber.d("[CART] Validating cart")
            
            val response = apiService.validateCart()
            
            if (response.isSuccessful) {
                if (response.data != null) {
                    Timber.d("[CART] Cart valid: ${response.data.isValid}")
                    Result.Success(response.data.isValid)
                } else {
                    Timber.w("[CART] Validation response empty")
                    Result.Error(AppError.Network(
                        message = "پاسخ سرور نامعتبر",
                        statusCode = 200
                    ))
                }
            } else {
                Timber.w("[CART] Validation failed: ${response.code()}")
                Result.Error(AppError.Network(
                    message = response.message ?: "بررسی سبد ناموفق",
                    statusCode = response.code()
                ))
            }
        } catch (e: Exception) {
            Timber.e(e, "[CART] Validation error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🎈 Apply discount code to cart
     */
    override suspend fun applyDiscountCode(code: String): Result<Cart> {
        return try {
            Timber.d("[CART] Applying discount: $code")
            
            if (code.isBlank()) {
                Timber.w("[CART] Invalid coupon code")
                return Result.Error(AppError.Validation(
                    message = "کد تخفیف نمی‌تواند خالی باشد",
                    field = "code"
                ))
            }
            
            // TODO: Implement when API is ready
            Result.Error(AppError.Unknown(
                message = "امام‌سازی این بهره برنامه انجام نشده"
            ))
        } catch (e: Exception) {
            Timber.e(e, "[CART] Discount application error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * ❌ Remove discount code from cart
     */
    override suspend fun removeDiscountCode(): Result<Unit> {
        return try {
            Timber.d("[CART] Removing discount code")
            
            // TODO: Implement when API is ready
            Result.Error(AppError.Unknown(
                message = "امام‌سازی این بهره برنامه انجام نشده"
            ))
        } catch (e: Exception) {
            Timber.e(e, "[CART] Remove discount error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🔄 Helper to map cart and transform
     */
    private inline fun <T> getCart(mapper: (Cart) -> T): Flow<Result<T>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getCart()
            
            if (response.isSuccessful) {
                if (response.data != null) {
                    val cart = response.data.toCart()
                    emit(Result.Success(mapper(cart)))
                } else {
                    emit(Result.Error(AppError.Network(
                        message = "پاسخ سرور نامعتبر",
                        statusCode = 200
                    )))
                }
            } else {
                emit(Result.Error(AppError.Network(
                    message = response.message ?: "خطا",
                    statusCode = response.code()
                )))
            }
        } catch (e: Exception) {
            emit(Result.Error(exceptionHandler.handleException(e)))
        }
    }

    // ============================================
    // 🔄 Mapper Functions
    // ============================================

    private fun com.noghre.sod.data.remote.dto.CartDto.toCart(): Cart {
        return Cart(
            items = items.map { it.toCartItem() },
            totalPrice = total,
            discountAmount = discount,
        )
    }

    private fun CartItemDto.toCartItem(): CartItem {
        return CartItem(
            id = id,
            productId = productId,
            quantity = quantity,
            price = price,
            totalPrice = totalPrice,
        )
    }
}