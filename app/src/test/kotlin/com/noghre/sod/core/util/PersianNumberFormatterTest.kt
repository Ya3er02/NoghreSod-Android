package com.noghre.sod.core.util

import org.junit.Test
import org.junit.Before
import com.google.common.truth.Truth.assertThat
import com.noghre.sod.domain.model.Toman
import com.noghre.sod.domain.model.Rial

/**
 * Unit tests برای Persian number formatting
 * 
 * اهداف:
 * - تبدیل ارقام انگلیسی به فارسی
 * - اضافه جداکننده‌ی هزاری
 * - قالب زبانی پول و درصد
 */
class PersianNumberFormatterTest {
    
    private lateinit var formatter: PersianNumberFormatter
    
    @Before
    fun setup() {
        formatter = PersianNumberFormatter()
    }
    
    // ==================== Basic Digit Conversion ====================
    
    @Test
    fun `formatPersianDigits should convert English digits to Persian`() {
        val result = formatter.formatPersianDigits("0123456789")
        assertThat(result).isEqualTo("۰۱۲۳۴۵۶۷۸۹")
    }
    
    @Test
    fun `formatPersianDigits should handle mixed content`() {
        val result = formatter.formatPersianDigits("Price: 123 Toman")
        assertThat(result).contains("۱۲۳")
    }
    
    @Test
    fun `formatPersianDigits should preserve non-digit characters`() {
        val result = formatter.formatPersianDigits("Hello123World")
        assertThat(result).isEqualTo("Hello۱۲۳World")
    }
    
    // ==================== Number Formatting with Separators ====================
    
    @Test
    fun `formatNumber should add thousand separator`() {
        val result = formatter.formatNumber(1000)
        assertThat(result).contains(",")
    }
    
    @Test
    fun `formatNumber should convert digits to Persian`() {
        val result = formatter.formatNumber(123)
        assertThat(result).isEqualTo("۱۲۳")
    }
    
    @Test
    fun `formatNumber should handle large numbers with separators`() {
        val result = formatter.formatNumber(1234567)
        assertThat(result).isEqualTo("۱,۲۳۴,۵۶۷")
    }
    
    @Test
    fun `formatNumber should handle zero`() {
        val result = formatter.formatNumber(0)
        assertThat(result).isEqualTo("۰")
    }
    
    @Test
    fun `formatNumber should handle negative numbers`() {
        val result = formatter.formatNumber(-500)
        assertThat(result).contains("−") // Persian minus
        assertThat(result).contains("۵۰۰")
    }
    
    // ==================== Currency Formatting ====================
    
    @Test
    fun `formatTomanPrice should include Toman suffix`() {
        val price = Toman(100000)
        val result = formatter.formatTomanPrice(price)
        
        assertThat(result).endsWith("تومان")
    }
    
    @Test
    fun `formatTomanPrice should format with separators`() {
        val price = Toman(123456)
        val result = formatter.formatTomanPrice(price)
        
        assertThat(result).contains(",")
        assertThat(result).contains("۱۲۳,۴۵۶")
    }
    
    @Test
    fun `formatRialPrice should include Rial suffix`() {
        val price = Rial(1000000)
        val result = formatter.formatRialPrice(price)
        
        assertThat(result).endsWith("ریال")
    }
    
    @Test
    fun `formatRialPrice should format with separators`() {
        val price = Rial(1234567)
        val result = formatter.formatRialPrice(price)
        
        assertThat(result).contains(",")
    }
    
    // ==================== Percentage Formatting ====================
    
    @Test
    fun `formatPercent should include percent symbol`() {
        val result = formatter.formatPercent(50.0)
        assertThat(result).endsWith("%")
    }
    
    @Test
    fun `formatPercent should convert digits to Persian`() {
        val result = formatter.formatPercent(50.0)
        assertThat(result).contains("۵۰")
    }
    
    @Test
    fun `formatPercent should handle decimal values`() {
        val result = formatter.formatPercent(25.5)
        assertThat(result).contains("۲۵")
        assertThat(result).contains("٫") // Persian decimal separator
        assertThat(result).contains("۵")
    }
    
    @Test
    fun `formatPercent should handle zero`() {
        val result = formatter.formatPercent(0.0)
        assertThat(result).isEqualTo("۰%")
    }
    
    @Test
    fun `formatPercent should handle 100 percent`() {
        val result = formatter.formatPercent(100.0)
        assertThat(result).isEqualTo("۱۰۰%")
    }
    
    // ==================== Edge Cases ====================
    
    @Test
    fun `formatNumber should handle very large numbers`() {
        val result = formatter.formatNumber(Long.MAX_VALUE / 2)
        assertThat(result).contains(",")
        assertThat(result).doesNotContain("0".."9") // Only Persian digits
    }
    
    @Test
    fun `formatNumber should handle single digit`() {
        val result = formatter.formatNumber(5)
        assertThat(result).isEqualTo("۵")
    }
    
    @Test
    fun `formatNumber should handle numbers ending in zero`() {
        val result = formatter.formatNumber(1000000)
        assertThat(result).isEqualTo("۱,۰۰۰,۰۰۰")
    }
    
    // ==================== RTL Character Handling ====================
    
    @Test
    fun `formatted output should be suitable for RTL layout`() {
        val result = formatter.formatTomanPrice(Toman(12345))
        
        // Should have Persian text (Toman suffix)
        assertThat(result).contains("تومان")
        // Should have Persian digits
        assertThat(result).doesNotContain("0".."9")
    }
    
    // ==================== Consistency Tests ====================
    
    @Test
    fun `same input should produce same output`() {
        val input = 123456
        val result1 = formatter.formatNumber(input)
        val result2 = formatter.formatNumber(input)
        
        assertThat(result1).isEqualTo(result2)
    }
    
    @Test
    fun `currency format should be consistent`() {
        val price = Toman(100000)
        val result1 = formatter.formatTomanPrice(price)
        val result2 = formatter.formatTomanPrice(price)
        
        assertThat(result1).isEqualTo(result2)
    }
    
    // ==================== Integration Tests ====================
    
    @Test
    fun `price conversion should maintain formatting`() {
        val toman = Toman(100)
        val rial = toman.toRial()
        
        val tomanFormatted = formatter.formatTomanPrice(toman)
        val rialFormatted = formatter.formatRialPrice(rial)
        
        // Both should be formatted in Persian
        assertThat(tomanFormatted).doesNotContain("0".."9")
        assertThat(rialFormatted).doesNotContain("0".."9")
    }
}
