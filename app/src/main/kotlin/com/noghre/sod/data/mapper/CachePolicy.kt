package com.noghre.sod.data.mapper

/**
 * Cache configuration and policies.
 * Defines cache expiry times and size limits.
 */
object CachePolicy {
    // Cache expires after 5 minutes
    const val CACHE_EXPIRY_TIME = 5 * 60 * 1000L // milliseconds

    // Maximum number of products to cache
    const val MAX_PRODUCT_CACHE_SIZE = 50

    // Maximum number of categories to cache
    const val MAX_CATEGORY_CACHE_SIZE = 30

    /**
     * Check if cached data is still valid.
     *
     * @param cachedAt Timestamp when data was cached
     * @return True if cache is still valid, false if expired
     */
    fun isCacheValid(cachedAt: Long): Boolean {
        return System.currentTimeMillis() - cachedAt < CACHE_EXPIRY_TIME
    }
}
