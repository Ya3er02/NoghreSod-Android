package com.noghre.sod.domain.model

data class Cart(
    val id: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val discount: Double = 0.0,
    val shipping: Double = 0.0,
    val tax: Double = 0.0,
    val coupon: Coupon? = null,
    val total: Double
) {
    fun isEmpty() = items.isEmpty()
    fun itemCount() = items.sumOf { it.quantity }
}

data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val stock: Int
) {
    fun subtotal() = price * quantity
}
