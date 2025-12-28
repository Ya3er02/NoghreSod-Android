package com.noghre.sod.domain.repository

import com.noghre.sod.core.util.Result
import com.noghre.sod.domain.model.*

/**
 * Payment Repository Interface
 * Abstracts payment operations and supports multiple gateways
 */
interface PaymentRepository {
    
    /**
     * Request payment from specified gateway
     * Handles initiation of payment transaction
     * 
     * @param orderId Order identifier
     * @param amount Transaction amount in Toman
     * @param gateway Payment gateway to use
     * @param mobile Customer mobile number
     * @return Result with PaymentResponse containing payment URL
     */
    suspend fun requestPayment(
        orderId: String,
        amount: Long,
        gateway: PaymentGateway,
        mobile: String?
    ): Result<PaymentResponse>
    
    /**
     * Verify payment after user returns from gateway
     * Confirms successful payment and updates order status
     * 
     * @param authority Payment authority from callback
     * @param amount Transaction amount to verify
     * @param gateway Gateway that was used
     * @return Result with PaymentVerification containing transaction details
     */
    suspend fun verifyPayment(
        authority: String,
        amount: Long,
        gateway: PaymentGateway
    ): Result<PaymentVerification>
    
    /**
     * Get payment details
     * 
     * @param paymentId Payment identifier
     * @return Result with Payment object
     */
    suspend fun getPayment(paymentId: String): Result<Payment>
    
    /**
     * Get all payments for specific order
     * 
     * @param orderId Order identifier
     * @return Result with list of payments
     */
    suspend fun getOrderPayments(orderId: String): Result<List<Payment>>
}