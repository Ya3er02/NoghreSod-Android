package com.noghre.sod.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Format number as currency (Persian format)
 */
fun Double.formatAsCurrency(currencySymbol: String = "ريال"): String {
    val symbols = DecimalFormatSymbols(Locale("fa", "IR")).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }
    val formatter = DecimalFormat("#,##0", symbols)
    return "${formatter.format(this)} $currencySymbol"
}

/**
 * Format number as currency with decimal places
 */
fun Double.formatAsCurrencyWithDecimals(decimals: Int = 2, currencySymbol: String = "ريال"): String {
    val symbols = DecimalFormatSymbols(Locale("fa", "IR")).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }
    val pattern = "#,##0." + "0".repeat(decimals)
    val formatter = DecimalFormat(pattern, symbols)
    return "${formatter.format(this)} $currencySymbol"
}

/**
 * Format number with thousands separator
 */
fun Long.formatWithSeparator(): String {
    val formatter = DecimalFormat("#,###")
    return formatter.format(this)
}

/**
 * Check if number is in valid range
 */
fun Double.isInRange(min: Double = 0.0, max: Double = Double.MAX_VALUE): Boolean {
    return this in min..max
}

/**
 * Round to specific decimal places
 */
fun Double.roundTo(decimals: Int): Double {
    val multiplier = Math.pow(10.0, decimals.toDouble())
    return Math.round(this * multiplier) / multiplier
}
