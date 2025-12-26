package com.noghre.sod.presentation.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.noghre.sod.R

/**
 * Comprehensive Persian/Farsi localization support.
 * Provides RTL support, Persian-specific fonts, and localized strings.
 *
 * Features:
 * - Full RTL (Right-to-Left) layout support
 * - Persian number formatting (۰۱۲۳۴۵۶۷۸۹)
 * - Persian calendar support
 * - Localized date/time formatting
 * - Currency formatting for IRR
 * - Persian-specific typography
 *
 * @author Yaser
 * @version 1.0.0
 */

// ==================== PERSIAN TYPOGRAPHY ====================

/**
 * Iranian Sans font family for Persian text.
 * Optimized for readability in Persian language.
 */
val PersianFontFamily = FontFamily(
    // Light
    androidx.compose.ui.text.font.Font(R.font.iransans_light, FontWeight.Light),
    // Regular
    androidx.compose.ui.text.font.Font(R.font.iransans_regular, FontWeight.Normal),
    // Medium
    androidx.compose.ui.text.font.Font(R.font.iransans_medium, FontWeight.Medium),
    // Bold
    androidx.compose.ui.text.font.Font(R.font.iransans_bold, FontWeight.Bold),
    // ExtraBold
    androidx.compose.ui.text.font.Font(R.font.iransans_extrabold, FontWeight.ExtraBold)
)

/**
 * Localization context for Persian language support.
 */
data class PersianLocalizationContext(
    val fontFamily: FontFamily = PersianFontFamily,
    val usePersinNumbers: Boolean = true,
    val usePersianCalendar: Boolean = true,
    val currencySymbol: String = "ریال",
    val currencyCode: String = "IRR"
)

val LocalPersianLocalization = staticCompositionLocalOf<PersianLocalizationContext> {
    PersianLocalizationContext()
}

// ==================== PERSIAN NUMBER CONVERSION ====================

/**
 * Persian digits (۰-۹) for number display.
 */
object PersianNumbers {
    private val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    private val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    private val englishDigits = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    
    /**
     * Convert English numbers to Persian (۰-۹).
     */
    fun toPersian(input: String): String {
        var result = input
        for (i in 0..9) {
            result = result.replace(englishDigits[i], persianDigits[i])
            result = result.replace(arabicDigits[i], persianDigits[i])
        }
        return result
    }
    
    /**
     * Convert Persian numbers to English.
     */
    fun toEnglish(input: String): String {
        var result = input
        for (i in 0..9) {
            result = result.replace(persianDigits[i], englishDigits[i])
            result = result.replace(arabicDigits[i], englishDigits[i])
        }
        return result
    }
}

// ==================== PERSIAN FORMATTING ====================

object PersianFormatter {
    
    /**
     * Format currency for Iranian Rial (IRR).
     * Example: 1234567 → "۱٬۲۳۴٬۵۶۷ ریال"
     */
    fun formatCurrency(amount: Long, usePersianNumbers: Boolean = true): String {
        val formatted = String.format("%,d", amount).replace(",", "٬")
        val withSymbol = "$formatted ریال"
        return if (usePersianNumbers) {
            PersianNumbers.toPersian(withSymbol)
        } else {
            withSymbol
        }
    }
    
    /**
     * Format decimal price.
     * Example: 1234.56 → "۱٬۲۳۴٫۵۶"
     */
    fun formatPrice(price: Double, usePersianNumbers: Boolean = true): String {
        val formatted = String.format("%,.2f", price).replace(",", "٬")
        return if (usePersianNumbers) {
            PersianNumbers.toPersian(formatted)
        } else {
            formatted
        }
    }
    
    /**
     * Format phone number.
     * Example: "989123456789" → "0912 345 6789"
     */
    fun formatPhoneNumber(phone: String, usePersianNumbers: Boolean = true): String {
        val cleaned = phone.replace(Regex("[^0-9]"), "")
        val formatted = when {
            cleaned.length == 11 && cleaned.startsWith("98") -> {
                val withoutCountry = cleaned.substring(2)
                "0${withoutCountry.substring(0, 3)} ${withoutCountry.substring(3, 6)} ${withoutCountry.substring(6)}"
            }
            cleaned.length == 11 -> "0${cleaned.substring(1, 4)} ${cleaned.substring(4, 7)} ${cleaned.substring(7)}"
            cleaned.length == 10 -> "${cleaned.substring(0, 3)} ${cleaned.substring(3, 6)} ${cleaned.substring(6)}"
            else -> phone
        }
        return if (usePersianNumbers) {
            PersianNumbers.toPersian(formatted)
        } else {
            formatted
        }
    }
    
    /**
     * Format postal code.
     * Example: "1234567890" → "12345-67890"
     */
    fun formatPostalCode(postalCode: String, usePersianNumbers: Boolean = true): String {
        val cleaned = postalCode.replace(Regex("[^0-9]"), "")
        val formatted = when {
            cleaned.length == 10 -> "${cleaned.substring(0, 5)}-${cleaned.substring(5)}"
            else -> postalCode
        }
        return if (usePersianNumbers) {
            PersianNumbers.toPersian(formatted)
        } else {
            formatted
        }
    }
    
    /**
     * Format date in Persian format.
     * Example: 2025-12-26 → "۲۵ دی ۱۴۰۴"
     */
    fun formatPersianDate(
        day: Int,
        monthPersianName: String,
        year: Int,
        usePersianNumbers: Boolean = true
    ): String {
        val formatted = "$day $monthPersianName $year"
        return if (usePersianNumbers) {
            PersianNumbers.toPersian(formatted)
        } else {
            formatted
        }
    }
}

// ==================== PERSIAN CALENDAR ====================

/**
 * Persian/Jalali calendar support.
 * Converts Gregorian dates to Persian calendar dates.
 */
object PersianCalendar {
    
    private val persianMonths = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )
    
    /**
     * Get Persian month name from month number (1-12).
     */
    fun getPersianMonthName(month: Int): String {
        return if (month in 1..12) {
            persianMonths[month - 1]
        } else {
            ""
        }
    }
    
    /**
     * Convert Gregorian date to Persian date.
     * Returns triplet (year, month, day) in Persian calendar.
     */
    fun gregorianToPersian(gYear: Int, gMonth: Int, gDay: Int): Triple<Int, Int, Int> {
        val gy = gYear - 1600
        val gm = gMonth - 1
        val gd = gDay - 1
        
        val g_day_no = 365 * gy + ((gy + 3) / 4) - ((gy + 99) / 100) + ((gy + 399) / 400) +
                gd + (if (gm > 1) 31 else 0) + (if (gm > 2) 28 else 0) +
                (if (gm > 3) 31 else 0) + (if (gm > 4) 30 else 0) +
                (if (gm > 5) 31 else 0) + (if (gm > 6) 30 else 0) +
                (if (gm > 7) 31 else 0) + (if (gm > 8) 31 else 0) +
                (if (gm > 9) 30 else 0) + (if (gm > 10) 31 else 0) +
                (if (gm > 11) 30 else 0)
        
        val j_day_no = g_day_no - 79
        var j_np = j_day_no / 12053
        j_day_no %= 12053
        var jy = 979 + 33 * j_np + 4 * (j_day_no / 1461)
        j_day_no %= 1461
        
        if (j_day_no >= 366) {
            jy += (j_day_no - 1) / 365
            j_day_no = (j_day_no - 1) % 365
        }
        
        val jm = if (j_day_no < 186) (j_day_no / 31) + 1 else (j_day_no - 186) / 30 + 7
        val jd = (if (j_day_no < 186) (j_day_no % 31) else (j_day_no - 186) % 30) + 1
        
        return Triple(jy, jm, jd)
    }
}

// ==================== COMPOSABLE EXTENSIONS ====================

/**
 * Display price with Persian formatting.
 */
@Composable
fun PersianPriceText(
    price: Long,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    usePersianNumbers: Boolean = true
) {
    androidx.compose.material3.Text(
        text = PersianFormatter.formatCurrency(price, usePersianNumbers),
        style = style.copy(
            fontFamily = PersianFontFamily,
            fontSize = 16.sp
        )
    )
}

/**
 * Display formatted date in Persian.
 */
@Composable
fun PersianDateText(
    day: Int,
    month: Int,
    year: Int,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    val monthName = PersianCalendar.getPersianMonthName(month)
    val dateText = PersianFormatter.formatPersianDate(day, monthName, year)
    
    androidx.compose.material3.Text(
        text = dateText,
        style = style.copy(fontFamily = PersianFontFamily)
    )
}
