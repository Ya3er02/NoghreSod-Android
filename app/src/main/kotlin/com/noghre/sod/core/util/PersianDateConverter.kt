package com.noghre.sod.core.util

import java.util.Calendar

/**
 * مبدل تاریخ میلادی به جلالی (خورشیدی)
 * برای استفاده در طول برنامه
 */
object PersianDateConverter {
    
    // تقویم جلالی - نقاط شکست
    private val jy_breaks = intArrayOf(
        -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
        1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178
    )
    
    /**
     * تبدیل تاریخ میلادی به خورشیدی
     * @param gregorian تاریخ میلادی
     * @return Triple(سال, ماه, روز)
     */
    fun toJalali(gregorian: Calendar): Triple<Int, Int, Int> {
        val gy = gregorian.get(Calendar.YEAR)
        val gm = gregorian.get(Calendar.MONTH) + 1
        val gd = gregorian.get(Calendar.DAY_OF_MONTH)
        
        return gregorianToJalali(gy, gm, gd)
    }
    
    /**
     * تبدیل اعداد انگلیسی به فارسی
     * 2024 → ۲۰۲۴
     */
    fun toFarsiNumbers(input: String): String {
        val farsiDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
        var output = input
        
        for (i in 0..9) {
            output = output.replace(i.toString(), farsiDigits[i])
        }
        return output
    }
    
    /**
     * تبدیل تاریخ به فرمت "۱۴۰۳/۱۰/۰۶"
     */
    fun toPersianDateString(jy: Int, jm: Int, jd: Int): String {
        val formattedMonth = if (jm < 10) "0$jm" else "$jm"
        val formattedDay = if (jd < 10) "0$jd" else "$jd"
        val dateString = "$jy/$formattedMonth/$formattedDay"
        return toFarsiNumbers(dateString)
    }
    
    /**
     * تبدیل تاریخ به متن فارسی
     * مثال: "دوشنبه، ۶ دی ۱۴۰۳"
     */
    fun toPersianText(jy: Int, jm: Int, jd: Int): String {
        val dayName = getDayName(jy, jm, jd)
        val monthName = getMonthName(jm)
        val farsiDay = toFarsiNumbers("$jd")
        val farsiYear = toFarsiNumbers("$jy")
        
        return "$dayName، $farsiDay $monthName $farsiYear"
    }
    
    /**
     * نام روزهای هفته
     */
    private fun getDayName(jy: Int, jm: Int, jd: Int): String {
        val days = arrayOf(
            "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه",
            "چهارشنبه", "پنج‌شنبه", "جمعه"
        )
        
        val dayOfWeek = calculateDayOfWeek(jy, jm, jd)
        return days.getOrNull(dayOfWeek) ?: ""
    }
    
    /**
     * نام ماه‌های سال خورشیدی
     */
    private fun getMonthName(jm: Int): String {
        val months = arrayOf(
            "", "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
        )
        return months.getOrNull(jm) ?: ""
    }
    
    /**
     * الگوریتم تبدیل میلادی به خورشیدی
     */
    private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gy2 = if (gm > 2) gy + 1 else gy
        val days = 365 * gy + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400)
            .also {
                val dm = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
                return@also it + dm[gm - 1] + gd
            }
        
        var jy = -1595 + 33 * (days / 12053) + 4 * ((days % 12053) / 1461)
        var g_d_n = days - 365 * jy - ((jy + 3) / 4) + ((jy + 99) / 100) - ((jy + 399) / 400)
        
        for (i in jy_breaks.indices) {
            var jm = 0
            var jump = 0
            
            if (i == 0) {
                jump = jy_breaks[0]
            } else if (jy < jy_breaks[i]) {
                jump = jy_breaks[i - 1]
            }
            
            if (jy < jy_breaks[i]) break
            
            jy += 33 * (((days - 365 * jy - ((jy + 3) / 4) + ((jy + 99) / 100) - ((jy + 399) / 400))) / 12053)
        }
        
        g_d_n = days - 365 * jy - ((jy + 3) / 4) + ((jy + 99) / 100) - ((jy + 399) / 400)
        
        var jm = 0
        if (g_d_n < 186) {
            jm = 1 + g_d_n / 31
        } else {
            jm = 7 + (g_d_n - 186) / 30
        }
        
        val jd = 1 + (if (g_d_n < 186) g_d_n % 31 else (g_d_n - 186) % 30)
        
        return Triple(jy, jm, jd)
    }
    
    /**
     * محاسبه روز هفته (برای بدستالی)
     */
    private fun calculateDayOfWeek(jy: Int, jm: Int, jd: Int): Int {
        val a = ((14 - jm) / 12)
        val y = jy + 4800 - a
        val m = jm + 12 * a - 3
        return (jd + ((153 * m + 2) / 5) + 365 * y + (y / 4) - (y / 100) + (y / 400)) % 7
    }
}
