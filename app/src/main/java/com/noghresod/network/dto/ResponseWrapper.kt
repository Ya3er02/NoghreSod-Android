package com.noghresod.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

// ==================== Response Wrapper ====================

/**
 * Wrapper برای همه responses
 * ✅ HTTP status کنترل کن
 * ✅ Headers بخون
 * ✅ Error handling ساده
 */
@Serializable
data class ApiResponse<T>(
    @SerialName("data")
    val data: T? = null,
    
    @SerialName("error")
    val error: ApiError? = null,
    
    @SerialName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @SerialName("success")
    val success: Boolean = error == null
) {
    fun isSuccessful(): Boolean = success && data != null
    fun getErrorMessage(): String = error?.message ?: "Unknown error"
}

@Serializable
data class ApiError(
    @SerialName("code")
    val code: String,
    
    @SerialName("message")
    val message: String,
    
    @SerialName("details")
    val details: Map<String, String>? = null,
    
    @SerialName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class PaginatedResponse<T>(
    @SerialName("items")
    val items: List<T>,
    
    @SerialName("pagination")
    val pagination: PaginationInfo
)

// ==================== Pagination Info ====================

@Serializable
sealed class PaginationInfo {
    
    @Serializable
    @SerialName("page_based")
    data class PageBased(
        @SerialName("page")
        val page: Int,
        
        @SerialName("pageSize")
        val pageSize: Int,
        
        @SerialName("totalPages")
        val totalPages: Int,
        
        @SerialName("totalItems")
        val totalItems: Long,
        
        @SerialName("hasNextPage")
        val hasNextPage: Boolean,
        
        @SerialName("hasPreviousPage")
        val hasPreviousPage: Boolean
    ) : PaginationInfo()
    
    @Serializable
    @SerialName("cursor_based")
    data class CursorBased(
        @SerialName("nextCursor")
        val nextCursor: String?,
        
        @SerialName("previousCursor")
        val previousCursor: String?,
        
        @SerialName("hasMore")
        val hasMore: Boolean,
        
        @SerialName("pageSize")
        val pageSize: Int
    ) : PaginationInfo()
}

// ==================== Serializers ====================

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = 
        PrimitiveSerialDescriptor("Instant", kotlinx.serialization.descriptors.PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }
    
    override fun deserialize(decoder: Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }
}

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    override val descriptor: SerialDescriptor = 
        PrimitiveSerialDescriptor("LocalDateTime", kotlinx.serialization.descriptors.PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }
    
    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }
}

// ==================== Money Value Class ====================

@Serializable
@JvmInline
value class Money(val amount: Long) {
    
    fun toToman(): Long = amount
    fun toRial(): Long = amount * 10
    
    fun format(locale: Locale = Locale("fa", "IR")): String {
        val formatter = java.text.NumberFormat.getNumberInstance(locale)
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

// ==================== Order Status ====================

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

object OrderStatusSerializer : KSerializer<OrderStatus> {
    override val descriptor: SerialDescriptor = 
        PrimitiveSerialDescriptor("OrderStatus", kotlinx.serialization.descriptors.PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: OrderStatus) {
        encoder.encodeString(value.value)
    }
    
    override fun deserialize(decoder: Decoder): OrderStatus {
        return OrderStatus.fromString(decoder.decodeString())
    }
}

@Serializable(with = PaymentStatusSerializer::class)
sealed class PaymentStatus(val value: String) {
    object Unpaid : PaymentStatus("unpaid")
    object Paid : PaymentStatus("paid")
    object Failed : PaymentStatus("failed")
    object Refunded : PaymentStatus("refunded")
    
    companion object {
        fun fromString(value: String): PaymentStatus {
            return when (value.lowercase()) {
                "unpaid" -> Unpaid
                "paid" -> Paid
                "failed" -> Failed
                "refunded" -> Refunded
                else -> throw IllegalArgumentException("Unknown payment status: $value")
            }
        }
    }
}

object PaymentStatusSerializer : KSerializer<PaymentStatus> {
    override val descriptor: SerialDescriptor = 
        PrimitiveSerialDescriptor("PaymentStatus", kotlinx.serialization.descriptors.PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: PaymentStatus) {
        encoder.encodeString(value.value)
    }
    
    override fun deserialize(decoder: Decoder): PaymentStatus {
        return PaymentStatus.fromString(decoder.decodeString())
    }
}

// ==================== Validation ====================

data class ValidationError(
    val field: String,
    val message: String,
    val code: String
)

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<ValidationError>) : ValidationResult()
}

interface Validator<T> {
    fun validate(data: T): ValidationResult
}

// Email و Phone validators
fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return emailRegex.matches(this)
}

fun String.isValidIranianPhone(): Boolean {
    val phoneRegex = "^(\\+98|0)?9\\d{9}$".toRegex()
    return phoneRegex.matches(this)
}

fun String.hasUpperCase(): Boolean = any { it.isUpperCase() }
fun String.hasDigit(): Boolean = any { it.isDigit() }
fun String.hasSpecialChar(): Boolean = any { !it.isLetterOrDigit() }
