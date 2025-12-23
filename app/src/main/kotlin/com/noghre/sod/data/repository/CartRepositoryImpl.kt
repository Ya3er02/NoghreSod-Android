package com.noghre.sod.data.repository

import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.domain.usecase.cart.CouponResult
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@ViewModelScoped
class CartRepositoryImpl @Inject constructor() : CartRepository {
    override suspend fun addToCart(productId: String, quantity: Int) {
        // TODO: Implement
    }

    override suspend fun updateCartItem(productId: String, quantity: Int) {
        // TODO: Implement
    }

    override suspend fun removeFromCart(productId: String) {
        // TODO: Implement
    }

    override suspend fun clearCart() {
        // TODO: Implement
    }

    override fun getCart(): Flow<List<CartItem>> = flowOf(emptyList())

    override fun getCartTotal(): Flow<Double> = flowOf(0.0)

    override suspend fun applyCoupon(couponCode: String): CouponResult {
        return CouponResult(false, message = "تخفیف نامه نامعتبر")
    }

    override suspend fun removeCoupon() {
        // TODO: Implement
    }

    override suspend fun syncCart() {
        // TODO: Implement
    }
}
