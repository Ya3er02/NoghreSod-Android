package com.noghre.sod.data.dto

from kotlinx.serialization.Serializable
import com.noghre.sod.data.model.Order
import com.noghre.sod.data.model.OrderItem
import com.noghre.sod.data.model.OrderStatus

@Serializable
data class OrderDto(
    val id: String,
    val userId: String,
    val items: List<OrderItemDto>,
    val status: String = "PENDING",
    val totalAmount: Double,
    val shippingAddress: String,
    val trackingNumber: String? = null,
    val estimatedDelivery: Long? = null
)

@Serializable
data class OrderItemDto(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double
)

fun OrderDto.toEntity(): Order {
    return Order(
        id = id,
        userId = userId,
        items = items.map {
            OrderItem(
                productId = it.productId,
                productName = it.productName,
                quantity = it.quantity,
                price = it.price
            )
        },
        status = OrderStatus.valueOf(status),
        totalAmount = totalAmount,
        shippingAddress = shippingAddress,
        trackingNumber = trackingNumber,
        estimatedDelivery = estimatedDelivery
    )
}
