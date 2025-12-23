package com.noghre.sod.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Payment-related DTOs for Zarinpal/IDPay integration
 */

@Serializable
data class InitPaymentRequestDto(
    @SerialName("order_id")
    val orderId: String,
    @SerialName("amount")
    val amount: Long, // Amount in Toman
    @SerialName("return_url")
    val returnUrl: String,
    @SerialName("description")
    val description: String = "خرید از فروشگاه نقره‌سود"
)

@Serializable
data class InitPaymentResponseDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("authority")
    val authority: String?,
    @SerialName("payment_url")
    val paymentUrl: String?,
    @SerialName("message")
    val message: String? = null
)

@Serializable
data class VerifyPaymentRequestDto(
    @SerialName("order_id")
    val orderId: String,
    @SerialName("authority")
    val authority: String
)

@Serializable
data class VerifyPaymentResponseDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("ref_id")
    val refId: String?,
    @SerialName("message")
    val message: String? = null
)

@Serializable
data class PaymentStatusDto(
    @SerialName("order_id")
    val orderId: String,
    @SerialName("status")
    val status: String, // UNPAID, PAID, REFUNDED, FAILED
    @SerialName("ref_id")
    val refId: String? = null,
    @SerialName("paid_at")
    val paidAt: Long? = null
)

/**
 * Zarinpal-specific response wrappers
 */
@Serializable
data class ZarinpalPaymentResponse(
    @SerialName("data")
    val data: ZarinpalPaymentData? = null,
    @SerialName("errors")
    val errors: Map<String, String>? = null
)

@Serializable
data class ZarinpalPaymentData(
    @SerialName("authority")
    val authority: String,
    @SerialName("code")
    val code: Int
)

@Serializable
data class ZarinpalVerifyResponse(
    @SerialName("data")
    val data: ZarinpalVerifyData? = null,
    @SerialName("errors")
    val errors: Map<String, String>? = null
)

@Serializable
data class ZarinpalVerifyData(
    @SerialName("code")
    val code: Int,
    @SerialName("ref_id")
    val refId: String,
    @SerialName("card_pan")
    val cardPan: String? = null,
    @SerialName("card_hash")
    val cardHash: String? = null
)

/**
 * IDPay-specific request/response wrappers (alternative payment gateway)
 */
@Serializable
data class IDPayPaymentRequest(
    @SerialName("order_id")
    val orderId: String,
    @SerialName("amount")
    val amount: Long,
    @SerialName("phone")
    val phone: String,
    @SerialName("mail")
    val email: String?,
    @SerialName("name")
    val name: String,
    @SerialName("desc")
    val description: String = "خرید از نقره‌سود",
    @SerialName("callback")
    val callbackUrl: String
)

@Serializable
data class IDPayPaymentResponse(
    @SerialName("id")
    val id: String?,
    @SerialName("link")
    val link: String?,
    @SerialName("error_code")
    val errorCode: Int? = null,
    @SerialName("error_message")
    val errorMessage: String? = null
)

@Serializable
data class IDPayVerifyRequest(
    @SerialName("id")
    val id: String,
    @SerialName("order_id")
    val orderId: String,
    @SerialName("track_id")
    val trackId: String
)

@Serializable
data class IDPayVerifyResponse(
    @SerialName("id")
    val id: String?,
    @SerialName("order_id")
    val orderId: String?,
    @SerialName("track_id")
    val trackId: String?,
    @SerialName("amount")
    val amount: Long? = null,
    @SerialName("date")
    val date: String? = null,
    @SerialName("payment")
    val payment: IDPayPaymentInfo? = null,
    @SerialName("error_code")
    val errorCode: Int? = null
)

@Serializable
data class IDPayPaymentInfo(
    @SerialName("track_id")
    val trackId: String,
    @SerialName("amount")
    val amount: Long,
    @SerialName("date")
    val date: String,
    @SerialName("card_no")
    val cardNo: String
)
