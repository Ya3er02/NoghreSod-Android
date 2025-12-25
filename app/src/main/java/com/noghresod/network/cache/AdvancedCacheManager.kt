package com.noghresod.network.cache

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

// ==================== Cache Policies ====================

sealed class CachePolicy {
    object Forever : CachePolicy()
    
    data class TimeToLive(val duration: Long) : CachePolicy()
    
    data class Versioned(val version: Int) : CachePolicy()
    
    data class ETagBased(val etag: String) : CachePolicy()
    
    data class Dependent(
        val dependencies: List<String>,
        val ttl: Long
    ) : CachePolicy()
    
    data class StaleWhileRevalidate(
        val freshDuration: Long,
        val staleDuration: Long
    ) : CachePolicy()
}

data class CacheMetadata(
    val key: String,
    val policy: CachePolicy,
    val timestamp: Long = System.currentTimeMillis(),
    val version: Int = 1,
    val etag: String? = null,
    val size: Long = 0,
    val dependencies: List<String> = emptyList()
)

// ==================== Advanced Cache Manager ====================

class AdvancedCacheManager(
    @ApplicationContext private val context: Context
) {
    
    private val cacheMetadata = ConcurrentHashMap<String, CacheMetadata>()
    private val cacheLock = Mutex()
    
    // Memory cache storage
    private val memoryCache = ConcurrentHashMap<String, Any>()
    private val MAX_MEMORY_CACHE_SIZE = 100 * 1024 * 1024 // 100 MB
    
    // ==================== Core Cache Methods ====================
    
    /**
     * بررسی اعتبار cache با policy تعریف شده
     */
    suspend fun isCacheValid(
        key: String,
        policy: CachePolicy = CachePolicy.Forever
    ): Boolean = cacheLock.withLock {
        val metadata = cacheMetadata[key] ?: return@withLock false
        val isMissing = memoryCache[key] == null
        
        if (isMissing) return@withLock false
        
        when (policy) {
            is CachePolicy.Forever -> true
            
            is CachePolicy.TimeToLive -> {
                val age = System.currentTimeMillis() - metadata.timestamp
                age < policy.duration
            }
            
            is CachePolicy.Versioned -> {
                metadata.version == policy.version
            }
            
            is CachePolicy.ETagBased -> {
                metadata.etag == policy.etag
            }
            
            is CachePolicy.Dependent -> {
                val allDepsValid = policy.dependencies.all { dep ->
                    cacheMetadata[dep]?.let { depMetadata ->
                        val age = System.currentTimeMillis() - depMetadata.timestamp
                        age < policy.ttl
                    } ?: false
                }
                
                if (!allDepsValid) {
                    Timber.d("❌ Cache invalidated due to dependency: $key")
                    cacheMetadata.remove(key)
                    memoryCache.remove(key)
                }
                
                allDepsValid
            }
            
            is CachePolicy.StaleWhileRevalidate -> {
                val age = System.currentTimeMillis() - metadata.timestamp
                age < policy.staleDuration
            }
        }
    }
    
    /**
     * بررسی fresh بودن cache
     */
    suspend fun isCacheFresh(
        key: String,
        policy: CachePolicy.StaleWhileRevalidate
    ): Boolean = cacheLock.withLock {
        val metadata = cacheMetadata[key] ?: return@withLock false
        val age = System.currentTimeMillis() - metadata.timestamp
        age < policy.freshDuration
    }
    
    /**
     * ذخیره data در cache
     */
    suspend fun <T : Any> putCache(
        key: String,
        data: T,
        policy: CachePolicy,
        etag: String? = null,
        version: Int = 1,
        dependencies: List<String> = emptyList()
    ) = cacheLock.withLock {
        memoryCache[key] = data
        
        cacheMetadata[key] = CacheMetadata(
            key = key,
            policy = policy,
            timestamp = System.currentTimeMillis(),
            version = version,
            etag = etag,
            size = estimateSize(data),
            dependencies = dependencies
        )
        
        Timber.d("✅ Cache updated: $key (policy: ${policy::class.simpleName})")
        
        // Check memory limits
        if (getTotalCacheSize() > MAX_MEMORY_CACHE_SIZE) {
            evictLRU(MAX_MEMORY_CACHE_SIZE / 2)
        }
    }
    
    /**
     * خروج cache
     */
    suspend fun <T> getCache(key: String): T? = cacheLock.withLock {
        @Suppress("UNCHECKED_CAST")
        return@withLock memoryCache[key] as? T
    }
    
    /**
     * Invalidate کردن cache
     */
    suspend fun invalidateCache(
        key: String,
        cascade: Boolean = true
    ) = cacheLock.withLock {
        cacheMetadata.remove(key)
        memoryCache.remove(key)
        
        if (cascade) {
            val dependents = cacheMetadata.values.filter { metadata ->
                metadata.dependencies.contains(key)
            }.map { it.key }
            
            dependents.forEach { dependent ->
                cacheMetadata.remove(dependent)
                memoryCache.remove(dependent)
                Timber.d("📄 Cascade invalidated: $dependent")
            }
        }
        
        Timber.d("❌ Cache invalidated: $key (cascade: $cascade)")
    }
    
    /**
     * پاك کردن cache های منقضی‌شده
     */
    suspend fun cleanupExpiredCaches() = cacheLock.withLock {
        val now = System.currentTimeMillis()
        val expired = cacheMetadata.filter { (_, metadata) ->
            when (val policy = metadata.policy) {
                is CachePolicy.TimeToLive -> {
                    now - metadata.timestamp > policy.duration
                }
                is CachePolicy.StaleWhileRevalidate -> {
                    now - metadata.timestamp > policy.staleDuration
                }
                else -> false
            }
        }
        
        expired.keys.forEach { key ->
            cacheMetadata.remove(key)
            memoryCache.remove(key)
            Timber.d("🗑️ Cleaned up expired cache: $key")
        }
        
        Timber.i("🏄 Cleaned up ${expired.size} expired caches")
    }
    
    /**
     * حجم کل cache
     */
    suspend fun getTotalCacheSize(): Long = cacheLock.withLock {
        cacheMetadata.values.sumOf { it.size }
    }
    
    /**
     * Eviction بر اساس LRU
     */
    suspend fun evictLRU(targetSize: Long) = cacheLock.withLock {
        var currentSize = getTotalCacheSize()
        
        if (currentSize <= targetSize) return@withLock
        
        val sorted = cacheMetadata.values.sortedBy { it.timestamp }
        
        for (metadata in sorted) {
            if (currentSize <= targetSize) break
            
            // Skip Forever policies
            if (metadata.policy is CachePolicy.Forever) continue
            
            cacheMetadata.remove(metadata.key)
            memoryCache.remove(metadata.key)
            currentSize -= metadata.size
            
            Timber.d("🚮 Evicted cache: ${metadata.key} (size: ${metadata.size})")
        }
        
        Timber.i("📄 LRU eviction completed. New size: $currentSize")
    }
    
    /**
     * آمار جاری cache
     */
    suspend fun getCacheStats(): CacheStats = cacheLock.withLock {
        CacheStats(
            totalEntries = cacheMetadata.size,
            totalSize = getTotalCacheSize(),
            maxSize = MAX_MEMORY_CACHE_SIZE
        )
    }
    
    // ==================== Helper Methods ====================
    
    private fun <T> estimateSize(data: T): Long {
        return when (data) {
            is List<*> -> (data.size * 1024).toLong() // Approximate 1KB per item
            is String -> data.length.toLong()
            is Map<*, *> -> (data.size * 2048).toLong()
            else -> 1024L
        }
    }
}

data class CacheStats(
    val totalEntries: Int,
    val totalSize: Long,
    val maxSize: Long
) {
    val usagePercent: Float get() = (totalSize.toFloat() / maxSize) * 100
}

// ==================== Stale-While-Revalidate Repository ====================

abstract class StaleWhileRevalidateRepository<T>(
    protected val cacheManager: AdvancedCacheManager
) {
    
    sealed class DataState<out R> {
        object Loading : DataState<Nothing>()
        data class Cached<R>(val data: R) : DataState<R>()
        data class Fresh<R>(val data: R) : DataState<R>()
        data class Stale<R>(val data: R, val error: Exception) : DataState<R>()
        data class Error(val exception: Exception) : DataState<Nothing>()
    }
    
    suspend fun <R> staleWhileRevalidate(
        cacheKey: String,
        freshDuration: Long = 5.minutes.inWholeMilliseconds,
        staleDuration: Long = 1.hours.inWholeMilliseconds,
        query: suspend () -> R?,
        fetch: suspend () -> R,
        save: suspend (R) -> Unit,
        shouldFetch: (R?) -> Boolean = { true }
    ): DataState<R> {
        val policy = CachePolicy.StaleWhileRevalidate(freshDuration, staleDuration)
        
        // 1. ابتدا فرستگر cache
        val cached = query()
        if (cached != null) {
            if (cacheManager.isCacheFresh(cacheKey, policy)) {
                return DataState.Fresh(cached)
            }
            return DataState.Cached(cached)
        }
        
        // 2. اگر باید fetch کن ببین
        if (!shouldFetch(cached)) {
            return cached?.let { DataState.Fresh(it) } ?: DataState.Loading
        }
        
        // 3. جلب برای background
        return try {
            val fresh = fetch()
            save(fresh)
            
            cacheManager.putCache(
                key = cacheKey,
                data = fresh,
                policy = policy
            )
            
            DataState.Fresh(fresh)
            
        } catch (e: Exception) {
            Timber.w(e, "🚮 Background refresh failed for $cacheKey")
            
            if (cached != null) {
                DataState.Stale(cached, e)
            } else {
                DataState.Error(e)
            }
        }
    }
}
