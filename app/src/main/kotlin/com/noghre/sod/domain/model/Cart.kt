package com.noghre.sod.domain.model

/**
 * Domain model for Cart.
 * Represents the shopping cart with items.
 *
 * @property id Cart identifier
 * @property userId User ID who owns the cart
 * @property items List of cart items
 * @property totalPrice Total price of all items
 * @property itemCount Total number of items
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
data class Cart(
    val id: String,
    val userId: String,
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val itemCount: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    fun calculateTotal(): Double {
        return items.sumOf { it.subtotal }
    }

    fun getTotalItems(): Int {
        return items.sumOf { it.quantity }
    }
}

/**
 * Domain model for Cart Item.
 * Represents a single item in the cart.
 *
 * @property id Cart item identifier
 * @property cartId Cart ID
 * @property product The product in cart
 * @property quantity Quantity of product
 * @property selectedColor Selected color variant
 * @property selectedSize Selected size variant
 * @property subtotal Total price for this item
 * @property addedAt When item was added
 */
data class CartItem(
    val id: String,
    val cartId: String,
    val product: Product,
    val quantity: Int,
    val selectedColor: String? = null,
    val selectedSize: String? = null,
    val subtotal: Double = product.price * quantity,
    val addedAt: String = ""
)
