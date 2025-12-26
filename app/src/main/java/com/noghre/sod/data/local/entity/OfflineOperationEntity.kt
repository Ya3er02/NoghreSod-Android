package com.noghre.sod.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entity representing an operation that needs to be synced when offline.
 *
 * The offline-first architecture queues all operations when network is unavailable
 * and syncs them once connectivity is restored.
 *
 * @param id Unique operation ID
 * @param type Operation type (ADD_TO_CART, REMOVE_FROM_CART, UPDATE_QUANTITY, PLACE_ORDER, etc)
 * @param resourceId The ID of the resource being operated on (product ID, order ID, etc)
 * @param payload JSON string containing operation details
 * @param status Operation status (PENDING, SYNCING, SUCCESS, FAILED)
 * @param retryCount Number of retry attempts
 * @param maxRetries Maximum allowed retries
 * @param createdAt Timestamp when operation was created
 * @param updatedAt Timestamp of last update
 * @param syncedAt Timestamp when successfully synced (null if not synced)
 * @param errorMessage Error message if sync failed
 */
@Entity(tableName = "offline_operations")
data class OfflineOperationEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    val type: String, // e.g., "ADD_TO_CART", "REMOVE_FROM_CART", "UPDATE_QUANTITY", "PLACE_ORDER"
    val resourceId: String, // Product ID, Order ID, etc
    val payload: String, // JSON string with operation data
    
    val status: String = "PENDING", // PENDING, SYNCING, SUCCESS, FAILED
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncedAt: Long? = null,
    
    val errorMessage: String? = null
) {
    /**
     * Operation types supported
     */
    companion object {
        const val TYPE_ADD_TO_CART = "ADD_TO_CART"
        const val TYPE_REMOVE_FROM_CART = "REMOVE_FROM_CART"
        const val TYPE_UPDATE_QUANTITY = "UPDATE_QUANTITY"
        const val TYPE_PLACE_ORDER = "PLACE_ORDER"
        const val TYPE_UPDATE_PROFILE = "UPDATE_PROFILE"
        const val TYPE_APPLY_COUPON = "APPLY_COUPON"
        
        const val STATUS_PENDING = "PENDING"
        const val STATUS_SYNCING = "SYNCING"
        const val STATUS_SUCCESS = "SUCCESS"
        const val STATUS_FAILED = "FAILED"
    }
    
    /**
     * Check if operation should be retried
     */
    fun shouldRetry(): Boolean = status == STATUS_FAILED && retryCount < maxRetries
    
    /**
     * Check if operation can be synced (not already syncing)
     */
    fun canSync(): Boolean = status == STATUS_PENDING || (status == STATUS_FAILED && shouldRetry())
    
    /**
     * Get next retry delay in milliseconds (exponential backoff)
     * Formula: initialDelay * (2 ^ retryCount)
     * 1st retry: 1000ms
     * 2nd retry: 2000ms
     * 3rd retry: 4000ms
     */
    fun getNextRetryDelay(): Long {
        val initialDelay = 1000L
        return initialDelay * (1 shl retryCount)  // 2^retryCount
    }
}
