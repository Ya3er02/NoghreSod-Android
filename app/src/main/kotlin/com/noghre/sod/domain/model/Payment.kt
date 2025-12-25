package com.noghre.sod.domain.model

/**
 * Payment method enumeration.
 */
enum class PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    BANK_TRANSFER,
    CRYPTO_WALLET,
    DIGITAL_WALLET,
    CASH_ON_DELIVERY
}

/**
 * Domain model for Payment.
 * Represents a payment transaction.
 *
 * @property id Payment identifier
 * @property orderId Associated order ID
 * @property userId User who made payment
 * @property method Payment method used
 * @property amount Amount paid
 * @property currency Currency code
 * @property status Payment status
 * @property transactionId Transaction ID from payment gateway
 * @property transactionRef Reference from payment provider
 * @property cardLast4 Last 4 digits of card (if applicable)
 * @property createdAt Payment timestamp
 */
data class Payment(
    val id: String,
    val orderId: String,
    val userId: String,
    val method: PaymentMethod,
    val amount: Double,
    val currency: String = "USD",
    val status: PaymentStatus = PaymentStatus.PENDING,
    val transactionId: String? = null,
    val transactionRef: String? = null,
    val cardLast4: String? = null,
    val createdAt: String = ""
)

/**
 * Payment status enumeration.
 */
enum class PaymentStatus {
    PENDING,        // Awaiting payment
    PROCESSING,     // Being processed
    COMPLETED,      // Successfully completed
    FAILED,         // Payment failed
    CANCELLED,      // Payment cancelled
    REFUNDED        // Refund processed
}
