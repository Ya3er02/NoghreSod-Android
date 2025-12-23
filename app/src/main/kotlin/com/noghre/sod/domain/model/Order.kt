package com.noghre.sod.domain.model

import java.util.Date

data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val address: Address,
    val status: OrderStatus,
    val totalPrice: Double,
    val discountAmount: Double = 0.0,
    val shippingCost: Double = 0.0,
    val finalPrice: Double = totalPrice - discountAmount + shippingCost,
    val paymentMethod: PaymentMethod,
    val notes: String? = null,
    val referenceId: String? = null,
    val createdAt: Date,
    val updatedAt: Date,
    val estimatedDelivery: Date? = null
)

data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double = price * quantity
)
