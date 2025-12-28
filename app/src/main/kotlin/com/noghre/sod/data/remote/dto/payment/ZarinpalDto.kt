package com.noghre.sod.data.remote.dto.payment

import com.google.gson.annotations.SerializedName

/**
 * Zarinpal payment request DTO
 * Sent to Zarinpal API to initiate payment
 */
data class ZarinpalPaymentRequestDto(
    @SerializedName("merchant_id")
    val merchantId: String,
    
    @SerializedName("amount")
    val amount: Long,           // به ریال (تومان ال ریال به ریال)
    
    @SerializedName("callback_url")
    val callbackUrl: String,    // URL برای return بعد از پرداخت
    
    @SerializedName("description")
    val description: String,    // توضیح تراکنش
    
    @SerializedName("metadata")
    val metadata: ZarinpalMetadataDto? = null  // اطلاعات اضافی
)

/**
 * Zarinpal metadata
 * Additional information about the transaction
 */
data class ZarinpalMetadataDto(
    @SerializedName("mobile")
    val mobile: String? = null,  // موبایل خریدار
    
    @SerializedName("email")
    val email: String? = null,   // ایمیل خریدار
    
    @SerializedName("order_id")
    val orderId: String? = null  // شناسه سفارش
)

/**
 * Zarinpal payment response DTO
 * Received after requesting payment
 */
data class ZarinpalPaymentResponseDto(
    @SerializedName("data")
    val data: ZarinpalPaymentDataDto?,
    
    @SerializedName("errors")
    val errors: List<String>?  // لیست خطاها
)

/**
 * Zarinpal payment data in response
 */
data class ZarinpalPaymentDataDto(
    @SerializedName("code")
    val code: Int,              // 100 = موفق
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("authority")
    val authority: String,      // کد رهگیری که برای verify استفاده خواهد شد
    
    @SerializedName("fee_type")
    val feeType: String?,       // نوع کارمزد
    
    @SerializedName("fee")
    val fee: Long?              // مبلغ کارمزد
)

/**
 * Zarinpal verify request DTO
 * Sent to verify payment after user returns
 */
data class ZarinpalVerifyRequestDto(
    @SerializedName("merchant_id")
    val merchantId: String,
    
    @SerializedName("amount")
    val amount: Long,           // به ریال
    
    @SerializedName("authority")
    val authority: String       // کد رهگیری دریافت شده از callback
)

/**
 * Zarinpal verify response DTO
 * Received after verification
 */
data class ZarinpalVerifyResponseDto(
    @SerializedName("data")
    val data: ZarinpalVerifyDataDto?,
    
    @SerializedName("errors")
    val errors: List<String>?  // لیست خطاها
)

/**
 * Zarinpal verify data in response
 */
data class ZarinpalVerifyDataDto(
    @SerializedName("code")
    val code: Int,              // 100 و 101 = موفق
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("card_hash")
    val cardHash: String?,      // هش کارت بانکی
    
    @SerializedName("card_pan")
    val cardPan: String?,       // 4 رقم آخر کارت
    
    @SerializedName("ref_id")
    val refId: Long?,           // شماره مرجع تراکنش (اهمیترین اطلاع)
    
    @SerializedName("fee_type")
    val feeType: String?,       // نوع کارمزد
    
    @SerializedName("fee")
    val fee: Long?              // مبلغ کارمزد
)