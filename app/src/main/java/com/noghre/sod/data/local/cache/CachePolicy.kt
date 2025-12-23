package com.noghre.sod.data.local.cache

import java.util.concurrent.TimeUnit

/**
 * Cache expiry policy for different data types
 */
object CachePolicy {
    // Cache durations in milliseconds
    val PRODUCT_CACHE_DURATION = TimeUnit.MINUTES.toMillis(5)
    val CATEGORY_CACHE_DURATION = TimeUnit.HOURS.toMillis(24)
    val USER_CACHE_DURATION = TimeUnit.MINUTES.toMillis(30)
    val ORDER_CACHE_DURATION = TimeUnit.HOURS.toMillis(6)
    val ADDRESS_CACHE_DURATION = TimeUnit.HOURS.toMillis(24)

    /**
     * Check if cache has expired
     */
    fun isCacheExpired(expiryTime: Long): Boolean {
        return System.currentTimeMillis() > expiryTime
    }

    /**
     * Get cache expiry timestamp
     */
    fun getCacheExpiry(duration: Long): Long {
        return System.currentTimeMillis() + duration
    }
}