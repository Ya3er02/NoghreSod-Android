package com.noghre.sod.data.payment

import com.noghre.sod.domain.model.PaymentVerification
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Caches payment verifications to ensure idempotency
 * 
 * Problem: If verification is called multiple times for the same payment authority,
 * it may trigger duplicate order fulfillment.
 * 
 * Solution: Cache successful verifications and return cached result on subsequent calls.
 * 
 * Behavior:
 * - First call: Verifies with gateway, caches result
 * - Subsequent calls: Returns cached result immediately
 * - Expired entries: Cleaned up after TTL expiration
 * 
 * Thread-safe operations for concurrent access
 */
@Singleton
class PaymentVerificationCache @Inject constructor() {
    
    private data class CachedVerification(
        val verification: PaymentVerification,
        val cachedAt: Long
    )
    
    // Thread-safe cache: authority -> cached verification with timestamp
    private val cache = mutableMapOf<String, CachedVerification>()
    
    /**
     * Get cached verification if exists and not expired
     * 
     * @param authority Payment gateway authority code
     * @return Cached verification if valid, null otherwise
     */
    fun getVerification(authority: String): PaymentVerification? = synchronized(cache) {
        return cache[authority]?.let { cached ->
            // Check if cache entry has expired
            if (System.currentTimeMillis() - cached.cachedAt > CACHE_TTL_MS) {
                // Expired, remove and return null
                cache.remove(authority)
                Timber.d("Verification cache expired for authority: $authority")
                null
            } else {
                // Valid, return cached verification
                cached.verification
            }
        }
    }
    
    /**
     * Cache a successful verification
     * 
     * @param authority Payment gateway authority code
     * @param verification Verification details to cache
     */
    fun cacheVerification(authority: String, verification: PaymentVerification) {
        synchronized(cache) {
            cache[authority] = CachedVerification(
                verification = verification,
                cachedAt = System.currentTimeMillis()
            )
            Timber.d("Cached verification for authority: $authority")
        }
    }
    
    /**
     * Clear specific verification from cache
     * Useful for testing or manual cache invalidation
     * 
     * @param authority Authority to remove
     */
    fun clearVerification(authority: String) {
        synchronized(cache) {
            cache.remove(authority)
            Timber.d("Cleared cached verification for authority: $authority")
        }
    }
    
    /**
     * Clear all cached verifications
     * Useful for logout or cache reset
     */
    fun clearAll() {
        synchronized(cache) {
            cache.clear()
            Timber.d("Cleared all cached verifications")
        }
    }
    
    companion object {
        // Cache entries valid for 1 hour (3,600,000 ms)
        // Prevents stale verifications while remaining effective for typical user flow
        private const val CACHE_TTL_MS = 60 * 60 * 1000L
    }
}
