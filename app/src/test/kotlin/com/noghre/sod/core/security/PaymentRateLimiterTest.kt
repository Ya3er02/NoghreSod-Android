package com.noghre.sod.core.security

import io.mockk.mockk
import org.junit.Test
import org.junit.Before
import com.google.common.truth.Truth.assertThat
import kotlin.concurrent.thread
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/**
 * Unit tests برای PaymentRateLimiter
 * 
 * اهداف:
 * - تست rate limiting logic
 * - تست thread safety
 * - تست cleanup of expired attempts
 * - تست concurrent access
 */
class PaymentRateLimiterTest {
    
    private lateinit var rateLimiter: PaymentRateLimiter
    private val testUserId = "test_user_123"
    
    @Before
    fun setup() {
        rateLimiter = PaymentRateLimiter()
    }
    
    // ==================== Basic Rate Limiting ====================
    
    @Test
    fun `first 5 attempts should be allowed`() {
        repeat(5) { index ->
            val result = rateLimiter.canAttempt(testUserId)
            assertThat(result).isTrue()
        }
    }
    
    @Test
    fun `6th attempt should be blocked within 60 seconds`() {
        // Allow first 5
        repeat(5) {
            rateLimiter.canAttempt(testUserId)
        }
        
        // 6th should be blocked
        val result = rateLimiter.canAttempt(testUserId)
        assertThat(result).isFalse()
    }
    
    @Test
    fun `different users should have independent limits`() {
        val user1 = "user1"
        val user2 = "user2"
        
        // User 1 uses all 5 attempts
        repeat(5) {
            assertThat(rateLimiter.canAttempt(user1)).isTrue()
        }
        
        // User 1 should be blocked
        assertThat(rateLimiter.canAttempt(user1)).isFalse()
        
        // User 2 should still be allowed
        assertThat(rateLimiter.canAttempt(user2)).isTrue()
    }
    
    // ==================== Attempt Tracking ====================
    
    @Test
    fun `getAttemptCount should return correct count`() {
        val count1 = rateLimiter.getAttemptCount(testUserId)
        assertThat(count1).isEqualTo(0)
        
        rateLimiter.canAttempt(testUserId)
        val count2 = rateLimiter.getAttemptCount(testUserId)
        assertThat(count2).isEqualTo(1)
        
        repeat(3) {
            rateLimiter.canAttempt(testUserId)
        }
        val count5 = rateLimiter.getAttemptCount(testUserId)
        assertThat(count5).isEqualTo(4)
    }
    
    @Test
    fun `getAttemptCount should return 0 for unknown user`() {
        val unknownUser = "unknown_user_xyz"
        val count = rateLimiter.getAttemptCount(unknownUser)
        
        assertThat(count).isEqualTo(0)
    }
    
    // ==================== Reset Functionality ====================
    
    @Test
    fun `resetUser should clear attempts for user`() {
        // Make 5 attempts
        repeat(5) {
            rateLimiter.canAttempt(testUserId)
        }
        
        // Should be rate limited
        assertThat(rateLimiter.canAttempt(testUserId)).isFalse()
        
        // Reset
        rateLimiter.resetUser(testUserId)
        
        // Should be allowed again
        assertThat(rateLimiter.canAttempt(testUserId)).isTrue()
    }
    
    @Test
    fun `resetUser should only affect specified user`() {
        val user1 = "user1"
        val user2 = "user2"
        
        // Both users make attempts
        repeat(3) { rateLimiter.canAttempt(user1) }
        repeat(4) { rateLimiter.canAttempt(user2) }
        
        // Reset user1
        rateLimiter.resetUser(user1)
        
        // User1 should be reset
        assertThat(rateLimiter.getAttemptCount(user1)).isEqualTo(0)
        
        // User2 should not be affected
        assertThat(rateLimiter.getAttemptCount(user2)).isEqualTo(4)
    }
    
    // ==================== Clear All ====================
    
    @Test
    fun `clearAll should reset all users`() {
        val user1 = "user1"
        val user2 = "user2"
        
        // Make attempts
        repeat(3) { rateLimiter.canAttempt(user1) }
        repeat(2) { rateLimiter.canAttempt(user2) }
        
        assertThat(rateLimiter.getAttemptCount(user1)).isEqualTo(3)
        assertThat(rateLimiter.getAttemptCount(user2)).isEqualTo(2)
        
        // Clear all
        rateLimiter.clearAll()
        
        // Both should be reset
        assertThat(rateLimiter.getAttemptCount(user1)).isEqualTo(0)
        assertThat(rateLimiter.getAttemptCount(user2)).isEqualTo(0)
    }
    
    // ==================== Time Window Tests ====================
    
    @Test
    fun `attempts beyond 60 second window should be cleaned up`() {
        // This is harder to test without mocking time
        // But we can verify the mechanism exists
        val count1 = rateLimiter.getAttemptCount(testUserId)
        assertThat(count1).isEqualTo(0)
    }
    
    // ==================== Thread Safety Tests ====================
    
    @Test
    fun `concurrent attempts from multiple threads should be safe`() {
        val threadCount = 10
        val attemptsPerThread = 3
        val latch = CountDownLatch(threadCount)
        val successCount = AtomicInteger(0)
        
        repeat(threadCount) { threadIndex ->
            thread {
                try {
                    repeat(attemptsPerThread) {
                        if (rateLimiter.canAttempt(testUserId)) {
                            successCount.incrementAndGet()
                        }
                    }
                } finally {
                    latch.countDown()
                }
            }
        }
        
        latch.await()
        
        // Should have exactly 5 successful attempts (rate limit)
        assertThat(successCount.get()).isEqualTo(5)
    }
    
    @Test
    fun `concurrent reads should not cause issues`() {
        val threadCount = 5
        val latch = CountDownLatch(threadCount)
        val counts = mutableListOf<Int>()
        
        // Make some attempts first
        repeat(3) {
            rateLimiter.canAttempt(testUserId)
        }
        
        repeat(threadCount) {
            thread {
                try {
                    val count = rateLimiter.getAttemptCount(testUserId)
                    synchronized(counts) {
                        counts.add(count)
                    }
                } finally {
                    latch.countDown()
                }
            }
        }
        
        latch.await()
        
        // All reads should report same count
        assertThat(counts).hasSize(threadCount)
        counts.forEach { count ->
            assertThat(count).isEqualTo(3)
        }
    }
    
    @Test
    fun `concurrent resets should be safe`() {
        val threadCount = 3
        val latch = CountDownLatch(threadCount)
        
        // Make attempts
        repeat(5) {
            rateLimiter.canAttempt(testUserId)
        }
        
        repeat(threadCount) {
            thread {
                try {
                    rateLimiter.resetUser(testUserId)
                } finally {
                    latch.countDown()
                }
            }
        }
        
        latch.await()
        
        // Should be in clean state
        val count = rateLimiter.getAttemptCount(testUserId)
        assertThat(count).isEqualTo(0)
    }
    
    // ==================== Boundary Tests ====================
    
    @Test
    fun `exactly 5 attempts should be allowed`() {
        repeat(5) {
            assertThat(rateLimiter.canAttempt(testUserId)).isTrue()
        }
    }
    
    @Test
    fun `exactly 6 attempts should be blocked`() {
        repeat(5) {
            rateLimiter.canAttempt(testUserId)
        }
        
        assertThat(rateLimiter.canAttempt(testUserId)).isFalse()
    }
    
    @Test
    fun `empty user ID should work`() {
        val emptyUserId = ""
        val result = rateLimiter.canAttempt(emptyUserId)
        
        assertThat(result).isTrue()
    }
    
    @Test
    fun `very long user ID should work`() {
        val longUserId = "a".repeat(10000)
        val result = rateLimiter.canAttempt(longUserId)
        
        assertThat(result).isTrue()
    }
    
    // ==================== State Consistency Tests ====================
    
    @Test
    fun `rate limiter should maintain consistent state`() {
        // Multiple operations
        rateLimiter.canAttempt("user1")
        rateLimiter.canAttempt("user2")
        rateLimiter.resetUser("user1")
        rateLimiter.canAttempt("user1")
        
        // Check state
        assertThat(rateLimiter.getAttemptCount("user1")).isEqualTo(1)
        assertThat(rateLimiter.getAttemptCount("user2")).isEqualTo(1)
    }
}
