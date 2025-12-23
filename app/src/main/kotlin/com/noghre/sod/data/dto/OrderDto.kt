package com.noghre.sod.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Order-related DTOs for API communication
 */

@Serializable
data class CreateOrderRequestDto(
    @SerialName("items")
    val items: List<OrderItemRequestDto>,
    @SerialName("shipping_address")
    val shippingAddress: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("recipient_name")
    val recipientName: String,
    @SerialName("payment_method")
    val paymentMethod: String,
    @SerialName("notes")
    val notes: String? = null
)

@Serializable
data class OrderItemRequestDto(
    @SerialName("product_id")
    val productId: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("weight")
    val weight: Double,
    @SerialName("unit_price")
    val unitPrice: Long,
    @SerialName("labor_cost")
    val laborCost: Long = 0
)

@Serializable
data class OrderResponseDto(
    @SerialName("id")
    val id: String,
    @SerialName("order_number")
    val orderNumber: String,
    @SerialName("status")
    val status: String,
    @SerialName("items")
    val items: List<OrderItemResponseDto>,
    @SerialName("total_weight")
    val totalWeight: Double,
    @SerialName("subtotal")
    val subtotal: Long,
    @SerialName("labor_cost")
    val laborCost: Long,
    @SerialName("tax")
    val tax: Long,
    @SerialName("shipping_cost")
    val shippingCost: Long,
    @SerialName("discount_amount")
    val discountAmount: Long = 0,
    @SerialName("total")
    val total: Long,
    @SerialName("payment_method")
    val paymentMethod: String,
    @SerialName("payment_status")
    val paymentStatus: String,
    @SerialName("tracking_code")
    val trackingCode: String? = null,
    @SerialName("estimated_delivery_date")
    val estimatedDeliveryDate: Long? = null,
    @SerialName("shipping_address")
    val shippingAddress: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("recipient_name")
    val recipientName: String,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long
)

@Serializable
data class OrderItemResponseDto(
    @SerialName("id")
    val id: Long,
    @SerialName("product_id")
    val productId: String,
    @SerialName("product_name")
    val productName: String,
    @SerialName("product_image")
    val productImage: String,
    @SerialName("purity")
    val purity: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("weight")
    val weight: Double,
    @SerialName("unit_price")
    val unitPrice: Long,
    @SerialName("labor_cost")
    val laborCost: Long = 0,
    @SerialName("discount")
    val discount: Int = 0
)

@Serializable
data class OrderListResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: List<OrderResponseDto> = emptyList(),
    @SerialName("pagination")
    val pagination: PaginationDto? = null,
    @SerialName("message")
    val message: String? = null
)

@Serializable
data class OrderTrackingDto(
    @SerialName("order_id")
    val orderId: String,
    @SerialName("order_number")
    val orderNumber: String,
    @SerialName("status")
    val status: String,
    @SerialName("tracking_code")
    val trackingCode: String? = null,
    @SerialName("estimated_delivery_date")
    val estimatedDeliveryDate: Long? = null,
    @SerialName("actual_delivery_date")
    val actualDeliveryDate: Long? = null,
    @SerialName("status_history")
    val statusHistory: List<OrderStatusDto>
)

@Serializable
data class OrderStatusDto(
    @SerialName("status")
    val status: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("timestamp")
    val timestamp: Long
)
