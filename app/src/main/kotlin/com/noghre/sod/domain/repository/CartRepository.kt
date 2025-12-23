package com.noghre.sod.domain.repository

import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.usecase.cart.CouponResult
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun addToCart(productId: String, quantity: Int)
    suspend fun updateCartItem(productId: String, quantity: Int)
    suspend fun removeFromCart(productId: String)
    suspend fun clearCart()
    fun getCart(): Flow<List<CartItem>>
    fun getCartTotal(): Flow<Double>
    suspend fun applyCoupon(couponCode: String): CouponResult
    suspend fun removeCoupon()
    suspend fun syncCart()
}
