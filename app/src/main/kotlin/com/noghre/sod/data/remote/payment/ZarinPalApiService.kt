package com.noghre.sod.data.remote.payment

import retrofit2.http.Body
import retrofit2.http.POST

data class PaymentRequest(
    val merchant_id: String,
    val amount: Double,
    val currency: String = "IRT",
    val description: String,
    val callback_url: String,
    val metadata: MetadataRequest? = null
)

data class MetadataRequest(
    val order_id: String,
    val user_id: String? = null,
    val email: String? = null,
    val phone: String? = null
)

data class ZarinPalResponse<T>(
    val data: T? = null,
    val errors: Any? = null
)

data class PaymentAuthorityResponse(
    val authority: String? = null,
    val ref_id: Int? = null,
    val code: Int? = null,
    val message: String? = null
)

data class VerifyRequest(
    val merchant_id: String,
    val amount: Double,
    val authority: String
)

data class VerifyResponse(
    val ref_id: Int? = null,
    val card_pan: String? = null,
    val card_hash: String? = null,
    val code: Int? = null,
    val message: String? = null
)

interface ZarinPalApiService {
    @POST("pg/v4/payment/request.json")
    suspend fun requestPayment(@Body request: PaymentRequest): ZarinPalResponse<PaymentAuthorityResponse>

    @POST("pg/v4/payment/verify.json")
    suspend fun verifyPayment(@Body request: VerifyRequest): ZarinPalResponse<VerifyResponse>
}
