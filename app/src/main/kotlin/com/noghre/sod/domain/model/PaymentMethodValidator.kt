package com.noghre.sod.domain.model

import java.math.BigDecimal

/**
 * Payment method definition with validation, fees, and constraints.
 * Validates payment method availability, calculates processing fees,
 * and enforces amount limits.
 * 
 * @author Yaser
 * @version 1.0.0
 */
enum class PaymentMethodValidated(
    val displayName: String,
    val displayNameFa: String,
    val processingFeePercent: Double = 0.0,
    val minimumAmount: BigDecimal = BigDecimal.ZERO,
    val maximumAmount: BigDecimal = BigDecimal("999999999"),
    val isAvailableForUsers: Boolean = true,
    val requiresVerification: Boolean = false,
    val estimatedDaysForCompletion: Int = 0
) {
    ONLINE_GATEWAY(
        displayName = "Online Payment Gateway",
        displayNameFa = "درگاه پرداخت آنلاین",
        processingFeePercent = 2.5,
        minimumAmount = BigDecimal("1000"),
        maximumAmount = BigDecimal("50000000"),
        isAvailableForUsers = true,
        requiresVerification = true,
        estimatedDaysForCompletion = 0
    ),
    BANK_TRANSFER(
        displayName = "Bank Transfer",
        displayNameFa = "انتقال بانکی",
        processingFeePercent = 0.0,
        minimumAmount = BigDecimal("50000"),
        maximumAmount = BigDecimal("999999999"),
        isAvailableForUsers = true,
        requiresVerification = true,
        estimatedDaysForCompletion = 2
    ),
    CASH_ON_DELIVERY(
        displayName = "Cash on Delivery",
        displayNameFa = "پرداخت در محل",
        processingFeePercent = 3.0,
        minimumAmount = BigDecimal.ZERO,
        maximumAmount = BigDecimal("500000"),
        isAvailableForUsers = true,
        requiresVerification = false,
        estimatedDaysForCompletion = 3
    ),
    WALLET(
        displayName = "Wallet",
        displayNameFa = "کیف پول",
        processingFeePercent = 0.0,
        minimumAmount = BigDecimal("100"),
        maximumAmount = BigDecimal("10000000"),
        isAvailableForUsers = true,
        requiresVerification = false,
        estimatedDaysForCompletion = 0
    ),
    INSTALLMENT(
        displayName = "Installment Payment",
        displayNameFa = "پرداخت قسطی",
        processingFeePercent = 5.0,
        minimumAmount = BigDecimal("5000"),
        maximumAmount = BigDecimal("200000000"),
        isAvailableForUsers = true,
        requiresVerification = true,
        estimatedDaysForCompletion = 1
    );
    
    /**
     * Calculate processing fee for given amount.
     */
    fun calculateFee(amount: BigDecimal): BigDecimal {
        val feePercent = processingFeePercent / 100.0
        return amount.multiply(BigDecimal(feePercent))
    }
    
    /**
     * Calculate total amount including fee.
     */
    fun calculateTotalAmount(amount: BigDecimal): BigDecimal {
        return amount.plus(calculateFee(amount))
    }
    
    /**
     * Check if payment method is available for given amount.
     */
    fun isAvailableForAmount(amount: BigDecimal): Boolean {
        return amount >= minimumAmount && amount <= maximumAmount && isAvailableForUsers
    }
    
    /**
     * Get user-friendly error message if payment method is not available.
     */
    fun getAvailabilityError(amount: BigDecimal): String? {
        return when {
            amount < minimumAmount -> "حداقل مبلغ: ${minimumAmount}"
            amount > maximumAmount -> "حداکثر مبلغ: ${maximumAmount}"
            !isAvailableForUsers -> "این روش پرداخت در حال حاضر در دسترس نیست"
            else -> null
        }
    }
    
    /**
     * Check if user verification is required.
     */
    fun requiresUserVerification(): Boolean = requiresVerification
    
    /**
     * Get estimated delivery time.
     */
    fun getDeliveryEstimate(): String {
        return when {
            estimatedDaysForCompletion == 0 -> "فوری"
            estimatedDaysForCompletion == 1 -> "1 روز کاری"
            else -> "${estimatedDaysForCompletion} روز کاری"
        }
    }
}

/**
 * Payment method validator with business rules.
 */
object PaymentMethodValidator {
    
    /**
     * Get available payment methods for user and amount.
     */
    fun getAvailableMethods(
        totalAmount: BigDecimal,
        userVerified: Boolean = false,
        userLocation: String? = null
    ): List<PaymentMethodValidated> {
        return PaymentMethodValidated.values().filter { method ->
            // Amount must be within limits
            method.isAvailableForAmount(totalAmount) &&
            // User must be verified if required
            (!method.requiresVerification || userVerified) &&
            // Check location-specific restrictions
            isAvailableInLocation(method, userLocation)
        }
    }
    
    /**
     * Validate payment method for specific transaction.
     */
    fun validatePaymentMethod(
        method: PaymentMethodValidated,
        amount: BigDecimal,
        userVerified: Boolean = false
    ): PaymentValidationResult {
        return when {
            amount < method.minimumAmount -> PaymentValidationResult.MinimumAmountNotMet
            amount > method.maximumAmount -> PaymentValidationResult.MaximumAmountExceeded
            method.requiresVerification && !userVerified -> PaymentValidationResult.VerificationRequired
            !method.isAvailableForUsers -> PaymentValidationResult.MethodUnavailable
            else -> PaymentValidationResult.Valid(method.calculateFee(amount))
        }
    }
    
    /**
     * Check if payment method is available in user's location.
     */
    private fun isAvailableInLocation(
        method: PaymentMethodValidated,
        location: String?
    ): Boolean {
        // Add location-specific logic here
        // For example, some payment methods might not be available in certain provinces
        return when (method) {
            PaymentMethodValidated.CASH_ON_DELIVERY -> {
                // COD available everywhere
                true
            }
            else -> true
        }
    }
    
    /**
     * Get recommended payment method for amount.
     */
    fun getRecommendedMethod(amount: BigDecimal): PaymentMethodValidated? {
        return when {
            amount <= BigDecimal("500000") -> PaymentMethodValidated.CASH_ON_DELIVERY
            amount <= BigDecimal("50000000") -> PaymentMethodValidated.ONLINE_GATEWAY
            else -> PaymentMethodValidated.BANK_TRANSFER
        }
    }
}

/**
 * Result of payment method validation.
 */
sealed class PaymentValidationResult {
    data class Valid(val processingFee: BigDecimal) : PaymentValidationResult()
    object MinimumAmountNotMet : PaymentValidationResult()
    object MaximumAmountExceeded : PaymentValidationResult()
    object VerificationRequired : PaymentValidationResult()
    object MethodUnavailable : PaymentValidationResult()
    
    fun isValid(): Boolean = this is Valid
    
    fun getErrorMessage(): String? = when (this) {
        is Valid -> null
        MinimumAmountNotMet -> "مبلغ کمتر از حداقل مجاز است"
        MaximumAmountExceeded -> "مبلغ بیشتر از حداکثر مجاز است"
        VerificationRequired -> "لطفا ابتدا حساب خود را تایید کنید"
        MethodUnavailable -> "این روش پرداخت در حال حاضر در دسترس نیست"
    }
}
