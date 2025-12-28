package com.noghre.sod.data.payment

import com.noghre.sod.core.util.Result
import com.noghre.sod.domain.model.PaymentRequest
import com.noghre.sod.domain.model.PaymentResponse
import com.noghre.sod.domain.model.PaymentVerification

/**
 * Contract for payment gateway implementations
 * 
 * Each payment gateway (Zarinpal, IDPay, NextPay, etc.) must implement this interface
 * to provide consistent payment request and verification operations.
 * 
 * Implementations handle:
 * - API communication with the gateway
 * - Request/response mapping
 * - Gateway-specific error handling
 * - Transaction logging
 * 
 * Error Handling:
 * - All exceptions caught and mapped to Result.Error
 * - Gateway-specific errors mapped to AppError types
 * - No exception should propagate to caller
 */
interface PaymentService {
    
    /**
     * Request payment from this gateway
     * 
     * Process:
     * 1. Send payment request to gateway API
     * 2. Receive authority/transaction ID
     * 3. Get payment URL for user redirect
     * 4. Return PaymentResponse with redirect URL
     * 
     * @param request Payment request details (order ID, amount, description, etc.)
     * @return Result<PaymentResponse> containing authority and payment URL
     * 
     * Error Cases:
     * - Network error → Result.Error(AppError.Network(...))
     * - Invalid credentials → Result.Error(AppError.Authentication(...))
     * - Invalid amount → Result.Error(AppError.Validation(...))
     * - Gateway error → Result.Error(AppError.Payment(...))
     * - Unknown error → Result.Error(AppError.Unknown(...))
     */
    suspend fun requestPayment(request: PaymentRequest): Result<PaymentResponse>
    
    /**
     * Verify payment after user returns from gateway
     * 
     * Process:
     * 1. Receive authority code from callback/user return
     * 2. Send verification request to gateway API
     * 3. Gateway confirms transaction success and returns details
     * 4. Return PaymentVerification with reference ID and card info
     * 
     * @param authority Transaction identifier from gateway callback
     * @param amount Expected payment amount for verification
     * @return Result<PaymentVerification> with transaction details and status
     * 
     * Error Cases:
     * - Payment not found → Result.Error(AppError.NotFound(...))
     * - Payment amount mismatch → Result.Error(AppError.Validation(...))
     * - Payment failed at gateway → Result.Error(AppError.Payment(...))
     * - Network error → Result.Error(AppError.Network(...))
     * - Unknown error → Result.Error(AppError.Unknown(...))
     * 
     * Note: Verification is NOT idempotent at service level.
     * Idempotency is handled at repository level via PaymentVerificationCache.
     */
    suspend fun verifyPayment(
        authority: String,
        amount: Long
    ): Result<PaymentVerification>
}
