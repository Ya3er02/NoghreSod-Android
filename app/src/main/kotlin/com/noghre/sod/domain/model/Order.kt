package com.noghre.sod.domain.model

data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val shippingAddress: Address,
    val paymentMethod: String,
    val subtotal: Double,
    val discount: Double,
    val shipping: Double,
    val tax: Double,
    val total: Double,
    val status: OrderStatus,
    val coupon: Coupon? = null,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

data class OrderItem(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String
)

data class Address(
    val id: String,
    val title: String,
    val fullName: String,
    val phone: String,
    val province: String,
    val city: String,
    val street: String,
    val postalCode: String,
    val isDefault: Boolean = false
)
