package com.noghre.sod.domain.repository

import com.noghre.sod.domain.model.PaymentInitResponse
import com.noghre.sod.domain.model.PaymentVerifyResponse

interface PaymentRepository {
    suspend fun initiatePayment(
        orderId: String,
        amount: Double
    ): PaymentInitResponse

    suspend fun verifyPayment(
        authority: String,
        amount: Double,
        orderId: String
    ): PaymentVerifyResponse
}
