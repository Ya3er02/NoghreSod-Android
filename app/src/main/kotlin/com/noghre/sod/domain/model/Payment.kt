package com.noghre.sod.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Payment method enum.
 */
enum class PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    APPLE_PAY,
    GOOGLE_PAY,
    BANK_TRANSFER,
    DIGITAL_WALLET,
    CASH_ON_DELIVERY
}

/**
 * Payment status enum.
 */
enum class PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED,
    REFUNDED,
    PARTIAL_REFUND
}

/**
 * Domain model representing payment details.
 * 
 * @property id Unique payment identifier
 * @property orderId Reference to order
 * @property amount Payment amount
 * @property currency Currency code (USD, EUR, etc.)
 * @property method Payment method used
 * @property status Current payment status
 * @property transactionId Transaction reference ID from payment gateway
 * @property description Payment description
 * @property createdAt Payment creation date
 * @property completedAt Payment completion date
 * @property failureReason Reason for payment failure if applicable
 * 
 * @since 1.0.0
 */
data class Payment(
    val id: String,
    val orderId: String,
    val amount: BigDecimal,
    val currency: String = "USD",
    val method: PaymentMethod,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val transactionId: String? = null,
    val description: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null,
    val failureReason: String? = null
) {
    
    /**
     * Check if payment is successful.
     */
    fun isSuccessful(): Boolean = status == PaymentStatus.COMPLETED
    
    /**
     * Check if payment failed.
     */
    fun hasFailed(): Boolean = status == PaymentStatus.FAILED
    
    /**
     * Check if payment can be refunded.
     */
    fun canBeRefunded(): Boolean {
        return status in listOf(PaymentStatus.COMPLETED, PaymentStatus.PROCESSING)
    }
    
    /**
     * Get payment duration in seconds.
     */
    fun getProcessingTime(): Long? {
        return if (completedAt != null) {
            java.time.temporal.ChronoUnit.SECONDS.between(createdAt, completedAt)
        } else {
            null
        }
    }
}

/**
 * Domain model for card-based payments.
 * (For storing tokenized card info, never store full card details)
 * 
 * @property id Unique card token ID
 * @property cardHolder Name on card
 * @property lastFourDigits Last 4 digits of card
 * @property expiryMonth Expiry month (MM)
 * @property expiryYear Expiry year (YYYY)
 * @property cardType Visa, Mastercard, Amex, etc.
 * @property isDefault Set as default payment method
 * @property createdAt Card token creation date
 * 
 * @since 1.0.0
 */
data class CardPayment(
    val id: String,
    val cardHolder: String,
    val lastFourDigits: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val cardType: String,
    val isDefault: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    
    /**
     * Check if card is expired.
     */
    fun isExpired(): Boolean {
        val now = LocalDateTime.now()
        val cardExpiry = LocalDateTime.of(expiryYear, expiryMonth, 1, 0, 0)
        return now.isAfter(cardExpiry.plusMonths(1))
    }
    
    /**
     * Get display name (last 4 digits).
     */
    fun getDisplayName(): String = "${cardType.uppercase()} **** $lastFourDigits"
}

/**
 * Domain model for refund requests.
 * 
 * @property id Unique refund identifier
 * @property paymentId Reference to original payment
 * @property orderId Reference to order
 * @property amount Refund amount
 * @property reason Reason for refund
 * @property status Current refund status
 * @property createdAt Refund request creation date
 * @property processedAt Refund processing date
 * 
 * @since 1.0.0
 */
data class Refund(
    val id: String,
    val paymentId: String,
    val orderId: String,
    val amount: BigDecimal,
    val reason: String,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val processedAt: LocalDateTime? = null
) {
    
    /**
     * Check if refund is processed.
     */
    fun isProcessed(): Boolean = status == PaymentStatus.COMPLETED
}
