package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.local.entity.OfflineOperationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for offline operations.
 *
 * Manages the queue of operations that need to be synced when network is available.
 * Supports CRUD operations and various queries for sync management.
 */
@Dao
interface OfflineOperationDao {
    
    /**
     * Insert a new offline operation.
     * @param operation The operation to insert
     * @return The row ID of the inserted operation
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: OfflineOperationEntity): Long
    
    /**
     * Insert multiple offline operations.
     * @param operations List of operations to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperations(operations: List<OfflineOperationEntity>)
    
    /**
     * Update an existing offline operation.
     * @param operation The operation to update
     * @return Number of rows affected
     */
    @Update
    suspend fun updateOperation(operation: OfflineOperationEntity): Int
    
    /**
     * Delete an offline operation.
     * @param operation The operation to delete
     * @return Number of rows affected
     */
    @Delete
    suspend fun deleteOperation(operation: OfflineOperationEntity): Int
    
    /**
     * Delete an operation by ID.
     * @param operationId The ID of the operation to delete
     */
    @Query("DELETE FROM offline_operations WHERE id = :operationId")
    suspend fun deleteOperationById(operationId: String)
    
    /**
     * Get all pending operations.
     * @return Flow of pending operations
     */
    @Query("SELECT * FROM offline_operations WHERE status = 'PENDING' ORDER BY createdAt ASC")
    fun getPendingOperations(): Flow<List<OfflineOperationEntity>>
    
    /**
     * Get single pending operation for syncing.
     * @return One pending operation or null
     */
    @Query("SELECT * FROM offline_operations WHERE status = 'PENDING' ORDER BY createdAt ASC LIMIT 1")
    suspend fun getNextPendingOperation(): OfflineOperationEntity?
    
    /**
     * Get operations that can be retried (failed but under max retries).
     * @return List of failed operations that can be retried
     */
    @Query("SELECT * FROM offline_operations WHERE status = 'FAILED' AND retryCount < maxRetries ORDER BY createdAt ASC")
    suspend fun getFailedOperationsForRetry(): List<OfflineOperationEntity>
    
    /**
     * Get all operations with specific type.
     * @param type Operation type (e.g., "ADD_TO_CART")
     * @return List of operations with that type
     */
    @Query("SELECT * FROM offline_operations WHERE type = :type ORDER BY createdAt DESC")
    suspend fun getOperationsByType(type: String): List<OfflineOperationEntity>
    
    /**
     * Get operations for a specific resource.
     * @param resourceId The resource ID (product ID, order ID, etc)
     * @return List of operations for that resource
     */
    @Query("SELECT * FROM offline_operations WHERE resourceId = :resourceId ORDER BY createdAt DESC")
    suspend fun getOperationsByResourceId(resourceId: String): List<OfflineOperationEntity>
    
    /**
     * Get all operations by status.
     * @param status Status filter (PENDING, SYNCING, SUCCESS, FAILED)
     * @return List of operations with that status
     */
    @Query("SELECT * FROM offline_operations WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getOperationsByStatus(status: String): List<OfflineOperationEntity>
    
    /**
     * Get operation by ID.
     * @param operationId The operation ID
     * @return The operation or null if not found
     */
    @Query("SELECT * FROM offline_operations WHERE id = :operationId")
    suspend fun getOperationById(operationId: String): OfflineOperationEntity?
    
    /**
     * Get count of pending operations.
     * @return Number of pending operations
     */
    @Query("SELECT COUNT(*) FROM offline_operations WHERE status = 'PENDING'")
    suspend fun getPendingOperationCount(): Int
    
    /**
     * Get count of all operations.
     * @return Total number of operations
     */
    @Query("SELECT COUNT(*) FROM offline_operations")
    suspend fun getTotalOperationCount(): Int
    
    /**
     * Check if there are any pending operations.
     * @return True if there are pending operations
     */
    @Query("SELECT EXISTS(SELECT 1 FROM offline_operations WHERE status = 'PENDING' LIMIT 1)")
    suspend fun hasPendingOperations(): Boolean
    
    /**
     * Get all operations (useful for debugging).
     * @return Flow of all operations
     */
    @Query("SELECT * FROM offline_operations ORDER BY createdAt DESC")
    fun getAllOperations(): Flow<List<OfflineOperationEntity>>
    
    /**
     * Delete all synced operations (cleanup).
     */
    @Query("DELETE FROM offline_operations WHERE status = 'SUCCESS'")
    suspend fun deleteSuccessfulOperations()
    
    /**
     * Delete operations older than specified time (cleanup old failed operations).
     * @param timestamp Delete operations created before this timestamp
     */
    @Query("DELETE FROM offline_operations WHERE createdAt < :timestamp")
    suspend fun deleteOperationsOlderThan(timestamp: Long)
    
    /**
     * Mark operation as syncing.
     * @param operationId The operation ID
     */
    @Query("UPDATE offline_operations SET status = 'SYNCING', updatedAt = :now WHERE id = :operationId")
    suspend fun markAsSyncing(operationId: String, now: Long = System.currentTimeMillis())
    
    /**
     * Mark operation as successfully synced.
     * @param operationId The operation ID
     */
    @Query("UPDATE offline_operations SET status = 'SUCCESS', syncedAt = :now, updatedAt = :now WHERE id = :operationId")
    suspend fun markAsSuccessful(operationId: String, now: Long = System.currentTimeMillis())
    
    /**
     * Mark operation as failed and increment retry count.
     * @param operationId The operation ID
     * @param errorMessage The error message
     */
    @Query("UPDATE offline_operations SET status = 'FAILED', retryCount = retryCount + 1, errorMessage = :errorMessage, updatedAt = :now WHERE id = :operationId")
    suspend fun markAsFailed(operationId: String, errorMessage: String?, now: Long = System.currentTimeMillis())
}
