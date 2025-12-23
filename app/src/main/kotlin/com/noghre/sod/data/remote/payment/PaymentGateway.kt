package com.noghre.sod.data.remote.payment

data class PaymentInitResponse(
    val success: Boolean,
    val authority: String? = null,
    val paymentUrl: String? = null,
    val message: String? = null
)

data class PaymentVerifyResponse(
    val success: Boolean,
    val refId: String? = null,
    val cardPan: String? = null,
    val message: String? = null,
    val amount: Double? = null
)

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
