package com.noghre.sod.domain.usecase.payment

import com.noghre.sod.core.error.AppError
import com.noghre.sod.core.util.Result
import com.noghre.sod.domain.model.PaymentGateway
import com.noghre.sod.domain.model.PaymentVerification
import com.noghre.sod.domain.repository.PaymentRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for verifying payment after user returns from gateway
 * 
 * Responsibility: Orchestrate payment verification
 * - Validate authority and amount from callback
 * - Call PaymentRepository.verifyPayment (handles idempotency)
 * - Extract order ID and update order status
 * - Prevent duplicate fulfillment through caching
 * 
 * Integration Points:
 * - Called by payment callback handler
 * - Result used to trigger order confirmation
 * - Failure triggers payment retry flow
 */
class VerifyPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    
    /**
     * Execute payment verification
     * 
     * @param authority Payment gateway authority code (unique per transaction)
     * @param amount Payment amount in Tomans (must be > 0)
     * @param gateway Gateway that processed payment
     * @return Result<PaymentVerification> with transaction details
     */
    suspend operator fun invoke(
        authority: String,
        amount: Long,
        gateway: PaymentGateway
    ): Result<PaymentVerification> {
        // Validate authority
        if (authority.isBlank()) {
            Timber.w("VerifyPaymentUseCase: Invalid authority")
            return Result.Error(
                AppError.Validation(
                    message = "کد رهگیری پرداخت معتبر نیست",
                    field = "authority"
                )
            )
        }
        
        // Validate amount
        if (amount <= 0) {
            Timber.w("VerifyPaymentUseCase: Invalid amount: $amount")
            return Result.Error(
                AppError.Validation(
                    message = "مبلغ باید بیشتر از صفر باشد",
                    field = "amount"
                )
            )
        }
        
        // Log use case execution
        Timber.d(
            "VerifyPaymentUseCase: Verifying payment - " +
            "authority=$authority, amount=$amount, gateway=$gateway"
        )
        
        // Delegate to repository (which handles caching/idempotency)
        return try {
            paymentRepository.verifyPayment(
                authority = authority,
                amount = amount,
                gateway = gateway
            )
        } catch (e: Exception) {
            Timber.e(e, "VerifyPaymentUseCase: Exception during verification")
            Result.Error(AppError.Unknown(
                message = "خطا در تالید پرداخت",
                throwable = e
            ))
        }
    }
}
