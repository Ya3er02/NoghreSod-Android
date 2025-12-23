package com.noghre.sod.utils

/**
 * Check if string is empty or blank
 */
fun String?.isEmptyOrBlank(): Boolean {
    return this == null || this.isBlank()
}

/**
 * Safe substring
 */
fun String.safeSubstring(startIndex: Int, endIndex: Int): String {
    return try {
        this.substring(startIndex.coerceIn(0, length), endIndex.coerceIn(0, length))
    } catch (e: Exception) {
        this
    }
}

/**
 * Capitalize first character
 */
fun String.capitalizeFirstChar(): String {
    return if (this.isNotEmpty()) this[0].uppercase() + this.substring(1) else this
}

/**
 * Check if string is email
 */
fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    return this.matches(emailRegex)
}

/**
 * Check if string is valid phone number (Iranian format)
 */
fun String.isValidPhoneNumber(): Boolean {
    val phoneRegex = "^(\\+98|0)?9[0-9]{9}$".toRegex()
    return this.matches(phoneRegex)
}

/**
 * Remove special characters
 */
fun String.removeSpecialCharacters(): String {
    return this.replace(Regex("[^A-Za-z0-9]"), "")
}

/**
 * Truncate string with ellipsis
 */
fun String.truncateWithEllipsis(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.substring(0, maxLength - 3) + "..."
    } else {
        this
    }
}

/**
 * Reverse string
 */
fun String.reverse(): String {
    return this.reversed()
}
