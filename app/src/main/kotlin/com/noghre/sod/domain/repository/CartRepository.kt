package com.noghre.sod.domain.repository

import com.noghre.sod.domain.model.Cart
import com.noghre.sod.domain.model.Coupon
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCart(): Flow<Cart>

    suspend fun addToCart(productId: String, quantity: Int)

    suspend fun removeFromCart(productId: String)

    suspend fun updateQuantity(productId: String, quantity: Int)

    suspend fun clearCart()

    suspend fun applyCoupon(couponCode: String): Coupon

    suspend fun removeCoupon()

    suspend fun syncCart()
}
