package com.noghre.sod.data.remote.dto.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * Order Status - Type-safe enumeration
 * Using sealed class instead of plain String
 */
@Serializable(with = OrderStatusSerializer::class)
sealed class OrderStatus(val value: String) {
    object Pending : OrderStatus("pending")
    object Confirmed : OrderStatus("confirmed")
    object Processing : OrderStatus("processing")
    object Shipped : OrderStatus("shipped")
    object Delivered : OrderStatus("delivered")
    object Cancelled : OrderStatus("cancelled")
    object Returned : OrderStatus("returned")
    
    companion object {
        fun fromString(value: String): OrderStatus {
            return when (value.lowercase()) {
                "pending" -> Pending
                "confirmed" -> Confirmed
                "processing" -> Processing
                "shipped" -> Shipped
                "delivered" -> Delivered
                "cancelled" -> Cancelled
                "returned" -> Returned
                else -> throw IllegalArgumentException("Unknown order status: $value")
            }
        }
    }
}

/**
 * Payment Status - Type-safe enumeration
 */
@Serializable(with = PaymentStatusSerializer::class)
sealed class PaymentStatus(val value: String) {
    object Unpaid : PaymentStatus("unpaid")
    object Paid : PaymentStatus("paid")
    object Pending : PaymentStatus("pending")
    object Failed : PaymentStatus("failed")
    object Refunded : PaymentStatus("refunded")
    
    companion object {
        fun fromString(value: String): PaymentStatus {
            return when (value.lowercase()) {
                "unpaid" -> Unpaid
                "paid" -> Paid
                "pending" -> Pending
                "failed" -> Failed
                "refunded" -> Refunded
                else -> throw IllegalArgumentException("Unknown payment status: $value")
            }
        }
    }
}

/**
 * Money Value Object
 * Type-safe representation of currency amounts
 */
@Serializable
@JvmInline
value class Money(val amount: Long) {
    
    fun toToman(): Long = amount
    fun toRial(): Long = amount * 10
    
    fun format(): String {
        val formatter = java.text.NumberFormat.getInstance().apply {
            isGroupingUsed = true
            maximumFractionDigits = 0
        }
        return "${formatter.format(amount)} تومان"
    }
    
    operator fun plus(other: Money) = Money(amount + other.amount)
    operator fun minus(other: Money) = Money(amount - other.amount)
    operator fun times(quantity: Int) = Money(amount * quantity)
    
    companion object {
        val ZERO = Money(0)
        fun fromRial(rial: Long) = Money(rial / 10)
        fun fromToman(toman: Long) = Money(toman)
    }
}

/**
 * Order DTO with type-safe fields
 */
@Serializable
data class OrderDto(
    @SerialName("id")
    val id: String,
    
    @SerialName("orderNumber")
    val orderNumber: String,
    
    @SerialName("status")
    val status: OrderStatus,
    
    @SerialName("paymentStatus")
    val paymentStatus: PaymentStatus,
    
    @SerialName("total")
    val total: Money,
    
    @SerialName("subtotal")
    val subtotal: Money,
    
    @SerialName("discount")
    val discount: Money = Money.ZERO,
    
    @SerialName("tax")
    val tax: Money = Money.ZERO,
    
    @SerialName("shippingFee")
    val shippingFee: Money = Money.ZERO,
    
    @Serializable(with = InstantSerializer::class)
    @SerialName("createdAt")
    val createdAt: Instant,
    
    @Serializable(with = InstantSerializer::class)
    @SerialName("estimatedDelivery")
    val estimatedDelivery: Instant?,
    
    @Serializable(with = InstantSerializer::class)
    @SerialName("deliveredAt")
    val deliveredAt: Instant? = null,
    
    @SerialName("items")
    val items: List<OrderItemDto> = emptyList(),
    
    @SerialName("shippingAddress")
    val shippingAddress: AddressDto? = null
)

@Serializable
data class OrderItemDto(
    @SerialName("id")
    val id: String,
    
    @SerialName("productId")
    val productId: String,
    
    @SerialName("productName")
    val productName: String,
    
    @SerialName("quantity")
    val quantity: Int,
    
    @SerialName("unitPrice")
    val unitPrice: Money,
    
    @SerialName("total")
    val total: Money
)

@Serializable
data class AddressDto(
    @SerialName("street")
    val street: String,
    
    @SerialName("city")
    val city: String,
    
    @SerialName("province")
    val province: String,
    
    @SerialName("postalCode")
    val postalCode: String,
    
    @SerialName("country")
    val country: String = "Iran"
)

// ==================== Custom Serializers ====================

object OrderStatusSerializer : KSerializer<OrderStatus> {
    override val descriptor = PrimitiveSerialDescriptor("OrderStatus", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: OrderStatus) {
        encoder.encodeString(value.value)
    }
    
    override fun deserialize(decoder: Decoder): OrderStatus {
        return OrderStatus.fromString(decoder.decodeString())
    }
}

object PaymentStatusSerializer : KSerializer<PaymentStatus> {
    override val descriptor = PrimitiveSerialDescriptor("PaymentStatus", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: PaymentStatus) {
        encoder.encodeString(value.value)
    }
    
    override fun deserialize(decoder: Decoder): PaymentStatus {
        return PaymentStatus.fromString(decoder.decodeString())
    }
}

object InstantSerializer : KSerializer<Instant> {
    override val descriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }
    
    override fun deserialize(decoder: Decoder): Instant {
        return try {
            Instant.parse(decoder.decodeString())
        } catch (e: Exception) {
            // Fallback for custom formats
            val dateString = decoder.decodeString()
            Instant.parse("${dateString}Z")
        }
    }
}
