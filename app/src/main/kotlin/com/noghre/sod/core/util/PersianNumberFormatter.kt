package com.noghre.sod.core.util

import com.noghre.sod.domain.model.Price
import com.noghre.sod.domain.model.Toman

/**
 * Formatter for converting numbers to Persian (Farsi) representation.
 * 
 * Handles:
 * - Arabic numerals (0-9) → Persian digits (۰-۹)
 * - Thousands separator (Persian comma: ٬)
 * - Negative number sign (Persian minus: −)
 * 
 * Example:
 * - 1234567 → ۱٬۲۳۴٬۵۶۷
 * - -500 → −۵۰۰
 */
object PersianNumberFormatter {
    
    // Persian digit mapping (Farsi digits)
    private val persianDigits = arrayOf(
        '۰',  // 0 → ۰
        '۱',  // 1 → ۱
        '۲',  // 2 → ۲
        '۳',  // 3 → ۳
        '۴',  // 4 → ۴
        '۵',  // 5 → ۵
        '۶',  // 6 → ۶
        '۷',  // 7 → ۷
        '۸',  // 8 → ۸
        '۹'   // 9 → ۹
    )
    
    // Persian thousands separator (comma in Persian: ٬)
    private const val PERSIAN_COMMA = '٬'  // Persian separator for thousands
    
    // Persian minus sign
    private const val PERSIAN_MINUS = '−'   // Unicode minus sign
    
    /**
     * Format a number in Persian.
     * 
     * @param value Number to format
     * @return Formatted string with Persian digits and separators
     * 
     * Examples:
     * - 0 → "۰"
     * - 123 → "۱۲۳"
     * - 1234 → "۱٬۲۳۴"
     * - 1234567 → "۱٬۲۳۴٬۵۶۷"
     * - -500 → "−۵۰۰"
     */
    fun format(value: Long): String {
        if (value == 0L) return persianDigits[0].toString()
        
        val isNegative = value < 0
        val absValue = if (isNegative) -value else value
        
        // Convert to string and split from right to left in groups of 3
        val stringValue = absValue.toString()
        val parts = stringValue.reversed().chunked(3).reversed()
        
        // Format each part and rejoin with Persian comma
        val formattedParts = parts.map { part ->
            part.reversed().map { digit ->
                persianDigits[digit.toString().toInt()]
            }.joinToString("")
        }
        
        var result = formattedParts.joinToString(PERSIAN_COMMA.toString())
        
        // Add Persian minus sign if negative
        if (isNegative) {
            result = "$PERSIAN_MINUS$result"
        }
        
        return result
    }
    
    /**
     * Format a Toman amount with currency label.
     * 
     * @param toman Toman value
     * @return Formatted string with Persian digits and "تومان" label
     * 
     * Example: Toman(123456) → "۱٬۲۳۴٬۵۶ تومان"
     */
    fun formatTomanPrice(toman: Toman): String {
        return "${format(toman.value)} تومان"
    }
    
    /**
     * Format a Price object for display.
     * 
     * @param price Price object containing amount and display format
     * @return Formatted price string with Persian numerals
     */
    fun formatPrice(price: Price): String {
        return formatTomanPrice(price.amount)
    }
    
    /**
     * Format percentage for display.
     * 
     * @param percent Percentage value (0-100)
     * @return Formatted percentage string
     * 
     * Example: 15 → "۱۵%"
     */
    fun formatPercent(percent: Int): String {
        return "${format(percent.toLong())}%"
    }
    
    /**
     * Format quantity (for shopping cart items).
     * 
     * @param quantity Item quantity
     * @return Formatted quantity string with Persian digits
     * 
     * Example: 5 → "۵"
     */
    fun formatQuantity(quantity: Int): String {
        return format(quantity.toLong())
    }
    
    /**
     * Convert individual digit to Persian.
     * 
     * @param digit Single digit (0-9)
     * @return Persian digit character
     */
    fun formatDigit(digit: Int): Char {
        require(digit in 0..9) { "Digit must be 0-9, got: $digit" }
        return persianDigits[digit]
    }
    
    /**
     * Format date numbers for Persian calendar.
     * 
     * @param year Jalali year (e.g., 1404)
     * @param month Jalali month (1-12)
     * @param day Jalali day (1-31)
     * @return Formatted date string
     * 
     * Example: (1404, 10, 8) → "۱۴۰۴/۱۰/۰۸"
     */
    fun formatJalaliDate(year: Int, month: Int, day: Int): String {
        val yearStr = format(year.toLong())
        val monthStr = String.format("%02d", month).map { persianDigits[it.toString().toInt()] }.joinToString("")
        val dayStr = String.format("%02d", day).map { persianDigits[it.toString().toInt()] }.joinToString("")
        return "$yearStr/$monthStr/$dayStr"
    }
}