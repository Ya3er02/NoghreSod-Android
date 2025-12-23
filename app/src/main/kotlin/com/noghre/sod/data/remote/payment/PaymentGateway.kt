package com.noghre.sod.data.remote.payment

import com.noghre.sod.domain.model.PaymentInitResponse
import com.noghre.sod.domain.model.PaymentVerifyResponse

interface PaymentGateway {
    suspend fun initiatePayment(
        amount: Double,
        orderId: String,
        callbackUrl: String
    ): PaymentInitResponse

    suspend fun verifyPayment(
        authority: String,
        amount: Double
    ): PaymentVerifyResponse
}
