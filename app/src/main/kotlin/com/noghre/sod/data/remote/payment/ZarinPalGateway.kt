package com.noghre.sod.data.remote.payment

import android.os.Build
import com.noghre.sod.BuildConfig
import dagger.hilt.android.scopes.ViewModelScoped
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class ZarinPalGateway @Inject constructor(
    private val apiService: ZarinPalApiService
) : PaymentGateway {

    companion object {
        private const val MERCHANT_ID = "ZARINPAL_MERCHANT_ID"
    }

    override suspend fun initiatePayment(
        amount: Double,
        orderId: String,
        callbackUrl: String
    ): PaymentInitResponse {
        return try {
            val request = PaymentRequest(
                merchant_id = MERCHANT_ID,
                amount = amount,
                description = "سفارش #$orderId",
                callback_url = callbackUrl,
                metadata = MetadataRequest(
                    order_id = orderId
                )
            )

            val response = apiService.requestPayment(request)
            val data = response.data

            if (data?.code == 100 && !data.authority.isNullOrEmpty()) {
                val paymentUrl = buildPaymentUrl(data.authority!!)
                PaymentInitResponse(
                    success = true,
                    authority = data.authority,
                    paymentUrl = paymentUrl
                )
            } else {
                PaymentInitResponse(
                    success = false,
                    message = data?.message ?: "Payment initialization failed"
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Payment initiation error")
            PaymentInitResponse(
                success = false,
                message = e.message ?: "Network error"
            )
        }
    }

    override suspend fun verifyPayment(
        authority: String,
        amount: Double
    ): PaymentVerifyResponse {
        return try {
            val request = VerifyRequest(
                merchant_id = MERCHANT_ID,
                amount = amount,
                authority = authority
            )

            val response = apiService.verifyPayment(request)
            val data = response.data

            if (data?.code == 100 && data.ref_id != null) {
                PaymentVerifyResponse(
                    success = true,
                    refId = data.ref_id.toString(),
                    cardPan = data.card_pan
                )
            } else {
                PaymentVerifyResponse(
                    success = false,
                    message = data?.message ?: "Payment verification failed"
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Payment verification error")
            PaymentVerifyResponse(
                success = false,
                message = e.message ?: "Network error"
            )
        }
    }

    private fun buildPaymentUrl(authority: String): String {
        val baseUrl = if (BuildConfig.DEBUG) {
            "https://sandbox.zarinpal.com/pg/StartPay/$authority"
        } else {
            "https://www.zarinpal.com/pg/StartPay/$authority"
        }
        return baseUrl
    }
}
