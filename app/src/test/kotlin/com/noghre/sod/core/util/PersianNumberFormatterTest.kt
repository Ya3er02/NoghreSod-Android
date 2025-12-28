package com.noghre.sod.core.util

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PersianNumberFormatterTest {
    
    @Test
    fun testFormatPrice() {
        val result = PersianNumberFormatter.formatPrice(850000)
        assertEquals("۸۵۰٬۰۰۰ تومان", result)
    }
    
    @Test
    fun testFormatPriceWithoutCurrency() {
        val result = PersianNumberFormatter.formatPriceWithoutCurrency(1250000)
        assertEquals("۱٬۲۵۰٬۰۰۰", result)
    }
    
    @Test
    fun testFormatPhoneNumber() {
        val result = PersianNumberFormatter.formatPhoneNumber("09101234567")
        assertTrue(result.contains("۰۹۱۰"))  // 0910 in Persian
        assertTrue(result.contains("۳۴۵۶"))  // 3456 in Persian
    }
    
    @Test
    fun testFormatPhoneNumberWithPlus98() {
        val result = PersianNumberFormatter.formatPhoneNumber("+989101234567")
        assertTrue(result.contains("۰۹۱۰"))  // Should convert to 0910
    }
    
    @Test
    fun testFormatCardNumber() {
        val result = PersianNumberFormatter.formatCardNumber("6037697126661")
        assertTrue(result.contains("۶۰۳۷۶۹"))  // First 6 digits
        assertTrue(result.contains("۶۶۶۱"))  // Last 4 digits
        assertTrue(result.contains("***"))  // Masked middle
    }
    
    @Test
    fun testToFarsiNumbers() {
        assertEquals("۲۰۲۴", PersianNumberFormatter.toFarsiNumbers("2024"))
        assertEquals("۱۴۰۳", PersianNumberFormatter.toFarsiNumbers("1403"))
        assertEquals("۰-۹۹۹", PersianNumberFormatter.toFarsiNumbers("0-999"))
    }
    
    @Test
    fun testToEnglishNumbers() {
        assertEquals("2024", PersianNumberFormatter.toEnglishNumbers("۲۰۲۴"))
        assertEquals("1403", PersianNumberFormatter.toEnglishNumbers("۱۴۰۳"))
    }
    
    @Test
    fun testCalculateSilverPrice() {
        val weight = 15.5
        val pricePerGram = 2_500_000L
        val result = PersianNumberFormatter.calculateSilverPrice(weight, pricePerGram)
        assertEquals(38_750_000L, result)
    }
    
    @Test
    fun testAddTax() {
        val basePrice = 1_000_000L
        val result = PersianNumberFormatter.addTax(basePrice, 0.09f)
        assertEquals(1_090_000L, result)
    }
    
    @Test
    fun testCalculateFinalPrice() {
        val basePrice = 1_000_000L
        val shipping = 50_000L
        val result = PersianNumberFormatter.calculateFinalPrice(
            basePrice = basePrice,
            shippingCost = shipping,
            taxRate = 0.09f
        )
        // (1,000,000 + 50,000) * 1.09 = 1,144,500
        assertEquals(1_144_500L, result)
    }
    
    @Test
    fun testFormatWeight() {
        val result = PersianNumberFormatter.formatWeight(15.5)
        assertTrue(result.contains("۱۵"))  // 15 in Persian
        assertTrue(result.contains("۵"))  // 5 in Persian
        assertTrue(result.contains("گرم"))  // gram
    }
    
    @Test
    fun testFormatIranianNationalId() {
        val result = PersianNumberFormatter.formatIranianNationalId("0123456789")
        assertTrue(result.contains("-"))  // Should have dashes
    }
    
    @Test
    fun testLargeNumberFormatting() {
        val result = PersianNumberFormatter.formatPriceWithoutCurrency(999_999_999L)
        assertTrue(result.contains("٬"))  // Should have thousands separator
    }
    
    @Test
    fun testSmallNumberFormatting() {
        val result = PersianNumberFormatter.formatPrice(1000)
        assertTrue(result.contains("۱٬۰۰۰"))  // 1,000 formatted
    }
}
