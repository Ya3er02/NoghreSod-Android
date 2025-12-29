package com.noghre.sod.core.security

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.inject.Singleton
import timber.log.Timber

/**
 * Thread-safe rate limiter برای payment requests
 * 
 * محدودیت: Maximum 5 requests per user per 60 seconds
 */
@Singleton
class PaymentRateLimiter {
    private val attempts = ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>>()
    private val lock = ReentrantReadWriteLock()
    
    companion object {
        private const val MAX_ATTEMPTS = 5
        private const val WINDOW_MS = 60_000L // 60 seconds
    }
    
    /**
     * بررسی اگر user می‌تواند شروع پرداخت کند
     * 
     * @param userId Unique user identifier
     * @return true اگر request allowed, false اگر rate limited
     */
    fun canAttempt(userId: String): Boolean {
        lock.writeLock().lock()
        try {
            val now = System.currentTimeMillis()
            val userAttempts = attempts.getOrPut(userId) { ConcurrentLinkedQueue() }
            
            // Remove expired attempts (older than WINDOW_MS)
            while (userAttempts.isNotEmpty()) {
                val oldestAttempt = userAttempts.peek()
                if (oldestAttempt != null && now - oldestAttempt > WINDOW_MS) {
                    userAttempts.poll()
                } else {
                    break
                }
            }
            
            // Check if user has exceeded limit
            if (userAttempts.size >= MAX_ATTEMPTS) {
                Timber.w("⚠ï Rate limit exceeded for user: $userId (${userAttempts.size}/$MAX_ATTEMPTS)")
                return false
            }
            
            // Record this attempt
            userAttempts.offer(now)
            Timber.d("✅ Payment attempt allowed for user: $userId (${userAttempts.size}/$MAX_ATTEMPTS)")
            return true
            
        } finally {
            lock.writeLock().unlock()
        }
    }
    
    /**
     * دریافت جاری attempt count برای user
     */
    fun getAttemptCount(userId: String): Int {
        lock.readLock().lock()
        try {
            val userAttempts = attempts[userId] ?: return 0
            
            val now = System.currentTimeMillis()
            // Count non-expired attempts
            return userAttempts.count { (now - it) <= WINDOW_MS }
        } finally {
            lock.readLock().unlock()
        }
    }
    
    /**
     * حذف attempts برای user (debug/testing)
     */
    fun resetUser(userId: String) {
        lock.writeLock().lock()
        try {
            attempts.remove(userId)
            Timber.d("🗑️ Rate limiter reset for user: $userId")
        } finally {
            lock.writeLock().unlock()
        }
    }
    
    /**
     * حذف تمام cached attempts (برای app restart scenarios)
     */
    fun clearAll() {
        lock.writeLock().lock()
        try {
            attempts.clear()
            Timber.d("🗑️ All rate limiter data cleared")
        } finally {
            lock.writeLock().unlock()
        }
    }
}
