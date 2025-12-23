package com.noghre.sod.data.remote.payment

import com.noghre.sod.domain.common.ServerException
import com.noghre.sod.domain.model.PaymentInitResponse
import com.noghre.sod.domain.model.PaymentVerifyResponse
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ZarinPalGateway @Inject constructor(
    private val apiService: ZarinPalApiService,
    private val merchantId: String = "YOUR_MERCHANT_ID"
) : PaymentGateway {

    override suspend fun initiatePayment(
        amount: Double,
        orderId: String,
        callbackUrl: String
    ): PaymentInitResponse {
        try {
            val request = PaymentRequest(
                merchantId = merchantId,
                amount = (amount * 10).toLong(), // ZarinPal expects 10-rial units
                description = "Order: $orderId",
                callbackUrl = callbackUrl
            )
            
            val response = apiService.requestPayment(request)
            
            return if (response.data != null && response.data.code == 100) {
                val authority = response.data.authority
                val paymentUrl = "https://www.zarinpal.com/pg/StartPay/$authority"
                PaymentInitResponse(
                    authority = authority,
                    paymentUrl = paymentUrl
                )
            } else {
                throw ServerException("خطا در ارتباط با درگاه")
            }
        } catch (e: Exception) {
            throw ServerException(e.message ?: "خطای نامشخص")
        }
    }

    override suspend fun verifyPayment(
        authority: String,
        amount: Double
    ): PaymentVerifyResponse {
        try {
            val request = VerifyRequest(
                merchantId = merchantId,
                amount = (amount * 10).toLong(),
                authority = authority
            )
            
            val response = apiService.verifyPayment(request)
            
            return if (response.data != null && response.data.code == 100) {
                PaymentVerifyResponse(
                    success = true,
                    refId = response.data.refId?.toString(),
                    cardPan = response.data.cardPan?.takeLast(4)?.let { "****$it" }
                )
            } else {
                PaymentVerifyResponse(
                    success = false,
                    refId = null,
                    cardPan = null
                )
            }
        } catch (e: Exception) {
            throw ServerException(e.message ?: "خطا در درخواست با درگاه")
        }
    }
}
