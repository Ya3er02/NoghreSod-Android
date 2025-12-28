package com.noghre.sod.domain.model

/**
 * Payment status enumeration
 */
enum class PaymentStatus {
    PENDING,        // در انتظار پرداخت
    PROCESSING,     // در حال پردازش
    SUCCESS,        // موفق
    FAILED,         // ناموفق
    CANCELLED,      // لغو شده
    REFUNDED        // برگشت داده شده
}

/**
 * Payment gateway type enumeration
 */
enum class PaymentGateway {
    ZARINPAL,           // زرین‌پال (Most Popular)
    IDPAY,              // آیدی‌پی
    NEXTPAY,            // نکست‌پی
    ZIBAL,              // زیبال
    PAYPINGSUM,         // پی‌پینگ
    CASH_ON_DELIVERY    // پرداخت در محل
}

/**
 * Payment request data model
 * Used when initiating a payment transaction
 */
data class PaymentRequest(
    val orderId: String,
    val amount: Long,           // به تومان
    val gateway: PaymentGateway,
    val description: String,
    val callbackUrl: String,
    val mobile: String?,        // شماره موبایل پرداخت‌کننده
    val email: String?          // ایمیل (اختیاری)
)

/**
 * Payment response from gateway
 * Contains payment authority and URL for user redirect
 */
data class PaymentResponse(
    val authority: String,      // کد رهگیری درگاه
    val paymentUrl: String,     // لینک پرداخت (برای redirect)
    val status: PaymentStatus,
    val message: String?
)

/**
 * Payment verification result
 * Received after user completes payment
 */
data class PaymentVerification(
    val orderId: String,
    val authority: String,      // کد رهگیری
    val refId: String?,         // شماره مرجع تراکنش
    val cardPan: String?,       // شماره کارت (آخر 4 رقم)
    val cardHash: String?,      // هش کارت
    val feeType: String?,       // نوع کارمزد
    val fee: Long?,             // مبلغ کارمزد
    val status: PaymentStatus,
    val verifiedAt: Long        // زمان تایید
)

/**
 * Complete payment information
 * Stored in database
 */
data class Payment(
    val id: String,
    val orderId: String,
    val amount: Long,           // مبلغ سفارش (تومان)
    val gateway: PaymentGateway,
    val authority: String?,     // کد رهگیری درگاه
    val refId: String?,         // شماره مرجع
    val status: PaymentStatus,
    val createdAt: Long,        // زمان ایجاد
    val paidAt: Long?,          // زمان پرداخت
    val description: String?    // توضیح تراکنش
)