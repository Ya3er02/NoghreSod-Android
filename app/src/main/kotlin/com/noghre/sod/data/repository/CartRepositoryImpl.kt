package com.noghre.sod.data.repository

import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.remote.dto.AddToCartRequestDto
import com.noghre.sod.data.remote.dto.CartItemDto
import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.*
import com.noghre.sod.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : CartRepository {

    override fun getCart(): Flow<Result<Cart>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getCart()
            if (response.success && response.data != null) {
                val cart = response.data.toCart()
                emit(Result.Success(cart))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun addToCart(
        productId: String,
        product: Product,
        quantity: Int,
    ): Result<CartItem> {
        return try {
            val request = AddToCartRequestDto(productId, quantity)
            val response = apiService.addToCart(request)
            if (response.success && response.data != null) {
                Result.Success(response.data.toCartItem())
            } else {
                Result.Error(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeFromCart(itemId: String): Result<Unit> {
        return try {
            val response = apiService.removeFromCart(itemId)
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getCartSummary(): Flow<Result<CartSummary>> {
        return getCart() { cart ->
            CartSummary(
                itemCount = cart.items.size,
                totalPrice = cart.totalPrice,
                discountAmount = cart.discountAmount,
            )
        }
    }

    override suspend fun validateCart(): Result<Boolean> {
        return try {
            val response = apiService.validateCart()
            if (response.success && response.data != null) {
                Result.Success(response.data.isValid)
            } else {
                Result.Error(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun applyDiscountCode(code: String): Result<Cart> {
        return try {
            // Implementation would be similar to other methods
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeDiscountCode(): Result<Unit> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private inline fun <T> getCart(mapper: (Cart) -> T): Flow<Result<T>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getCart()
            if (response.success && response.data != null) {
                val cart = response.data.toCart()
                emit(Result.Success(mapper(cart)))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    // Mapper functions
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
