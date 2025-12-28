package com.noghre.sod.data.payment

import com.squareup.moshi.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * API Service for NextPay Payment Gateway
 * نکست‌پی - سريع و قابل اعتماد
 */
interface NextPayApiService {
    
    @FormUrlEncoded
    @POST("/gateway/send")
    suspend fun requestPayment(
        @Field("api_key") apiKey: String,
        @Field("order_id") orderId: String,
        @Field("amount") amount: Long,
        @Field("currency") currency: String = "TMN",
        @Field("client_mobile") phone: String,
        @Field("callback_uri") callbackUrl: String
    ): NextPayPaymentResponse
    
    @FormUrlEncoded
    @POST("/gateway/verify")
    suspend fun verifyPayment(
        @Field("api_key") apiKey: String,
        @Field("trans_id") transactionId: String,
        @Field("amount") amount: Long
    ): NextPayVerifyResponse
}

data class NextPayPaymentResponse(
    @Json(name = "code")
    val code: Int,  // 0 = success
    
    @Json(name = "trans_id")
    val transactionId: String,
    
    @Json(name = "gateway_name")
    val gatewayName: String = "NEXTPAY"
)

data class NextPayVerifyResponse(
    @Json(name = "code")
    val code: Int,  // 0 = success
    
    @Json(name = "trans_id")
    val transactionId: String,
    
    @Json(name = "order_id")
    val orderId: String,
    
    @Json(name = "amount")
    val amount: Long,
    
    @Json(name = "status")
    val status: Int  // -1 = pending, 0 = success, 1 = failed
)

/**
 * NextPay Payment Service Implementation
 */
class NextPayPaymentService(
    private val api: NextPayApiService,
    private val apiKey: String = "YOUR_NEXTPAY_API_KEY"
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
            val response = api.requestPayment(
                apiKey = apiKey,
                orderId = orderId,
                amount = amount,
                phone = phone,
                callbackUrl = callbackUrl
            )
            
            // NextPay returns code 0 for success
            val paymentUrl = if (response.code == 0) {
                "https://nextpay.org/gateway/${ response.transactionId}"
            } else {
                null
            }
            
            PaymentResponse(
                success = response.code == 0,
                paymentUrl = paymentUrl,
                transactionId = response.transactionId,
                message = اگر response.code == 0 "درخواست پرداخت ارسال شد" else "خطا: كد ${response.code}"
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
            // Fetch amount from database or order
            // For now, we'll use a placeholder
            val amount = 0L  // Should be fetched from order
            
            val response = api.verifyPayment(
                apiKey = apiKey,
                transactionId = transactionId,
                amount = amount
            )
            
            // NextPay returns code 0 for success
            val success = response.code == 0 && response.status == 0
            
            VerifyResponse(
                success = success,
                transactionId = response.transactionId,
                orderId = response.orderId,
                amount = response.amount,
                message = اگر success "پرداخت تایید شد" else "پرداخت ناموفق"
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
    
    /**
     * Build payment URL for NextPay
     */
    fun buildPaymentUrl(transactionId: String): String {
        return "https://nextpay.org/gateway/$transactionId"
    }
}
