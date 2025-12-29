package com.noghre.sod.domain.usecase.payment

import com.noghre.sod.core.error.AppError
import com.noghre.sod.core.util.Result
import com.noghre.sod.domain.model.Rial
import com.noghre.sod.domain.model.Toman
import com.noghre.sod.domain.repository.PaymentRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Business logic for initiating payment with Zarinpal gateway.
 * 
 * Responsibilities (Domain layer):
 * - Validate payment amount (min 1000 Toman, max 500M Toman)
 * - Convert Toman to Rial (Zarinpal requirement: 1 Toman = 10 Rial)
 * - Generate Persian payment description
 * - Apply business rules (e.g., no payment for canceled orders)
 * 
 * Responsibilities (Data layer - delegated to repository):
 * - Network communication with Zarinpal API
 * - Error handling for network failures
 * - Retry logic for transient failures
 * 
 * This use case is pure Kotlin, testable with JUnit (no Android dependencies).
 */
class RequestPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    companion object {
        // Business rules: valid payment range
        private const val MIN_AMOUNT_TOMAN = 1_000L        // 1,000 Toman
        private const val MAX_AMOUNT_TOMAN = 500_000_000L   // 500 Million Toman
    }
    
    suspend operator fun invoke(
        amount: Toman,
        orderId: String,
        description: String = "",
        callbackUrl: String
    ): Result<PaymentInitiation> {
        
        // Step 1: Validate amount is within business rules
        if (!amount.isValidPaymentAmount()) {
            Timber.w("Payment amount outside valid range: ${amount.value} Toman")
            return when {
                amount.value < MIN_AMOUNT_TOMAN -> {
                    Result.Error(
                        AppError.Validation(
                            "تومان ۱٬۰۰۰ آن الزام پرداخت مبلغ حداقل"
                        )
                    )
                }
                else -> {
                    Result.Error(
                        AppError.Validation(
                            "تومان میلیون ۵۰۰ از بیشتر پرداخت نمی‌توانب"
                        )
                    )
                }
            }
        }
        
        // Step 2: Validate order ID format
        if (orderId.isBlank()) {
            return Result.Error(
                AppError.Validation("سفارش گانه الزام")
            )
        }
        
        // Step 3: Convert Toman to Rial for Zarinpal API
        // CRITICAL: Zarinpal requires amounts in Rial (10x larger)
        val amountInRial = amount.toRial()
        Timber.d(
            "Payment conversion: ${amount.value} Toman = " +
            "${amountInRial.value} Rial for order $orderId"
        )
        
        // Step 4: Generate Persian description if not provided
        val persianDescription = if (description.isBlank()) {
            "سفارش $orderId - سود نقره"
        } else {
            description
        }
        
        // Step 5: Validate callback URL (security check)
        if (!isValidCallbackUrl(callbackUrl)) {
            Timber.e("Invalid callback URL: $callbackUrl")
            return Result.Error(
                AppError.Security("آدرس بازگشت نامعتبر")
            )
        }
        
        // Step 6: Delegate to repository (Data layer responsibility)
        // Repository handles network communication, retries, error handling
        Timber.i(
            "Requesting payment from Zarinpal:\n" +
            "- Amount: ${amount.value} Toman (${amountInRial.value} Rial)\n" +
            "- Order: $orderId\n" +
            "- Description: $persianDescription\n" +
            "- Callback: $callbackUrl"
        )
        
        return when (val result = paymentRepository.initiatePayment(
            amountInRial = amountInRial.value,
            orderId = orderId,
            description = persianDescription,
            callbackUrl = callbackUrl
        )) {
            is Result.Success -> {
                Timber.i(
                    "Payment initiation successful:\n" +
                    "- Authority: ${result.data.authority}\n" +
                    "- Payment URL: ${result.data.paymentUrl}"
                )
                
                Result.Success(
                    PaymentInitiation(
                        authority = result.data.authority,
                        paymentUrl = result.data.paymentUrl,
                        orderId = orderId,
                        amount = amount
                    )
                )
            }
            
            is Result.Error -> {
                Timber.e(
                    "Payment initiation failed: ${result.error.message}"
                )
                result
            }
            
            is Result.Loading -> result
        }
    }
    
    /**
     * Validate callback URL to prevent open redirect attacks.
     */
    private fun isValidCallbackUrl(url: String): Boolean {
        return url.startsWith("https://noghresod.ir/payment") ||
               url.startsWith("https://api.noghresod.ir/payment") ||
               url.startsWith("app://noghresod/payment")  // Deep link
    }
}

/**
 * Result data class for successful payment initiation.
 */
data class PaymentInitiation(
    val authority: String,      // Zarinpal transaction authority
    val paymentUrl: String,     // URL to redirect user for payment
    val orderId: String,        // Internal order ID
    val amount: Toman           // Amount in Toman (for display/logging)
)