package com.noghre.sod.core.ext

import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Extension functions for money/price handling in jewelry commerce.
 * 
 * Supports Iranian currency (Rial) and jewelry pricing.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */

/**
 * Formats price with currency symbol and locale.
 * 
 * Example: 50000.formatPrice() = "۵۰,۰۰۰ ریال"
 * 
 * @param showCurrency Include currency symbol
 * @return Formatted price string
 */
fun Double.formatPrice(showCurrency: Boolean = true): String {
    val persianLocale = Locale("fa", "IR")
    val formatter = NumberFormat.getInstance(persianLocale).apply {
        isGroupingUsed = true
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }
    
    val formatted = formatter.format(this.toLong())
    return if (showCurrency) "$formatted ریال" else formatted
}

/**
 * Formats price with decimal places for display.
 * 
 * Example: 50000.55.formatPriceWithDecimals() = "۵۰,۰۰۰.55 ریال"
 * 
 * @param decimals Number of decimal places
 * @return Formatted price string
 */
fun Double.formatPriceWithDecimals(decimals: Int = 2): String {
    val persianLocale = Locale("fa", "IR")
    val formatter = NumberFormat.getInstance(persianLocale).apply {
        isGroupingUsed = true
        minimumFractionDigits = decimals
        maximumFractionDigits = decimals
    }
    
    val formatted = formatter.format(this)
    return "$formatted ریال"
}

/**
 * Formats price as weight-based (grams).
 * 
 * Used for silver jewelry pricing.
 * Example: 15.5.formatByGram() = "۱۵.5 لی (per gram)"
 * 
 * @return Formatted weight-price string
 */
fun Double.formatByGram(): String {
    return "${this.toPersianNumbers()} گرم (بر هر گرم)"
}

/**
 * Converts price to Rials with proper decimal handling.
 * 
 * @return Price in Rials as Long
 */
fun Double.toRials(): Long {
    return (this * 10).roundToInt() / 10L
}

/**
 * Parses price string to double value.
 * 
 * Handles Persian numbers and currency symbols.
 * Example: "۵۰,۰۰۰ ریال".parsePrice() = 50000.0
 * 
 * @return Numeric price value
 */
fun String.parsePrice(): Double {
    val cleanPrice = this
        .replace("ریال", "")
        .replace(",", "")
        .toEnglishNumbers()
        .trim()
    
    return cleanPrice.toDoubleOrNull() ?: 0.0
}

/**
 * Calculates discount amount from original price.
 * 
 * @param discountPercent Discount percentage (0-100)
 * @return Discount amount
 */
fun Double.calculateDiscount(discountPercent: Double): Double {
    return this * (discountPercent / 100)
}

/**
 * Calculates final price after discount.
 * 
 * @param discountPercent Discount percentage
 * @return Price after discount
 */
fun Double.applyDiscount(discountPercent: Double): Double {
    return this - this.calculateDiscount(discountPercent)
}

/**
 * Calculates tax amount (VAT).
 * 
 * Default Iranian VAT rate: 9%
 * 
 * @param taxPercent Tax percentage (default 9%)
 * @return Tax amount
 */
fun Double.calculateTax(taxPercent: Double = 9.0): Double {
    return this * (taxPercent / 100)
}

/**
 * Calculates final price including tax.
 * 
 * @param taxPercent Tax percentage
 * @return Price including tax
 */
fun Double.applyTax(taxPercent: Double = 9.0): Double {
    return this + this.calculateTax(taxPercent)
}

/**
 * Calculates total cost with discount and tax.
 * 
 * @param discountPercent Discount percentage
 * @param taxPercent Tax percentage
 * @return Final total price
 */
fun Double.calculateTotal(discountPercent: Double, taxPercent: Double = 9.0): Double {
    val afterDiscount = this.applyDiscount(discountPercent)
    return afterDiscount.applyTax(taxPercent)
}

/**
 * Checks if price qualifies for free shipping (silver threshold).
 * 
 * Free shipping for orders over 500,000 IRR
 * 
 * @return true if order is eligible for free shipping
 */
fun Double.isEligibleForFreeShipping(threshold: Double = 500000.0): Boolean {
    return this >= threshold
}

/**
 * Formats price as jewelry weight (grams) with price per gram.
 * 
 * @param weightGrams Weight in grams
 * @param pricePerGram Price per gram
 * @return Formatted display string
 */
fun Double.formatJewelryPrice(weightGrams: Double, pricePerGram: Double): String {
    val total = weightGrams * pricePerGram
    val formattedWeight = weightGrams.toPersianNumbers()
    val formattedTotal = total.formatPrice()
    
    return "$formattedWeight گرم @ $formattedTotal"
}

/**
 * Converts large numbers to compact notation.
 * 
 * Example: 1500000.toCompactNotation() = "1.5 میلیون"
 * 
 * @return Compact notation string
 */
fun Double.toCompactNotation(): String {
    return when {
        this >= 1_000_000 -> {
            val millions = this / 1_000_000
            "${String.format("%.1f", millions).replace(",", ".")} میلیون"
        }
        this >= 1_000 -> {
            val thousands = this / 1_000
            "${String.format("%.1f", thousands).replace(",", ".")} هزار"
        }
        else -> this.formatPrice()
    }
}
