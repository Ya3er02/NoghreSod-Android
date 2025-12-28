package com.noghre.sod.data.payment

import com.noghre.sod.BuildConfig
import com.noghre.sod.core.error.AppError
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.remote.api.ZarinpalApi
import com.noghre.sod.data.remote.dto.payment.*
import com.noghre.sod.domain.model.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Zarinpal Payment Service Implementation
 * Handles all communication with Zarinpal payment gateway
 */
@Singleton
class ZarinpalPaymentService @Inject constructor(
    private val api: ZarinpalApi
) {
    
    companion object {
        // TODO: Move to BuildConfig or secure storage (like gradle.properties)
        // Don't commit actual merchant ID to public repository!
        private const val MERCHANT_ID = "YOUR_ZARINPAL_MERCHANT_ID"
        private const val USE_SANDBOX = BuildConfig.DEBUG  // Use sandbox in debug builds
    }
    
    /**
     * Request payment from Zarinpal
     * 
     * @param request Payment request details
     * @return Result with PaymentResponse containing payment URL for redirect
     */
    suspend fun requestPayment(
        request: PaymentRequest
    ): Result<PaymentResponse> {
        return try {
            Timber.d("Requesting Zarinpal payment for order: ${request.orderId}")
            
            // Prepare request DTO
            val dto = ZarinpalPaymentRequestDto(
                merchantId = MERCHANT_ID,
                amount = request.amount * 10,  // Convert Toman to Rial
                callbackUrl = request.callbackUrl,
                description = request.description,
                metadata = ZarinpalMetadataDto(
                    mobile = request.mobile,
                    email = request.email,
                    orderId = request.orderId
                )
            )
            
            // Make API call
            val response = api.requestPayment(dto)
            
            // Process response
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                
                // Check if data exists and code is 100 (success)
                if (body.data != null && body.data.code == 100) {
                    // Build payment URL based on environment
                    val paymentUrl = if (USE_SANDBOX) {
                        "${ZarinpalApi.SANDBOX_PAYMENT_URL}${body.data.authority}"
                    } else {
                        "${ZarinpalApi.PAYMENT_URL}${body.data.authority}"
                    }
                    
                    Timber.i("Zarinpal payment requested successfully. Authority: ${body.data.authority}")
                    
                    Result.Success(
                        PaymentResponse(
                            authority = body.data.authority,
                            paymentUrl = paymentUrl,
                            status = PaymentStatus.PENDING,
                            message = body.data.message
                        )
                    )
                } else {
                    // API returned error
                    val errorMsg = body.errors?.joinToString(", ") ?: "Unknown error from Zarinpal"
                    Timber.e("Zarinpal payment request failed: $errorMsg")
                    Result.Error(AppError.Payment(errorMsg))
                }
            } else {
                // HTTP error
                val statusCode = response.code()
                Timber.e("Zarinpal API HTTP error: $statusCode")
                Result.Error(AppError.Network("خطا در ارتباط با درگاه (کد: $statusCode)", statusCode))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception while requesting Zarinpal payment")
            Result.Error(AppError.Unknown(e.message ?: "خطای نامشخص در درخواست پرداخت"))
        }
    }
    
    /**
     * Verify payment after user returns from Zarinpal gateway
     * 
     * @param authority Payment authority from callback
     * @param amount Transaction amount (for verification)
     * @return Result with PaymentVerification containing transaction details
     */
    suspend fun verifyPayment(
        authority: String,
        amount: Long
    ): Result<PaymentVerification> {
        return try {
            Timber.d("Verifying Zarinpal payment. Authority: $authority")
            
            // Prepare verify request DTO
            val dto = ZarinpalVerifyRequestDto(
                merchantId = MERCHANT_ID,
                amount = amount * 10,  // Convert Toman to Rial
                authority = authority
            )
            
            // Make API call
            val response = api.verifyPayment(dto)
            
            // Process response
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                
                // Check if data exists and code is 100 or 101 (both mean success)
                if (body.data != null && (body.data.code == 100 || body.data.code == 101)) {
                    Timber.i("Zarinpal payment verified successfully. RefId: ${body.data.refId}")
                    
                    Result.Success(
                        PaymentVerification(
                            orderId = "",  // Will be filled from metadata or callback params
                            authority = authority,
                            refId = body.data.refId?.toString(),  // Important: Transaction reference
                            cardPan = body.data.cardPan,           // Last 4 digits of card
                            cardHash = body.data.cardHash,         // Card hash
                            feeType = body.data.feeType,
                            fee = body.data.fee,
                            status = PaymentStatus.SUCCESS,
                            verifiedAt = System.currentTimeMillis()
                        )
                    )
                } else {
                    // Verification failed
                    val errorMsg = body.errors?.joinToString(", ") ?: "Payment verification failed"
                    Timber.w("Zarinpal payment verification failed: $errorMsg")
                    Result.Error(AppError.Payment(errorMsg))
                }
            } else {
                // HTTP error
                val statusCode = response.code()
                Timber.e("Zarinpal verification HTTP error: $statusCode")
                Result.Error(AppError.Network("خطا در تأیید پرداخت (کد: $statusCode)", statusCode))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception while verifying Zarinpal payment")
            Result.Error(AppError.Unknown(e.message ?: "خطای نامشخص در تأیید پرداخت"))
        }
    }
}