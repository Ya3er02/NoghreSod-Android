package com.noghre.sod.core.config

import java.util.TimeZone

/**
 * شرایط بازار ایران
 * برای مشابه قققل ایرانیان در ایران
 */
data class IranMarketConfig(
    // منطقه‌زمانی
    val timezone: String = "Asia/Tehran",
    
    // پول: تومان (نه ریال!)
    val currency: String = "TMN",
    val currencySymbol: String = "ت",
    
    // محدودیت‌های بانکی
    val maxTransactionAmount: Long = 999_999_999,  // حد اکثر
    val minTransactionAmount: Long = 1_000,        // حد اقل
    
    // مالیات
    val taxRate: Float = 0.09f,  // ۹ درصد (VAT)
    
    // هزینه ارسال
    val baseShippingCost: Long = 5_000,      // ۵ هزار تومان
    val shippingPerKg: Long = 2_000,         // ۲ هزار تومان برای کیلوگرم
    val maxFreeShipping: Long = 500_000,     // رایگان ارسال بالای 500هزار
    
    // استان‌ها
    val provinces: List<String> = listOf(
        "تهران",
        "البرز",
        "قزوین",
        "مازندران",
        "گیلان",
        "اردبیل",
        "اصفهان",
        "سمنان",
        "زنجان",
        "همدان",
        "لرستان",
        "اللوز
        "بوشهر",
        "خراسان رضوی",
        "خراسان شمالی",
        "سیستان و بلوچستان",
        "خوزستان",
        "هرمزگان",
        "هرمسلاز",
        "کرمنو",
        "رطلطللللللح
        "فارس",
        "همدان",
        "هرمزگان",
        "کرمانشاه",
        "لرستان",
        "لرستان",
        "مركزی",
        "قم",
        "رازاوی",
        "یزد"
    )
)

/**
 * درگاه‌های پرداخت
 */
enum class PaymentGateway(val displayName: String) {
    ZARINPAL("زرین‌پال"),
    IDPAY("آی‌دی‌پی"),
    NEXTPAY("نکست‌پی"),
    ZIBAL("زیبال"),
    CASH_ON_DELIVERY("پرداخت در محل")
}

/**
 * روش‌های ارسال
 */
enum class ShippingMethod(val displayName: String, val deliveryDays: Int) {
    DENA("دنا", 3),
    MONTAQEL_OL_BAD("منتقل الباد", 2),
    JOOE_POST("جور پست", 5),
    ZERBAHAR("زرباخر با Tracking", 1)
}

/**
 * بانک‌های معتبر
 */
object IranianBankingRules {
    
    val supportedBanks = listOf(
        "بانک ملی ایران",
        "بانک سپه",
        "بانک بنابان",
        "بانک صنعت و معدن",
        "بانک توسعه تعاون",
        "بانک دی",
        "بانک رسالت",
        "بانک شهر",
        "بانک رفاه",
        "بانک کارآفرین",
        "بانک مسکن"
    )
    
    /**
     * خدماتی که برای ایران تحریم است
     */
    val sanctionedServices = listOf(
        "PayPal",
        "Stripe",
        "Square",
        "2Checkout",
        "Skrill",
        "Payoneer",
        "Wise"
    )
    
    fun isBankSupported(bankName: String): Boolean {
        return supportedBanks.any { it.contains(bankName, ignoreCase = true) }
    }
    
    fun isServiceAvailable(serviceName: String): Boolean {
        return !sanctionedServices.any { it.contains(serviceName, ignoreCase = true) }
    }
}

object IranConfig {
    val config = IranMarketConfig()
    
    // حقیقی استانهای ایران
    val allProvinces = config.provinces
    
    // درگاه‌های معتبر
    val availableGateways = listOf(
        PaymentGateway.ZARINPAL,
        PaymentGateway.IDPAY,
        PaymentGateway.NEXTPAY
    )
    
    // روش‌های ارسال
    val shippingMethods = ShippingMethod.values().toList()
    
    // Currency
    val currency = "Toman (TMN)"
}
