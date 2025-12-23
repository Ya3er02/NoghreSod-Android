package com.noghre.sod.presentation.cart

import com.noghre.sod.domain.model.Product

/**
 * UI state for shopping cart screen
 */
data class CartUiState(
    val items: List<CartItemUi> = emptyList(),
    val totalPrice: Double = 0.0,
    val discountAmount: Double = 0.0,
    val finalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isClearing: Boolean = false
)

/**
 * UI representation of cart item
 */
data class CartItemUi(
    val id: String,
    val product: Product,
    val quantity: Int,
    val subtotal: Double,
    val isUpdating: Boolean = false
)
