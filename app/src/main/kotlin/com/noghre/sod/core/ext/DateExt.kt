package com.noghre.sod.core.ext

import java.util.*
import java.text.SimpleDateFormat
import android.text.format.DateUtils

/**
 * Extension functions for Date manipulation with Jalali calendar support.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */

/**
 * Formats date to Jalali (Persian) calendar format.
 * 
 * Pattern examples:
 * - "yyyy/MM/dd" -> "1402/10/09"
 * - "MMMM d, yyyy" -> "دی 9, 1402"
 * 
 * @param pattern Date format pattern
 * @return Formatted Jalali date string
 */
fun Date.toJalaliString(pattern: String = "yyyy/MM/dd"): String {
    val calendar = Calendar.getInstance().apply {
        time = this@toJalaliString
    }
    return formatJalali(calendar.get(Calendar.YEAR), 
                        calendar.get(Calendar.MONTH), 
                        calendar.get(Calendar.DAY_OF_MONTH))
}

/**
 * Converts Gregorian date to Jalali components (year, month, day).
 * 
 * @return Triple of (jalaliYear, jalaliMonth, jalaliDay)
 */
fun Date.toJalaliComponents(): Triple<Int, Int, Int> {
    val calendar = Calendar.getInstance().apply {
        time = this@toJalaliComponents
    }
    
    val gy = calendar.get(Calendar.YEAR)
    val gm = calendar.get(Calendar.MONTH) + 1
    val gd = calendar.get(Calendar.DAY_OF_MONTH)
    
    val g_d_n = 365 * gy + ((gy + 3) / 4) - ((gy + 99) / 100) + ((gy + 399) / 400) + gd
    
    val j_d_n = g_d_n - 79
    
    val j_np = j_d_n / 12053
    val j_d_n_rem = j_d_n % 12053
    
    val jy = 979 + 2820 * j_np + (1461 * j_d_n_rem) / 1029983
    
    val j_d_n_rem2 = j_d_n_rem % 1029983
    val jp = (j_d_n_rem2 / 365).toInt()
    val j_d_n_rem3 = j_d_n_rem2 % 365
    
    var jm = (6 * (jp + 1)).toInt()
    var jd = (j_d_n_rem3 + 1).toInt()
    
    if (jm > 12) jm = 12
    if (jd > daysInMonthJalali(jm, jy)) jd = daysInMonthJalali(jm, jy)
    
    return Triple(jy, jm, jd)
}

/**
 * Formats date with locale support.
 * 
 * @param pattern SimpleDateFormat pattern
 * @param locale Locale for formatting (default: Farsi/Persian)
 * @return Formatted date string
 */
fun Date.format(pattern: String, locale: Locale = Locale("fa", "IR")): String {
    val sdf = SimpleDateFormat(pattern, locale)
    return sdf.format(this)
}

/**
 * Checks if date is today.
 * 
 * @return true if date is today
 */
fun Date.isToday(): Boolean {
    val today = Calendar.getInstance()
    val date = Calendar.getInstance().apply { time = this@isToday }
    
    return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
           today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
}

/**
 * Checks if date is yesterday.
 * 
 * @return true if date is yesterday
 */
fun Date.isYesterday(): Boolean {
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val date = Calendar.getInstance().apply { time = this@isYesterday }
    
    return yesterday.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
           yesterday.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
}

/**
 * Calculates days between this date and another.
 * 
 * @param other Another date
 * @return Number of days difference (positive if other is in future)
 */
fun Date.daysBetween(other: Date): Long {
    return (other.time - this.time) / (1000 * 60 * 60 * 24)
}

/**
 * Gets relative time string (e.g., "2 hours ago", "in 3 days").
 * 
 * @param context Android context for resources
 * @return Relative time string
 */
fun Date.getRelativeTimeString(): String {
    return DateUtils.getRelativeTimeSpanString(
        this.time,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()
}

/**
 * Gets human-readable date string for Persian UI.
 * 
 * @return Display string (e.g., "امروز", "دیروز", "۲ روز پیش")
 */
fun Date.getPersianDisplayDate(): String {
    return when {
        this.isToday() -> "امروز"
        this.isYesterday() -> "دیروز"
        else -> {
            val days = -this.daysBetween(Date())
            when {
                days < 30 -> "$days روز پیش"
                days < 365 -> "${days / 30} ماه پیش"
                else -> "${days / 365} سال پیش"
            }
        }
    }
}

// Private helper functions

private fun formatJalali(jy: Int, jm: Int, jd: Int): String {
    val gy = jy + 1600
    val gm = jm + 8
    val gd = jd
    
    var leaps = ((gy - 1600) / 4) - ((gy - 1600) / 100) + ((gy - 1600) / 400)
    var days = 0
    for (i in 1600 until gy) {
        days += if (i % 400 == 0 || (i % 4 == 0 && i % 100 != 0)) 366 else 365
    }
    
    return String.format("%04d/%02d/%02d", jy, jm, jd)
}

private fun daysInMonthJalali(month: Int, year: Int): Int {
    return if (month <= 6) 31 else if (month <= 11) 30 else if ((year + 1346) % 33 % 4 == 1 && (year + 1346) % 33 == 1) 30 else 29
}
