package com.noghre.sod.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Stale-While-Revalidate Repository Pattern
 * 
 * Implements the stale-while-revalidate caching strategy:
 * 1. Return cached data immediately (even if stale)
 * 2. Fetch fresh data in background
 * 3. Update cache with fresh data
 * 4. Emit fresh data to subscribers
 * 
 * Benefits:
 * - Instant response time (better UX)
 * - Always has fallback data
 * - Background refresh keeps data fresh
 * - Graceful offline support
 * 
 * @since 1.0.0
 */
abstract class StaleWhileRevalidateRepository<T> {
    
    /**
     * Data state sealed class
     */
    sealed class DataState<out T> {
        object Loading : DataState<Nothing>()
        data class Cached<T>(val data: T) : DataState<T>()
        data class Fresh<T>(val data: T) : DataState<T>()
        data class Stale<T>(val data: T, val error: Exception) : DataState<T>()
        data class Error(val exception: Exception) : DataState<Nothing>()
    }
    
    /**
     * Stale-While-Revalidate pattern implementation
     * 
     * @param cacheKey Unique cache key for this data
     * @param freshDuration How long to consider data "fresh"
     * @param staleDuration How long to accept stale data
     * @param query Function to query cached data
     * @param fetch Function to fetch fresh data from network
     * @param save Function to save data to cache
     * @param shouldFetch Condition to determine if fetch is needed
     */
    suspend fun <R> staleWhileRevalidate(
        cacheKey: String,
        freshDuration: Duration = 5.minutes,
        staleDuration: Duration = 1.hours,
        query: suspend () -> R?,
        fetch: suspend () -> R,
        save: suspend (R) -> Unit,
        shouldFetch: (R?) -> Boolean = { true }
    ): Flow<DataState<R>> = flow {
        
        // 1. Try to emit cached data first
        val cached = query()
        if (cached != null) {
            emit(DataState.Cached(cached))
            
            // If fresh enough, return immediately
            if (isCacheFresh(cacheKey, freshDuration)) {
                emit(DataState.Fresh(cached))
                return@flow
            }
        } else {
            emit(DataState.Loading())
        }
        
        // 2. Check if we should fetch
        if (!shouldFetch(cached)) {
            cached?.let { emit(DataState.Fresh(it)) }
            return@flow
        }
        
        // 3. Fetch fresh data in background
        try {
            val fresh = fetch()
            save(fresh)
            updateCacheMetadata(cacheKey, staleDuration)
            
            emit(DataState.Fresh(fresh))
            
        } catch (e: Exception) {
            Timber.w(e, "Background refresh failed for $cacheKey")
            
            // If we have cached data (even stale), emit it with error
            if (cached != null) {
                emit(DataState.Stale(cached, e))
            } else {
                emit(DataState.Error(e))
            }
        }
    }
    
    /**
     * Check if cache is fresh
     */
    private suspend fun isCacheFresh(
        key: String,
        freshDuration: Duration
    ): Boolean {
        val timestamp = getCacheTimestamp(key) ?: return false
        val age = System.currentTimeMillis() - timestamp
        return age < freshDuration.inWholeMilliseconds
    }
    
    /**
     * Subclasses should implement these methods
     */
    protected abstract suspend fun getCacheTimestamp(key: String): Long?
    protected abstract suspend fun updateCacheMetadata(key: String, staleDuration: Duration)
}

/**
 * Example implementation for Product Repository
 * 
 * Usage:
 * ```
 * val productState = productRepository.getProducts(page = 1)
 *     .collect { state ->
 *         when (state) {
 *             is DataState.Loading -> showLoading()
 *             is DataState.Cached -> showCachedData(state.data)
 *             is DataState.Fresh -> showFreshData(state.data)
 *             is DataState.Stale -> showCachedDataWithWarning(state.data)
 *             is DataState.Error -> showError(state.exception)
 *         }
 *     }
 * ```
 */
abstract class ProductRepository : StaleWhileRevalidateRepository<List<Any>>() {
    
    // Cache timestamp tracking
    private val cacheTimestamps = mutableMapOf<String, Long>()
    
    override suspend fun getCacheTimestamp(key: String): Long? {
        return cacheTimestamps[key]
    }
    
    override suspend fun updateCacheMetadata(
        key: String,
        staleDuration: Duration
    ) {
        cacheTimestamps[key] = System.currentTimeMillis()
    }
    
    /**
     * Get products with stale-while-revalidate
     */
    suspend fun getProducts(
        page: Int = 1,
        pageSize: Int = 20
    ): Flow<DataState<List<Any>>> {
        return staleWhileRevalidate(
            cacheKey = "products_${page}_${pageSize}",
            freshDuration = 5.minutes,
            staleDuration = 1.hours,
            
            query = {
                // Query from local database/cache
                queryProductsFromDb(page, pageSize)
            },
            
            fetch = {
                // Fetch from API
                fetchProductsFromApi(page, pageSize)
            },
            
            save = { products ->
                // Save to database/cache
                saveProductsToDb(products)
            },
            
            shouldFetch = { cached ->
                // Only fetch if first page or cache is empty
                cached.isNullOrEmpty() || page == 1
            }
        )
    }
    
    protected abstract suspend fun queryProductsFromDb(page: Int, pageSize: Int): List<Any>?
    protected abstract suspend fun fetchProductsFromApi(page: Int, pageSize: Int): List<Any>
    protected abstract suspend fun saveProductsToDb(products: List<Any>)
}
