package com.noghre.sod.core.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Utility class for Persian/Iranian localization
 * Handles digit conversion, price formatting, phone validation, etc.
 */
object PersianUtils {
    
    private val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    
    /**
     * Convert English digits to Persian digits
     * Example: "123" -> "۱۲۳"
     */
    fun toPersianDigits(input: String): String {
        val builder = StringBuilder()
        for (char in input) {
            if (char.isDigit()) {
                builder.append(persianDigits[char.toString().toInt()])
            } else {
                builder.append(char)
            }
        }
        return builder.toString()
    }
    
    /**
     * Convert Persian digits to English digits
     * Example: "۱۲۳" -> "123"
     */
    fun toEnglishDigits(input: String): String {
        var result = input
        for (i in persianDigits.indices) {
            result = result.replace(persianDigits[i], ('0' + i))
        }
        return result
    }
    
    /**
     * Format price in Toman (Iranian currency)
     * Example: 1500000 -> "۱,۵۰۰,۰۰۰ تومان"
     */
    fun formatPrice(price: Double, usePersianDigits: Boolean = true): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        symbols.groupingSeparator = ','
        
        val formatter = DecimalFormat("#,###", symbols)
        val formatted = formatter.format(price.toLong())
        
        return if (usePersianDigits) {
            "${toPersianDigits(formatted)} تومان"
        } else {
            "$formatted تومان"
        }
    }
    
    /**
     * Format weight in grams
     * Example: 25.5 -> "۲۵.۵ گرم"
     */
    fun formatWeight(grams: Double, usePersianDigits: Boolean = true): String {
        val formatted = String.format(Locale.US, "%.1f", grams)
        return if (usePersianDigits) {
            "${toPersianDigits(formatted)} گرم"
        } else {
            "$formatted گرم"
        }
    }
    
    /**
     * Format phone number (Iranian)
     * Example: "09123456789" -> "۰۹۱۲ ۳۴۵ ۶۷۸۹"
     */
    fun formatPhoneNumber(phone: String, usePersianDigits: Boolean = true): String {
        val cleaned = phone.replace(Regex("[^0-9]"), "")
        
        if (cleaned.length != 11 || !cleaned.startsWith("09")) {
            return phone
        }
        
        val formatted = "${cleaned.substring(0, 4)} ${cleaned.substring(4, 7)} ${cleaned.substring(7)}"
        
        return if (usePersianDigits) {
            toPersianDigits(formatted)
        } else {
            formatted
        }
    }
    
    /**
     * Validate Iranian phone number
     * Format: 09XX-XXXXXXX (11 digits starting with 09)
     */
    fun isValidIranianPhone(phone: String): Boolean {
        val cleaned = phone.replace(Regex("[^0-9]"), "")
        return cleaned.matches(Regex("^09[0-9]{9}$"))
    }
    
    /**
     * Validate Iranian postal code
     * Format: 10 digits
     */
    fun isValidPostalCode(postalCode: String): Boolean {
        val cleaned = postalCode.replace(Regex("[^0-9]"), "")
        return cleaned.matches(Regex("^[0-9]{10}$"))
    }
    
    /**
     * Format Iranian postal code
     * Example: "1234567890" -> "۱۲۳۴۵-۶۷۸۹۰"
     */
    fun formatPostalCode(postalCode: String, usePersianDigits: Boolean = true): String {
        val cleaned = postalCode.replace(Regex("[^0-9]"), "")
        
        if (cleaned.length != 10) {
            return postalCode
        }
        
        val formatted = "${cleaned.substring(0, 5)}-${cleaned.substring(5)}"
        
        return if (usePersianDigits) {
            toPersianDigits(formatted)
        } else {
            formatted
        }
    }
    
    /**
     * Get Persian date/time string
     * TODO: Integrate with Persian Calendar library
     */
    fun getPersianDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale.US)
        return sdf.format(java.util.Date(timestamp))
    }
}

/**
 * Extension functions for easy usage
 */
fun String.toPersianDigits(): String = PersianUtils.toPersianDigits(this)
fun String.toEnglishDigits(): String = PersianUtils.toEnglishDigits(this)
fun Double.toPersianPrice(): String = PersianUtils.formatPrice(this)
fun Double.toPersianWeight(): String = PersianUtils.formatWeight(this)
fun String.formatIranianPhone(): String = PersianUtils.formatPhoneNumber(this)
fun String.isValidIranianPhone(): Boolean = PersianUtils.isValidIranianPhone(this)
fun String.formatIranianPostalCode(): String = PersianUtils.formatPostalCode(this)
fun String.isValidIranianPostalCode(): Boolean = PersianUtils.isValidPostalCode(this)