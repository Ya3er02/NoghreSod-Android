package com.noghre.sod.core.util

import org.junit.Test
import kotlin.test.assertEquals
import java.util.Calendar

class PersianDateConverterTest {
    
    @Test
    fun testGregorianToJalaliConversion() {
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.DECEMBER, 28)  // 28 Dec 2024 = 7 Dey 1403
        }
        
        val (jy, jm, jd) = PersianDateConverter.toJalali(calendar)
        
        assertEquals(1403, jy)
        assertEquals(10, jm)  // Dey
        assertEquals(7, jd)
    }
    
    @Test
    fun testPersianDateString() {
        val dateString = PersianDateConverter.toPersianDateString(1403, 10, 7)
        assertEquals("۱۴۰۳/۱۰/۰۷", dateString)
    }
    
    @Test
    fun testToFarsiNumbers() {
        assertEquals("۰", PersianDateConverter.toFarsiNumbers("0"))
        assertEquals("۱۲۳", PersianDateConverter.toFarsiNumbers("123"))
        assertEquals("۱۴۰۳", PersianDateConverter.toFarsiNumbers("1403"))
        assertEquals("۲۰۲۴", PersianDateConverter.toFarsiNumbers("2024"))
    }
    
    @Test
    fun testPersianTextFormat() {
        val text = PersianDateConverter.toPersianText(1403, 10, 7)
        // Should contain day name, day, month name, and year in Persian
        assert(text.contains("۱۴۰۳"))  // Year
        assert(text.contains("دی"))  // Month (Dey)
    }
    
    @Test
    fun testJanuaryConversion() {
        // 1 January 2024 = 12 Dey 1402
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.JANUARY, 1)
        }
        
        val (jy, jm, jd) = PersianDateConverter.toJalali(calendar)
        assertEquals(1402, jy)
        assertEquals(10, jm)
    }
    
    @Test
    fun testLeapYearHandling() {
        // 29 February 2024 (leap year) = 10 Esfand 1402
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.FEBRUARY, 29)
        }
        
        val (jy, jm, jd) = PersianDateConverter.toJalali(calendar)
        assertEquals(1402, jy)
    }
    
    @Test
    fun testNowahNozanConversion() {
        // 20 March 2024 (Persian New Year) = 1 Farvardin 1403
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.MARCH, 20)
        }
        
        val (jy, jm, jd) = PersianDateConverter.toJalali(calendar)
        assertEquals(1403, jy)
        assertEquals(1, jm)  // Farvardin
        assertEquals(1, jd)
    }
}
