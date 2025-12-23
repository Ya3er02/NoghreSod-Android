package com.noghre.sod.data.datasource.local

import com.noghre.sod.data.local.dao.CartDao
import com.noghre.sod.data.local.entity.CartEntity
import com.noghre.sod.data.local.entity.CartItemEntity
import com.noghre.sod.data.local.entity.SavedCartEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalCartDataSource @Inject constructor(
    private val cartDao: CartDao,
) {

    fun getCart(): Flow<CartEntity?> {
        return cartDao.getCart()
    }

    suspend fun insertCart(cart: CartEntity) {
        cartDao.insertCart(cart)
    }

    suspend fun updateCart(cart: CartEntity) {
        cartDao.updateCart(cart)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    suspend fun insertCartItem(item: CartItemEntity) {
        cartDao.insertCartItem(item)
    }

    suspend fun insertCartItems(items: List<CartItemEntity>) {
        cartDao.insertCartItems(items)
    }

    fun getCartItems(): Flow<List<CartItemEntity>> {
        return cartDao.getCartItems()
    }

    fun getCartItem(itemId: String): Flow<CartItemEntity?> {
        return cartDao.getCartItem(itemId)
    }

    fun getCartItemCount(): Flow<Int> {
        return cartDao.getCartItemCount()
    }

    suspend fun updateCartItem(item: CartItemEntity) {
        cartDao.updateCartItem(item)
    }

    suspend fun removeCartItem(itemId: String) {
        cartDao.removeCartItem(itemId)
    }

    suspend fun deleteAllCartItems() {
        cartDao.deleteAllCartItems()
    }

    suspend fun saveCart(savedCart: SavedCartEntity) {
        cartDao.saveCart(savedCart)
    }

    fun getSavedCarts(): Flow<List<SavedCartEntity>> {
        return cartDao.getSavedCarts()
    }

    fun getSavedCart(id: String): Flow<SavedCartEntity?> {
        return cartDao.getSavedCart(id)
    }

    suspend fun deleteSavedCart(savedCart: SavedCartEntity) {
        cartDao.deleteSavedCart(savedCart)
    }
}
