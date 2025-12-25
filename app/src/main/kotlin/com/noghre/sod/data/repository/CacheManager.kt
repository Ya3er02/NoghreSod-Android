package com.noghre.sod.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages cache invalidation with TTL (Time-to-Live) strategy.
 */
@Singleton
class CacheManager @Inject constructor() {

    private val cacheLock = Mutex()
    private val cacheTimestamps = ConcurrentHashMap<String, Long>()

    // Cache TTL in milliseconds
    companion object {
        private const val PRODUCTS_TTL = 30 * 60 * 1000L // 30 minutes
        private const val CATEGORIES_TTL = 2 * 60 * 60 * 1000L // 2 hours
        private const val FEATURED_TTL = 60 * 60 * 1000L // 1 hour
        private const val USER_PROFILE_TTL = 5 * 60 * 1000L // 5 minutes
        private const val ORDERS_TTL = 10 * 60 * 1000L // 10 minutes
        private const val CART_TTL = 1 * 60 * 1000L // 1 minute
    }

    /**
     * Check if cache is still valid.
     */
    suspend fun isCacheValid(cacheKey: String, ttl: Long): Boolean {
        return cacheLock.withLock {
            val lastUpdate = cacheTimestamps[cacheKey] ?: return@withLock false
            val isValid = System.currentTimeMillis() - lastUpdate < ttl
            
            if (!isValid) {
                Timber.d("Cache for $cacheKey expired (TTL: ${ttl}ms)")
                cacheTimestamps.remove(cacheKey)
            }
            
            isValid
        }
    }

    /**
     * Update cache timestamp.
     */
    suspend fun updateCache(cacheKey: String) {
        cacheLock.withLock {
            cacheTimestamps[cacheKey] = System.currentTimeMillis()
            Timber.d("Cache updated for $cacheKey")
        }
    }

    /**
     * Invalidate specific cache.
     */
    suspend fun invalidateCache(cacheKey: String) {
        cacheLock.withLock {
            cacheTimestamps.remove(cacheKey)
            Timber.d("Cache invalidated for $cacheKey")
        }
    }

    /**
     * Invalidate all caches.
     */
    suspend fun invalidateAllCaches() {
        cacheLock.withLock {
            cacheTimestamps.clear()
            Timber.d("All caches invalidated")
        }
    }

    /**
     * Check and update product cache.
     */
    suspend fun isProductsCacheValid(): Boolean {
        return isCacheValid("products", PRODUCTS_TTL)
    }

    suspend fun updateProductsCache() {
        updateCache("products")
    }

    suspend fun invalidateProductsCache() {
        invalidateCache("products")
    }

    /**
     * Check and update categories cache.
     */
    suspend fun isCategoriesCacheValid(): Boolean {
        return isCacheValid("categories", CATEGORIES_TTL)
    }

    suspend fun updateCategoriesCache() {
        updateCache("categories")
    }

    /**
     * Check and update featured products cache.
     */
    suspend fun isFeaturedCacheValid(): Boolean {
        return isCacheValid("featured", FEATURED_TTL)
    }

    suspend fun updateFeaturedCache() {
        updateCache("featured")
    }

    /**
     * Check and update user profile cache.
     */
    suspend fun isUserProfileCacheValid(): Boolean {
        return isCacheValid("user_profile", USER_PROFILE_TTL)
    }

    suspend fun updateUserProfileCache() {
        updateCache("user_profile")
    }

    suspend fun invalidateUserProfileCache() {
        invalidateCache("user_profile")
    }

    /**
     * Check and update orders cache.
     */
    suspend fun isOrdersCacheValid(): Boolean {
        return isCacheValid("orders", ORDERS_TTL)
    }

    suspend fun updateOrdersCache() {
        updateCache("orders")
    }

    suspend fun invalidateOrdersCache() {
        invalidateCache("orders")
    }

    /**
     * Check and update cart cache.
     */
    suspend fun isCartCacheValid(): Boolean {
        return isCacheValid("cart", CART_TTL)
    }

    suspend fun updateCartCache() {
        updateCache("cart")
    }

    suspend fun invalidateCartCache() {
        invalidateCache("cart")
    }

    /**
     * Invalidate user-related caches on logout.
     */
    suspend fun invalidateUserCaches() {
        invalidateCache("user_profile")
        invalidateCache("orders")
        invalidateCache("cart")
        Timber.d("User caches invalidated")
    }
}
