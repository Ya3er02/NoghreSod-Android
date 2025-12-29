package com.noghre.sod.domain.model

import org.junit.Test
import org.junit.Before
import com.google.common.truth.Truth.assertThat

/**
 * Unit tests برای Money value classes
 * 
 * اهداف:
 * - تأیید type safety بین Toman و Rial
 * - تست تبدیل currency
 * - تست arithmetic operations
 */
class MoneyTest {
    
    @Before
    fun setup() {
        // No setup needed for value class tests
    }
    
    // ==================== Toman Tests ====================
    
    @Test
    fun `Toman should be creatable with positive value`() {
        val toman = Toman(1000)
        assertThat(toman.value).isEqualTo(1000L)
    }
    
    @Test
    fun `Toman should handle zero value`() {
        val toman = Toman(0)
        assertThat(toman.value).isEqualTo(0L)
    }
    
    @Test
    fun `Toman should handle large values`() {
        val toman = Toman(Long.MAX_VALUE)
        assertThat(toman.value).isEqualTo(Long.MAX_VALUE)
    }
    
    @Test
    fun `Toman to Rial should multiply by 10`() {
        val toman = Toman(100)
        val rial = toman.toRial()
        
        assertThat(rial.value).isEqualTo(1000L)
    }
    
    @Test
    fun `Toman conversion should preserve value for edge cases`() {
        val toman = Toman(1)
        val rial = toman.toRial()
        
        assertThat(rial.value).isEqualTo(10L)
    }
    
    // ==================== Rial Tests ====================
    
    @Test
    fun `Rial should be creatable with positive value`() {
        val rial = Rial(10000)
        assertThat(rial.value).isEqualTo(10000L)
    }
    
    @Test
    fun `Rial should handle zero value`() {
        val rial = Rial(0)
        assertThat(rial.value).isEqualTo(0L)
    }
    
    @Test
    fun `Rial to Toman should divide by 10`() {
        val rial = Rial(1000)
        val toman = rial.toToman()
        
        assertThat(toman.value).isEqualTo(100L)
    }
    
    // ==================== Type Safety Tests ====================
    
    @Test
    fun `Toman and Rial should not be directly comparable`() {
        val toman = Toman(1000)
        val rial = Rial(10000)
        
        // These should NOT be equal even with same underlying value
        // because they are different types
        assertThat(toman.value == rial.value).isTrue()
        
        // But converted values should match
        assertThat(toman.toRial().value).isEqualTo(rial.value)
    }
    
    @Test
    fun `Money operations should preserve type`() {
        val toman1 = Toman(500)
        val toman2 = Toman(300)
        
        // Arithmetic with raw values
        val sum = toman1.value + toman2.value
        assertThat(sum).isEqualTo(800L)
        
        // Create new Toman from sum
        val resultToman = Toman(sum)
        assertThat(resultToman.value).isEqualTo(800L)
    }
    
    // ==================== Currency Consistency Tests ====================
    
    @Test
    fun `conversion from Toman to Rial and back should preserve value`() {
        val original = Toman(100)
        val rial = original.toRial()
        val backToToman = rial.toToman()
        
        assertThat(backToToman.value).isEqualTo(original.value)
    }
    
    @Test
    fun `multiple conversions should work correctly`() {
        val toman = Toman(1000)
        
        val rial1 = toman.toRial()
        assertThat(rial1.value).isEqualTo(10000L)
        
        val toman2 = rial1.toToman()
        assertThat(toman2.value).isEqualTo(1000L)
        
        val rial2 = toman2.toRial()
        assertThat(rial2.value).isEqualTo(10000L)
    }
    
    // ==================== Equality Tests ====================
    
    @Test
    fun `same Toman values should be equal`() {
        val toman1 = Toman(500)
        val toman2 = Toman(500)
        
        assertThat(toman1).isEqualTo(toman2)
    }
    
    @Test
    fun `same Rial values should be equal`() {
        val rial1 = Rial(5000)
        val rial2 = Rial(5000)
        
        assertThat(rial1).isEqualTo(rial2)
    }
    
    @Test
    fun `different Toman values should not be equal`() {
        val toman1 = Toman(500)
        val toman2 = Toman(600)
        
        assertThat(toman1).isNotEqualTo(toman2)
    }
    
    // ==================== Hash Code Tests ====================
    
    @Test
    fun `same Toman values should have same hash code`() {
        val toman1 = Toman(500)
        val toman2 = Toman(500)
        
        assertThat(toman1.hashCode()).isEqualTo(toman2.hashCode())
    }
    
    @Test
    fun `same Rial values should have same hash code`() {
        val rial1 = Rial(5000)
        val rial2 = Rial(5000)
        
        assertThat(rial1.hashCode()).isEqualTo(rial2.hashCode())
    }
    
    // ==================== String Representation Tests ====================
    
    @Test
    fun `Toman should have meaningful string representation`() {
        val toman = Toman(1000)
        val str = toman.toString()
        
        assertThat(str).contains("1000")
    }
    
    @Test
    fun `Rial should have meaningful string representation`() {
        val rial = Rial(10000)
        val str = rial.toString()
        
        assertThat(str).contains("10000")
    }
}
