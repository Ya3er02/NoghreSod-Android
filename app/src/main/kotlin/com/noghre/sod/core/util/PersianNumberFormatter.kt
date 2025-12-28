package com.noghre.sod.core.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * فرمت‌کننده اعداد ایرانی
 * برای کار با اعداد فارسی و ریال‌ها و تومان‌ها
 */
object PersianNumberFormatter {
    
    private val farsiDigits = arrayOf(
        "۰", "۱", "۲", "۳", "۴",
        "۵", "۶", "۷", "۸", "۹"
    )
    
    /**
     * تبدیل قیمت به فرمت فارسی
     * 1250000 → ۱٬۲۵۰٬۰۰۰ تومان
     */
    fun formatPrice(amount: Long): String {
        val formatted = String.format("%,d", amount)
        val farsiFormatted = toFarsiNumbers(formatted)
        return "$farsiFormatted تومان"
    }
    
    /**
     * تبدیل قیمت بدون یام
     * 1250000 → ۱٬۲۵۰٬۰۰۰
     */
    fun formatPriceWithoutCurrency(amount: Long): String {
        val formatted = String.format("%,d", amount)
        return toFarsiNumbers(formatted)
    }
    
    /**
     * تبدیل شماره موبایل
     * 09101234567 → زمینه: 0910 123 4567
     */
    fun formatPhoneNumber(phone: String): String {
        var cleaned = phone.replace(Regex("[^0-9]"), "")
        
        // تبدیل +98 به 0
        if (cleaned.startsWith("+98")) {
            cleaned = "0" + cleaned.substring(3)
        } else if (cleaned.startsWith("98")) {
            cleaned = "0" + cleaned.substring(2)
        }
        
        // فرمت: 0910 123 4567
        return if (cleaned.length >= 10) {
            val formatted = "${cleaned.substring(0, 4)} ${cleaned.substring(4, 7)} ${cleaned.substring(7)}"
            toFarsiNumbers(formatted)
        } else {
            toFarsiNumbers(cleaned)
        }
    }
    
    /**
     * تبدیل شماره کارت با mask
     * 6037697126661 → 603769 *** 6661
     */
    fun formatCardNumber(cardNumber: String): String {
        val cleaned = cardNumber.replace(Regex("[^0-9]"), "")
        return if (cleaned.length >= 10) {
            val masked = "${cleaned.substring(0, 6)} *** ${cleaned.substring(cleaned.length - 4)}"
            toFarsiNumbers(masked)
        } else {
            toFarsiNumbers(cleaned)
        }
    }
    
    /**
     * تبدیل اعداد انگلیسی به فارسی
     */
    fun toFarsiNumbers(input: String): String {
        var output = input
        for (i in 0..9) {
            output = output.replace(i.toString(), farsiDigits[i])
        }
        return output
    }
    
    /**
     * تبدیل اعداد فارسی به انگلیسی
     */
    fun toEnglishNumbers(input: String): String {
        var output = input
        for (i in 0..9) {
            output = output.replace(farsiDigits[i], i.toString())
        }
        return output
    }
    
    /**
     * کالکولاسیون وزن و قیمت نقره
     * weight (gram) و price per gram → total price
     */
    fun calculateSilverPrice(weight: Double, pricePerGram: Long): Long {
        return (weight * pricePerGram).toLong()
    }
    
    /**
     * مالیات 9% را اضافه کنید
     */
    fun addTax(basePrice: Long, taxRate: Float = 0.09f): Long {
        return (basePrice * (1 + taxRate)).toLong()
    }
    
    /**
     * قیمت نهايی = قیمت + مالیات + هزینه ارسال
     */
    fun calculateFinalPrice(
        basePrice: Long,
        shippingCost: Long = 0,
        taxRate: Float = 0.09f
    ): Long {
        val subtotal = basePrice + shippingCost
        val tax = (subtotal * taxRate).toLong()
        return subtotal + tax
    }
    
    /**
     * فرمت وزن با گرم
     * 15.5 → ۱۵.۵ گرم
     */
    fun formatWeight(weight: Double): String {
        val formatted = String.format("%.1f", weight)
        return "${toFarsiNumbers(formatted)} گرم"
    }
    
    /**
     * فرمت آیدی (شناسه مالی ایرانی)
     */
    fun formatIranianNationalId(id: String): String {
        val cleaned = id.replace(Regex("[^0-9]"), "")
        return if (cleaned.length == 10) {
            val formatted = "${cleaned.substring(0, 3)}-${cleaned.substring(3, 7)}-${cleaned.substring(7)}"
            toFarsiNumbers(formatted)
        } else {
            toFarsiNumbers(cleaned)
        }
    }
    
    /**
     * فرمت شبان (شناسه حساب بانکی)
     */
    fun formatIBAN(iban: String): String {
        val cleaned = iban.replace(Regex("[^A-Z0-9]"), "")
        val parts = cleaned.chunked(4).map { it }
        return parts.joinToString(" ").also { toFarsiNumbers(it) }
    }
}
