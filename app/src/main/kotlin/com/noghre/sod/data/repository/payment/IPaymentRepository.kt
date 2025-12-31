package com.noghre.sod.data.repository.payment

import com.noghre.sod.core.result.Result
import com.noghre.sod.domain.model.Payment

/**
 * Repository interface for payment operations.
 *
 * Handles:
 * - Payment processing
 * - Payment status tracking
 * - Transaction verification
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
interface IPaymentRepository {

    /**
     * Process payment for order.
     *
     * @param orderId Order ID to process payment for
     * @param amount Amount to charge
     * @param method Payment method (CREDIT_CARD, DEBIT_CARD, etc)
     * @return Result with Payment details
     */
    suspend fun processPayment(
        orderId: String,
        amount: Double,
        method: String
    ): Result<Payment>

    /**
     * Get payment status.
     *
     * @param paymentId Payment ID to check
     * @return Result with Payment status
     */
    suspend fun getPaymentStatus(paymentId: String): Result<Payment>

    /**
     * Verify payment transaction.
     *
     * @param transactionId Transaction ID from payment gateway
     * @return true if transaction is valid, false otherwise
     */
    suspend fun verifyTransaction(transactionId: String): Result<Boolean>
}
