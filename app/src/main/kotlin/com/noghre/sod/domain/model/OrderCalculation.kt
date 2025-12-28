package com.noghre.sod.domain.model

import com.noghre.sod.core.util.PersianNumberFormatter

/**
 * برکاه اعی سفارش (محاسبه قیمت)
 */
data class OrderCalculation(
    // قاعده قیمت
    val basePrice: Long,                    // قیمت ابتدایی (بدون مالیات)
    
    // تخفیفات
    val discountPercentage: Float = 0f,     // درصد تخفیف
    val discountAmount: Long = 0,           // خطی تخفیف
    
    // هزینه‌ها
    val shippingCost: Long = 0,             // هزینه ارسال
    val insuranceCost: Long = 0,            // هزینه بیمه
    
    // مالیات (الزامی)
    val taxRate: Float = 0.09f              // ترخ مالیات 9% (فهرست)
) {
    
    /**
     * قیمت بعد از تخفیف
     */
    val priceAfterDiscount: Long
        get() {
            val discount = if (discountPercentage > 0) {
                (basePrice * discountPercentage).toLong()
            } else {
                discountAmount
            }
            return basePrice - discount
        }
    
    /**
     * جمع قبل از مالیات
     */
    val subtotal: Long
        get() = priceAfterDiscount + shippingCost + insuranceCost
    
    /**
     * مالیات (9% از Subtotal)
     */
    val taxAmount: Long
        get() = (subtotal * taxRate).toLong()
    
    /**
     * قیمت نهایی
     */
    val totalPrice: Long
        get() = subtotal + taxAmount
    
    /**
     * برگشاع قیمتها
     */
    fun getPriceBreakdown(): PriceBreakdown {
        return PriceBreakdown(
            basePrice = basePrice,
            discountAmount = if (discountPercentage > 0) {
                (basePrice * discountPercentage).toLong()
            } else {
                discountAmount
            },
            priceAfterDiscount = priceAfterDiscount,
            shippingCost = shippingCost,
            insuranceCost = insuranceCost,
            subtotal = subtotal,
            taxAmount = taxAmount,
            totalPrice = totalPrice
        )
    }
}

/**
 * برگشاع قیمت برای نمایش خخ
 */
data class PriceBreakdown(
    val basePrice: Long,
    val discountAmount: Long,
    val priceAfterDiscount: Long,
    val shippingCost: Long,
    val insuranceCost: Long,
    val subtotal: Long,
    val taxAmount: Long,
    val totalPrice: Long
) {
    
    /**
     * برگشاع به فرمت فارسی
     */
    fun getFormattedBreakdown(): FormattedPriceBreakdown {
        return FormattedPriceBreakdown(
            basePrice = PersianNumberFormatter.formatPrice(basePrice),
            discountAmount = PersianNumberFormatter.formatPrice(discountAmount),
            priceAfterDiscount = PersianNumberFormatter.formatPrice(priceAfterDiscount),
            shippingCost = PersianNumberFormatter.formatPrice(shippingCost),
            insuranceCost = PersianNumberFormatter.formatPrice(insuranceCost),
            subtotal = PersianNumberFormatter.formatPrice(subtotal),
            taxAmount = PersianNumberFormatter.formatPrice(taxAmount),
            totalPrice = PersianNumberFormatter.formatPrice(totalPrice)
        )
    }
}

/**
 * برگشاع فرمت‌شده (برای UI)
 */
data class FormattedPriceBreakdown(
    val basePrice: String,
    val discountAmount: String,
    val priceAfterDiscount: String,
    val shippingCost: String,
    val insuranceCost: String,
    val subtotal: String,
    val taxAmount: String,
    val totalPrice: String
)

/**
 * رکورد مالیات (برای حفظ رکورد)
 */
data class TaxRecord(
    val orderId: String,
    val taxAmount: Long,
    val taxRate: Float,
    val subtotal: Long,
    val createdAt: Long,
    val description: String = "مالیات بر سفارش"
)

/**
 * رکورد قیمت نقره و طلا
 */
data class CommodityPrice(
    val commodity: String,      // نقره، طلا، مس
    val pricePerGram: Long,
    val lastUpdated: Long,
    val source: String         // وای اطلاع رسان
)

/**
 * رویه محاسبه قیمتها برای دفترچه (محاسبان)
 */
object TaxCompliance {
    
    // معایيرهای ایرانی
    const val STANDARD_TAX_RATE = 0.09f  // 9% VAT
    const val CURRENCY = "TMN"          // تومان
    const val CURRENCY_SUBUNIT = 1      // بدون Rial
    
    /**
     * بررسی تطابق عراقه مالیات
     */
    fun isCompliant(orderCalculation: OrderCalculation): Boolean {
        // مالیات باید 9% باشد
        if (orderCalculation.taxRate != STANDARD_TAX_RATE) {
            return false
        }
        
        // قیمت‌ها باید با تومان باشند
        if (orderCalculation.basePrice < 0 || 
            orderCalculation.shippingCost < 0 || 
            orderCalculation.insuranceCost < 0) {
            return false
        }
        
        return true
    }
    
    /**
     * تولید مستند مالیات
     */
    fun generateTaxRecord(
        orderId: String,
        orderCalculation: OrderCalculation
    ): TaxRecord {
        return TaxRecord(
            orderId = orderId,
            taxAmount = orderCalculation.taxAmount,
            taxRate = orderCalculation.taxRate,
            subtotal = orderCalculation.subtotal,
            createdAt = System.currentTimeMillis()
        )
    }
}
