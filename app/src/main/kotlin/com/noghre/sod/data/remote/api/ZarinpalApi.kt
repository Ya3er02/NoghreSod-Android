package com.noghre.sod.data.remote.api

import com.noghre.sod.data.remote.dto.payment.ZarinpalPaymentRequestDto
import com.noghre.sod.data.remote.dto.payment.ZarinpalPaymentResponseDto
import com.noghre.sod.data.remote.dto.payment.ZarinpalVerifyRequestDto
import com.noghre.sod.data.remote.dto.payment.ZarinpalVerifyResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Zarinpal Payment Gateway API
 * Documentation: https://docs.zarinpal.com/paymentGateway/
 * 
 * Most popular Iranian payment gateway
 * Supports multiple Iranian banks
 */
interface ZarinpalApi {
    
    companion object {
        // Production URLs
        const val BASE_URL = "https://api.zarinpal.com/pg/v4/payment/"
        const val PAYMENT_URL = "https://www.zarinpal.com/pg/StartPay/"
        
        // Sandbox URLs (for testing)
        const val SANDBOX_URL = "https://sandbox.zarinpal.com/pg/v4/payment/"
        const val SANDBOX_PAYMENT_URL = "https://sandbox.zarinpal.com/pg/StartPay/"
    }
    
    /**
     * Request payment from Zarinpal
     * 
     * Endpoint: POST /request.json
     * 
     * @param request Payment request details
     * @return Payment response with authority code
     */
    @POST("request.json")
    suspend fun requestPayment(
        @Body request: ZarinpalPaymentRequestDto
    ): Response<ZarinpalPaymentResponseDto>
    
    /**
     * Verify payment after user returns from gateway
     * 
     * Endpoint: POST /verify.json
     * 
     * @param request Verification request with authority and amount
     * @return Verification result with transaction details
     */
    @POST("verify.json")
    suspend fun verifyPayment(
        @Body request: ZarinpalVerifyRequestDto
    ): Response<ZarinpalVerifyResponseDto>
}