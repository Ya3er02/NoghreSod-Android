package com.noghre.sod.data.database.dao

import androidx.room.*
import com.noghre.sod.data.database.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for payment operations
 * 
 * Responsibilities:
 * - Insert new payments after processing
 * - Query payment history by payment ID
 * - Query all payments for a specific order
 * - Update payment status
 * - Delete stale payment records
 * 
 * All queries are suspend functions for coroutine support.
 * Flow<> queries provide reactive updates.
 */
@Dao
interface PaymentDao {
    
    /**
     * Insert a new payment record
     * OnConflict strategy: REPLACE to update if already exists
     * 
     * @param payment Payment entity to insert
     * @return Database row ID of inserted payment
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long
    
    /**
     * Insert multiple payments in single transaction
     * 
     * @param payments List of payment entities
     * @return List of inserted row IDs
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentEntity>): List<Long>
    
    /**
     * Query payment by ID
     * Returns single record if found
     * 
     * @param paymentId Unique payment identifier
     * @return PaymentEntity if found, null otherwise
     */
    @Query("SELECT * FROM payments WHERE id = :paymentId LIMIT 1")
    suspend fun getPaymentById(paymentId: String): PaymentEntity?
    
    /**
     * Query payment by authority (gateway reference)
     * Used to check if payment already verified
     * 
     * @param authority Payment gateway authority code
     * @return PaymentEntity if found, null otherwise
     */
    @Query("SELECT * FROM payments WHERE authority = :authority LIMIT 1")
    suspend fun getPaymentByAuthority(authority: String): PaymentEntity?
    
    /**
     * Query all payments for a specific order
     * Returns most recent first (order by createdAt DESC)
     * 
     * @param orderId Order identifier
     * @return List of payments for order (may be empty)
     */
    @Query(
        """SELECT * FROM payments 
           WHERE orderId = :orderId 
           ORDER BY createdAt DESC"""
    )
    suspend fun getPaymentsByOrderId(orderId: String): List<PaymentEntity>
    
    /**
     * Reactive query: Watch all payments for an order
     * Updates emitted whenever payments change
     * 
     * @param orderId Order identifier
     * @return Flow emitting payment lists when data changes
     */
    @Query(
        """SELECT * FROM payments 
           WHERE orderId = :orderId 
           ORDER BY createdAt DESC"""
    )
    fun observePaymentsByOrderId(orderId: String): Flow<List<PaymentEntity>>
    
    /**
     * Update payment status (e.g., PENDING -> SUCCESS)
     * Typically after verification
     * 
     * @param paymentId Payment identifier
     * @param status New payment status
     * @param refId Reference ID from gateway (optional)
     */
    @Query(
        """UPDATE payments 
           SET status = :status, refId = :refId, paidAt = :paidAt 
           WHERE id = :paymentId"""
    )
    suspend fun updatePaymentStatus(
        paymentId: String,
        status: String,
        refId: String?,
        paidAt: Long?
    )
    
    /**
     * Delete single payment record
     * Typically used for cleanup of failed/expired payments
     * 
     * @param paymentId Payment identifier to delete
     * @return Number of rows deleted
     */
    @Query("DELETE FROM payments WHERE id = :paymentId")
    suspend fun deletePayment(paymentId: String): Int
    
    /**
     * Delete all payments for an order
     * Use with caution - used for order cleanup
     * 
     * @param orderId Order identifier
     * @return Number of rows deleted
     */
    @Query("DELETE FROM payments WHERE orderId = :orderId")
    suspend fun deletePaymentsByOrderId(orderId: String): Int
    
    /**
     * Count total payments in database
     * Useful for analytics and cleanup decisions
     * 
     * @return Total number of payment records
     */
    @Query("SELECT COUNT(*) FROM payments")
    suspend fun getPaymentCount(): Int
    
    /**
     * Clear all payment records
     * WARNING: Use only in cleanup/logout flows
     * 
     * @return Number of rows deleted
     */
    @Query("DELETE FROM payments")
    suspend fun clearAllPayments(): Int
}
