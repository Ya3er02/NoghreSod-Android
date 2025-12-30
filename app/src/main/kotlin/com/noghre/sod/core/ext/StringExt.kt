package com.noghre.sod.core.ext

import java.text.Normalizer

/**
 * Extension functions for String manipulation with Persian language support.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */

/**
 * Checks if string contains only Persian characters.
 * 
 * @return true if string is valid Persian text
 */
fun String.isPersianWord(): Boolean {
    val persianPattern = "[\u0600-\u06FF]+".toRegex()
    return this.matches(persianPattern)
}

/**
 * Converts ASCII digits to Persian digits.
 * 
 * Example: "12345" becomes "۱۲۳۴۵"
 * 
 * @return String with Persian digits
 */
fun String.toPersianNumbers(): String {
    var result = this
    result = result.replace("0", "۰")
    result = result.replace("1", "۱")
    result = result.replace("2", "۲")
    result = result.replace("3", "۳")
    result = result.replace("4", "۴")
    result = result.replace("5", "۵")
    result = result.replace("6", "۶")
    result = result.replace("7", "۷")
    result = result.replace("8", "۸")
    result = result.replace("9", "۹")
    return result
}

/**
 * Converts Persian digits to ASCII digits.
 * 
 * Example: "۱۲۳۴۵" becomes "12345"
 * 
 * @return String with ASCII digits
 */
fun String.toEnglishNumbers(): String {
    var result = this
    result = result.replace("۰", "0")
    result = result.replace("۱", "1")
    result = result.replace("۲", "2")
    result = result.replace("۳", "3")
    result = result.replace("۴", "4")
    result = result.replace("۵", "5")
    result = result.replace("۶", "6")
    result = result.replace("۷", "7")
    result = result.replace("۸", "8")
    result = result.replace("۹", "9")
    return result
}

/**
 * Abbreviates string to specified length with ellipsis.
 * 
 * Example: "This is a long text".abbreviate(10) = "This is a ..."
 * 
 * @param maxLength Maximum length before abbreviation
 * @return Abbreviated string
 */
fun String.abbreviate(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.take(maxLength) + "..."
    } else {
        this
    }
}

/**
 * Validates Iranian IBAN format.
 * 
 * IBAN format: IR + 24 alphanumeric characters
 * Example: IR1234567890123456789012
 * 
 * @return true if valid Iranian IBAN
 */
fun String.isValidIRAN_IBAN(): Boolean {
    val ibanRegex = "^IR\\d{24}$".toRegex()
    return this.matches(ibanRegex)
}

/**
 * Validates mobile number format (Iranian).
 * 
 * Accepts formats:
 * - 09xxxxxxxxx (11 digits)
 * - 00989xxxxxxxxx (13 digits)
 * - +989xxxxxxxxx (12 digits)
 * 
 * @return true if valid Iranian mobile number
 */
fun String.isValidIranMobileNumber(): Boolean {
    val mobileRegex = "(^0?9\\d{9}$|^\\+989\\d{9}$|^00989\\d{9}$)".toRegex()
    return this.replace(" ", "").replace("-", "").matches(mobileRegex)
}

/**
 * Formats mobile number to standard Iranian format.
 * 
 * @return Formatted number (09xxxxxxxxx)
 */
fun String.formatIranMobileNumber(): String {
    val digits = this.replace(Regex("[^0-9]"), "")
    return when {
        digits.endsWith("9") && digits.length == 10 -> "0" + digits
        digits.startsWith("989") && digits.length == 12 -> "0" + digits.substring(2)
        digits.startsWith("989") && digits.length == 13 -> "0" + digits.substring(3)
        else -> digits
    }
}

/**
 * Normalizes text by removing diacritical marks.
 * 
 * @return Normalized string
 */
fun String.normalize(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
}

/**
 * Checks if string is a valid email address.
 * 
 * @return true if valid email format
 */
fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    return this.matches(emailRegex)
}

/**
 * Repeats string n times.
 * 
 * Example: "Hi".repeat(3) = "HiHiHi"
 * 
 * @param count Number of repetitions
 * @return Repeated string
 */
fun String.repeatString(count: Int): String {
    return this.repeat(count)
}

/**
 * Capitalizes first character (Persian-aware).
 * 
 * @return String with first character capitalized
 */
fun String.capitalizeFirst(): String {
    return if (this.isNotEmpty()) {
        this[0].uppercase() + this.substring(1)
    } else {
        this
    }
}
