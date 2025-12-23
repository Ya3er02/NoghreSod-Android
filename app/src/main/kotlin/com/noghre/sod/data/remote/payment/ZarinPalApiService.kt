package com.noghre.sod.data.remote.payment

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

interface ZarinPalApiService {
    @POST("request.json")
    suspend fun requestPayment(@Body request: PaymentRequest): ZarinPalResponse

    @POST("verify.json")
    suspend fun verifyPayment(@Body request: VerifyRequest): VerifyResponse
}

data class PaymentRequest(
    @SerializedName("merchant_id")
    val merchantId: String,
    @SerializedName("amount")
    val amount: Long,
    @SerializedName("description")
    val description: String,
    @SerializedName("callback_url")
    val callbackUrl: String
)

data class ZarinPalResponse(
    @SerializedName("data")
    val data: ZarinPalData?,
    @SerializedName("errors")
    val errors: Map<String, String>?
)

data class ZarinPalData(
    @SerializedName("authority")
    val authority: String,
    @SerializedName("code")
    val code: Int
)

data class VerifyRequest(
    @SerializedName("merchant_id")
    val merchantId: String,
    @SerializedName("amount")
    val amount: Long,
    @SerializedName("authority")
    val authority: String
)

data class VerifyResponse(
    @SerializedName("data")
    val data: VerifyData?,
    @SerializedName("errors")
    val errors: Map<String, String>?
)

data class VerifyData(
    @SerializedName("code")
    val code: Int,
    @SerializedName("ref_id")
    val refId: Long?,
    @SerializedName("card_pan")
    val cardPan: String?
)
