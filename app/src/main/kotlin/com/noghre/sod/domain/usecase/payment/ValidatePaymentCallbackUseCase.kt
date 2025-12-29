package com.noghre.sod.domain.usecase.payment

import com.noghre.sod.core.error.AppError
import com.noghre.sod.core.util.Result
import com.noghre.sod.domain.model.Rial
import com.noghre.sod.domain.model.Toman
import com.noghre.sod.domain.repository.PaymentRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Validates payment gateway callback to prevent fraud.
 * 
 * Validates:
 * 1. Authority exists in pending transactions
 * 2. Amount hasn't been tampered with
 * 3. Transaction not already processed (replay protection)
 * 4. Server-side verification with Zarinpal API
 * 
 * CRITICAL SECURITY: This use case runs BEFORE marking payment as successful.
 * Never trust client-side parameters. Always verify server-side.
 */
class ValidatePaymentCallbackUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(
        authority: String,
        status: String
    ): Result<PaymentValidation> {
        
        // Step 1: Validate status parameter format
        if (status != "OK" && status != "NOK") {
            Timber.w("Invalid status parameter: $status")
            return Result.Error(
                AppError.Payment("ماند درخواست نامعتبر")
            )
        }
        
        // Step 2: Handle user-cancelled payments
        if (status == "NOK") {
            return Result.Error(
                AppError.Payment("پرداخت توسط کاربر لغو شد")
            )
        }
        
        // Step 3: Fetch pending transaction from local database
        val pending = paymentRepository.getPendingTransaction(authority)
        if (pending == null) {
            Timber.w("Pending transaction not found for authority: $authority")
            Timber.w("홻تر برای بمب عيب هنجام عمل ارتباط به سرور دسترسی.")
            return Result.Error(
                AppError.Payment(ریخت تراكنش منافق یا مفقود")
            )
        }
        
        // Step 4: Detect replay attacks (already-processed transaction)
        if (pending.isVerified) {
            Timber.e(
                "🔴 REPLAY ATTACK DETECTED!\n" +
                "Authority: $authority\n" +
                "This transaction was already verified at ${pending.verifiedAt}\n" +
                "Potential malicious actor attempting to re-process payment."
            )
            return Result.Error(
                AppError.Security("الفعل قبلاً تایيد القد التراكنش هذا")
            )
        }
        
        // Step 5: Verify transaction hasn't expired
        val expiryTime = pending.createdAt + CALLBACK_TIMEOUT_MS
        if (System.currentTimeMillis() > expiryTime) {
            Timber.w("Payment callback received after expiry: $authority")
            return Result.Error(
                AppError.Payment(للتاراكنش زمان انقضاآ انتهى قد الصلاحية مدة")
            )
        }
        
        // Step 6: Server-side verification with Zarinpal
        Timber.d("Verifying payment with Zarinpal: Authority=$authority, Amount=${pending.amount}")
        
        val verificationResult = paymentRepository.verifyPayment(
            authority = authority,
            expectedAmount = pending.amount.value  // In Rial
        )
        
        return when (verificationResult) {
            is Result.Success -> {
                Timber.i("✅ Payment verified successfully: RefId=${verificationResult.data.refId}")
                
                // Mark as verified in local database with ref ID from gateway
                paymentRepository.markAsVerified(
                    authority = authority,
                    refId = verificationResult.data.refId,
                    cardPan = verificationResult.data.cardPan,
                    verifiedAt = System.currentTimeMillis()
                )
                
                Result.Success(
                    PaymentValidation(
                        isValid = true,
                        refId = verificationResult.data.refId,
                        orderId = pending.orderId,
                        amount = pending.amount.toToman()
                    )
                )
            }
            
            is Result.Error -> {
                Timber.e(
                    "❌ Payment verification failed from Zarinpal: " +
                    "${verificationResult.error.message}"
                )
                Result.Error(verificationResult.error)
            }
            
            is Result.Loading -> Result.Loading
        }
    }
    
    companion object {
        // Payment callback must be processed within 15 minutes of initiation
        private const val CALLBACK_TIMEOUT_MS = 15 * 60 * 1000  // 15 minutes
    }
}

/**
 * Result data class for successful payment validation.
 */
data class PaymentValidation(
    val isValid: Boolean,
    val refId: String,        // Zarinpal reference ID
    val orderId: String,      // Internal order ID
    val amount: Toman         // Amount in Toman for display
)