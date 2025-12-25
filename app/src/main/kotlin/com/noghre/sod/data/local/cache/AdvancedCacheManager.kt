package com.noghre.sod.data.local.cache

import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Advanced Cache Manager
 * 
 * Multi-layer caching strategy with:
 * - TTL (Time To Live) policies
 * - Version-based invalidation
 * - ETag-based caching
 * - Dependency tracking
 * - Stale-While-Revalidate pattern
 * - LRU eviction
 * 
 * @since 1.0.0
 */
@Singleton
class AdvancedCacheManager @Inject constructor() {
    
    private val cacheMetadata = ConcurrentHashMap<String, CacheMetadata>()
    private val cacheLock = Mutex()
    
    // ==================== Cache Policies ====================
    
    sealed class CachePolicy {
        // Cache is valid forever until explicitly invalidated
        object Forever : CachePolicy()
        
        // Simple TTL (Time To Live)
        data class TimeToLive(val duration: Long) : CachePolicy()
        
        // Version-based invalidation
        data class Versioned(val version: Int) : CachePolicy()
        
        // ETag-based (for HTTP caching)
        data class ETagBased(val etag: String) : CachePolicy()
        
        // Cache with dependencies on other caches
        data class Dependent(
            val dependencies: List<String>,
            val ttl: Long
        ) : CachePolicy()
        
        // Stale-While-Revalidate pattern
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
        val dependencies: List<String> = emptyList(),
        val accessCount: Int = 0,
        val lastAccessTime: Long = System.currentTimeMillis()
    )
    
    // ==================== Core Methods ====================
    
    /**
     * Check if cache is valid based on policy
     */
    suspend fun isCacheValid(
        key: String,
        policy: CachePolicy = CachePolicy.Forever
    ): Boolean = cacheLock.withLock {
        val metadata = cacheMetadata[key] ?: return@withLock false
        
        return@withLock when (policy) {
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
                // Check if all dependencies are valid
                val allDepsValid = policy.dependencies.all { dep ->
                    cacheMetadata[dep]?.let { depMetadata ->
                        val age = System.currentTimeMillis() - depMetadata.timestamp
                        age < policy.ttl
                    } ?: false
                }
                
                if (!allDepsValid) {
                    Timber.d("Cache invalidated due to dependency: $key")
                    cacheMetadata.remove(key)
                }
                
                allDepsValid
            }
            
            is CachePolicy.StaleWhileRevalidate -> {
                val age = System.currentTimeMillis() - metadata.timestamp
                age < policy.staleDuration // Accept cache until stale duration
            }
        }
    }
    
    /**
     * Check if cache is fresh (not stale)
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
     * Update cache metadata
     */
    suspend fun updateCache(
        key: String,
        policy: CachePolicy,
        etag: String? = null,
        version: Int = 1,
        size: Long = 0,
        dependencies: List<String> = emptyList()
    ) = cacheLock.withLock {
        val existing = cacheMetadata[key]
        cacheMetadata[key] = CacheMetadata(
            key = key,
            policy = policy,
            timestamp = System.currentTimeMillis(),
            version = version,
            etag = etag,
            size = size,
            dependencies = dependencies,
            accessCount = (existing?.accessCount ?: 0) + 1,
            lastAccessTime = System.currentTimeMillis()
        )
        
        Timber.d("Cache updated: $key (policy: ${policy::class.simpleName})")
    }
    
    /**
     * Invalidate cache with cascade support
     */
    suspend fun invalidateCache(
        key: String,
        cascade: Boolean = true
    ) = cacheLock.withLock {
        cacheMetadata.remove(key)
        
        if (cascade) {
            // Find all dependent caches
            val dependents = cacheMetadata.values.filter { metadata ->
                metadata.dependencies.contains(key)
            }.map { it.key }
            
            // Invalidate them
            dependents.forEach { dependent ->
                cacheMetadata.remove(dependent)
                Timber.d("Cascade invalidated: $dependent")
            }
        }
        
        Timber.d("Cache invalidated: $key (cascade: $cascade)")
    }
    
    /**
     * Clean up expired caches
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
                is CachePolicy.Dependent -> {
                    now - metadata.timestamp > policy.ttl
                }
                else -> false
            }
        }
        
        expired.keys.forEach { key ->
            cacheMetadata.remove(key)
            Timber.d("Cleaned up expired cache: $key")
        }
        
        Timber.i("Cleaned up ${expired.size} expired caches")
    }
    
    /**
     * Get total cache size in bytes
     */
    suspend fun getTotalCacheSize(): Long = cacheLock.withLock {
        cacheMetadata.values.sumOf { it.size }
    }
    
    /**
     * Evict caches based on LRU (Least Recently Used)
     */
    suspend fun evictLRU(targetSize: Long) = cacheLock.withLock {
        var currentSize = getTotalCacheSize()
        
        if (currentSize <= targetSize) return@withLock
        
        // Sort by timestamp (oldest first)
        val sorted = cacheMetadata.values.sortedBy { it.lastAccessTime }
        
        for (metadata in sorted) {
            if (currentSize <= targetSize) break
            
            // Skip Forever policies
            if (metadata.policy is CachePolicy.Forever) continue
            
            cacheMetadata.remove(metadata.key)
            currentSize -= metadata.size
            
            Timber.d("Evicted cache: ${metadata.key} (size: ${metadata.size})")
        }
        
        Timber.i("LRU eviction completed. New size: $currentSize")
    }
    
    /**
     * Get cache statistics
     */
    suspend fun getStatistics(): CacheStatistics = cacheLock.withLock {
        val totalSize = cacheMetadata.values.sumOf { it.size }
        val totalCount = cacheMetadata.size
        val totalAccesses = cacheMetadata.values.sumOf { it.accessCount }
        
        return CacheStatistics(
            totalSize = totalSize,
            totalCount = totalCount,
            totalAccesses = totalAccesses,
            averageAccessCount = if (totalCount > 0) totalAccesses / totalCount else 0
        )
    }
    
    data class CacheStatistics(
        val totalSize: Long,
        val totalCount: Int,
        val totalAccesses: Int,
        val averageAccessCount: Int
    )
}
