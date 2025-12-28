package com.noghre.sod.data.payment

import com.squareup.moshi.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * API Service for IDPay Payment Gateway
 * آی‌دی‌پی قابل اعتماد
 */
interface IDPayApiService {
    
    @POST("api/v1.1/payment")
    suspend fun requestPayment(
        @Header("X-API-KEY") apiKey: String,
        @Body request: IDPayPaymentRequest
    ): IDPayPaymentResponse
    
    @POST("api/v1.1/payment/verify")
    suspend fun verifyPayment(
        @Header("X-API-KEY") apiKey: String,
        @Body request: IDPayVerifyRequest
    ): IDPayVerifyResponse
}

data class IDPayPaymentRequest(
    @Json(name = "order_id")
    val orderId: String,
    
    @Json(name = "amount")
    val amount: Long,  // به تومان
    
    @Json(name = "phone")
    val phone: String,
    
    @Json(name = "mail")
    val email: String,
    
    @Json(name = "desc")
    val description: String,
    
    @Json(name = "callback")
    val callbackUrl: String
)

data class IDPayPaymentResponse(
    @Json(name = "link")
    val link: String,  // لینک پرداخت
    
    @Json(name = "id")
    val id: String,
    
    @Json(name = "status")
    val status: Int
)

data class IDPayVerifyRequest(
    @Json(name = "id")
    val id: String,
    
    @Json(name = "order_id")
    val orderId: String
)

data class IDPayVerifyResponse(
    @Json(name = "status")
    val status: Int,
    
    @Json(name = "track_id")
    val trackId: String,
    
    @Json(name = "order_id")
    val orderId: String,
    
    @Json(name = "amount")
    val amount: Long,
    
    @Json(name = "date")
    val date: String
)

/**
 * IDPay Payment Service Implementation
 */
class IDPayPaymentService(
    private val api: IDPayApiService,
    private val apiKey: String = "YOUR_IDPAY_API_KEY"
) : PaymentService {
    
    override suspend fun requestPayment(
        orderId: String,
        amount: Long,
        phone: String,
        email: String,
        description: String,
        callbackUrl: String
    ): PaymentResponse = withContext(Dispatchers.IO) {
        try {
            val request = IDPayPaymentRequest(
                orderId = orderId,
                amount = amount,
                phone = phone,
                email = email,
                description = description,
                callbackUrl = callbackUrl
            )
            
            val response = api.requestPayment(apiKey, request)
            
            PaymentResponse(
                success = response.status == 1,
                paymentUrl = response.link,
                transactionId = response.id,
                message = "درخواست پرداخت با موفقیت ارسال شد"
            )
        } catch (e: Exception) {
            PaymentResponse(
                success = false,
                paymentUrl = null,
                transactionId = null,
                message = "خطا در درخواست پرداخت: ${e.message}"
            )
        }
    }
    
    override suspend fun verifyPayment(
        transactionId: String,
        orderId: String
    ): VerifyResponse = withContext(Dispatchers.IO) {
        try {
            val request = IDPayVerifyRequest(
                id = transactionId,
                orderId = orderId
            )
            
            val response = api.verifyPayment(apiKey, request)
            
            VerifyResponse(
                success = response.status == 1,
                transactionId = response.trackId,
                orderId = response.orderId,
                amount = response.amount,
                message = اگر response.status == 1 "پرداخت تایید شد" else "پرداخت ناموفق"
            )
        } catch (e: Exception) {
            VerifyResponse(
                success = false,
                transactionId = null,
                orderId = orderId,
                amount = 0,
                message = "خطا در تایید: ${e.message}"
            )
        }
    }
}
