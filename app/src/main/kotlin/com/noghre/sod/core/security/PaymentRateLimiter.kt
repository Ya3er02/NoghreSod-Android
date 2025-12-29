package com.noghre.sod.core.security

import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Rate limiter for payment requests to prevent:
 * - DoS attacks on payment gateway
 * - Accidental double-charges from user double-clicking
 * - Merchant account being flagged as high-risk
 * 
 * Configuration:
 * - Max 5 payment requests per user per minute
 * - Sliding window implementation
 * - Thread-safe with ConcurrentHashMap
 */
@Singleton
class PaymentRateLimiter @Inject constructor() {
    
    companion object {
        // Maximum payment requests per user in the time window
        private const val MAX_ATTEMPTS = 5
        
        // Time window in milliseconds (1 minute)
        private const val WINDOW_MS = 60_000L
        
        // Map<UserId, Queue<Timestamp>> tracking attempt history
        private val attempts = ConcurrentHashMap<String, Queue<Long>>()
    }
    
    /**
     * Check if user can attempt a payment request.
     * 
     * @param userId Unique user identifier (can be account ID or device ID)
     * @return true if payment request allowed, false if rate limit exceeded
     */
    fun canAttempt(userId: String): Boolean {
        if (userId.isBlank()) {
            Timber.w("Rate limiter: Empty userId provided")
            return false
        }
        
        val now = System.currentTimeMillis()
        val userAttempts = attempts.getOrPut(userId) { LinkedList() }
        
        // Remove attempts outside the current time window
        synchronized(userAttempts) {
            while (userAttempts.isNotEmpty() && userAttempts.peek() < now - WINDOW_MS) {
                userAttempts.poll()
            }
            
            // Check if user exceeded rate limit
            if (userAttempts.size >= MAX_ATTEMPTS) {
                Timber.w(
                    "Rate limit exceeded for user $userId: " +
                    "${userAttempts.size}/$MAX_ATTEMPTS attempts in $WINDOW_MS ms"
                )
                return false
            }
            
            // Record this attempt
            userAttempts.offer(now)
            val remaining = MAX_ATTEMPTS - userAttempts.size
            
            Timber.d(
                "Payment attempt allowed for $userId. " +
                "Remaining: $remaining/$MAX_ATTEMPTS"
            )
            
            return true
        }
    }
    
    /**
     * Get remaining attempts for a user in the current window.
     * 
     * @return Number of remaining payment attempts (0-5)
     */
    fun getRemainingAttempts(userId: String): Int {
        if (userId.isBlank()) return 0
        
        val now = System.currentTimeMillis()
        val userAttempts = attempts[userId] ?: return MAX_ATTEMPTS
        
        synchronized(userAttempts) {
            // Count only recent attempts within time window
            val recentCount = userAttempts.count { it > now - WINDOW_MS }
            return MAX_ATTEMPTS - recentCount
        }
    }
    
    /**
     * Reset rate limit for a user (admin/support function).
     */
    fun resetUser(userId: String) {
        attempts.remove(userId)
        Timber.i("Rate limit reset for user $userId")
    }
    
    /**
     * Clear all rate limit records (for testing).
     */
    fun clearAll() {
        attempts.clear()
        Timber.w("All rate limits cleared")
    }
}