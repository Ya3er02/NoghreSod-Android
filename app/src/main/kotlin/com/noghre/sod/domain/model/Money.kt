package com.noghre.sod.domain.model

import com.noghre.sod.core.error.AppError
import com.noghre.sod.core.util.Result

/**
 * Toman (تومان) - Iranian currency unit.
 * Commonly used for displaying prices to users.
 * 1 Toman = 10 Rial
 */
@JvmInline
value class Toman(val value: Long) {
    init {
        require(value >= 0) { "Amount must be non-negative, got: $value" }
    }
    
    /**
     * Convert Toman to Rial (multiplication by 10).
     * Used for Zarinpal payment gateway API calls.
     * 
     * @return Rial value with precision preserved
     */
    fun toRial(): Rial = Rial(value * 10)
    
    /**
     * Add another amount in Toman.
     */
    operator fun plus(other: Toman): Toman = Toman(value + other.value)
    
    /**
     * 🔧 Fix 2.4: Subtraction with proper validation
     * Throws exception instead of silently returning zero on negative result.
     * This prevents silent business logic errors.
     * 
     * @throws IllegalArgumentException if result would be negative
     * @return New Toman value (guaranteed >= 0)
     */
    operator fun minus(other: Toman): Toman {
        val result = value - other.value
        require(result >= 0) {
            "Cannot subtract $other from $this: result would be negative. " +
            "Use subtractOrNull() or subtractSafely() for graceful handling."
        }
        return Toman(result)
    }
    
    /**
     * Safely subtract another amount, returning null if result would be negative.
     * Useful for wallet/balance operations where negative is invalid state.
     * 
     * @return New Toman value if valid, null if result would be negative
     */
    fun subtractOrNull(other: Toman): Toman? {
        val result = value - other.value
        return if (result >= 0) Toman(result) else null
    }
    
    /**
     * Safely subtract with error result handling.
     * Preferred for use in Result-based code patterns.
     * 
     * @return Result.Success with new Toman if valid, Result.Error if insufficient
     */
    fun subtractSafely(other: Toman): Result<Toman> {
        val result = value - other.value
        return if (result >= 0) {
            Result.Success(Toman(result))
        } else {
            Result.Error(
                AppError.Validation(
                    "مبلغ کافی نیست. موجودی: ${this.value} تومان، " +
                    "درخواست: ${other.value} تومان"
                )
            )
        }
    }
    
    /**
     * Multiply by percentage (for discounts).
     * @param percent 0-100 (e.g., 15 for 15%)
     * @return New Toman value
     */
    fun percentageOf(percent: Int): Toman {
        require(percent in 0..100) { "Percentage must be 0-100, got: $percent" }
        return Toman((value * percent) / 100)
    }
    
    /**
     * Check if this amount is within valid payment range.
     */
    fun isValidPaymentAmount(): Boolean {
        return value in 1_000..500_000_000  // 1K - 500M Toman
    }
    
    /**
     * Check if this amount is sufficient (greater than or equal to another).
     */
    fun isSufficientFor(required: Toman): Boolean = value >= required.value
    
    override fun toString(): String = "$value تومان"
}

/**
 * Rial (ریال) - Iranian currency, 10x smaller unit than Toman.
 * Used exclusively for Zarinpal payment gateway.
 * 1 Toman = 10 Rial
 */
@JvmInline
value class Rial(val value: Long) {
    init {
        require(value >= 0) { "Amount must be non-negative, got: $value" }
        require(value % 10 == 0L) { 
            "Rial must be multiple of 10 (represents 10 Rial = 1 Toman), got: $value" 
        }
    }
    
    /**
     * Convert Rial back to Toman (division by 10).
     * Rounds down to preserve integer precision.
     */
    fun toToman(): Toman = Toman(value / 10)
    
    /**
     * Get the raw value for API requests.
     */
    fun getApiValue(): Long = value
    
    override fun toString(): String = "$value ریال"
}

/**
 * Price wrapper combining display amount (Toman) with formatted string.
 * Immutable, type-safe price representation.
 */
data class Price(
    val amount: Toman,
    val displayText: String
) {
    companion object {
        /**
         * Factory function to create Price with validation.
         */
        fun create(tomanAmount: Long): Result<Price> {
            return if (tomanAmount < 0) {
                Result.Error(AppError.Validation("مبلغ نمی‌تواند منفی باشد"))
            } else {
                val toman = Toman(tomanAmount)
                Result.Success(
                    Price(
                        amount = toman,
                        displayText = formatForDisplay(toman)
                    )
                )
            }
        }
        
        private fun formatForDisplay(toman: Toman): String {
            val formatted = formatToman(toman.value)
            return "$formatted تومان"
        }
        
        /**
         * Format Toman value with Persian number formatting and thousands separator.
         * 1234567 → "۱،۲۳۴،۵۶۷"
         */
        private fun formatToman(value: Long): String {
            val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
            val parts = value.toString().reversed().chunked(3).reversed()
            return parts.joinToString("،") { part ->
                part.reversed().map { digit ->
                    persianDigits[digit.toString().toInt()]
                }.joinToString("")
            }
        }
    }
}
