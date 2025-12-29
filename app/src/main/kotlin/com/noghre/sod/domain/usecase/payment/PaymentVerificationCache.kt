package com.noghre.sod.domain.usecase.payment

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

/**
 * Cache برای تایید وریفیککردن transactionها
 * 
 * قابلیتها:
 * - جلوگیری از replay attacks
 * - بررسی زمانی windowing
 * - تعين نشان verified transactionها
 */
interface PaymentVerificationCache {
    /**
     * بررسی اگر authority قبلاً وریفاي شده است
     */
    suspend fun isAlreadyVerified(authority: String): Boolean
    
    /**
     * بررسی اگر verification در time window قرار دارد
     */
    suspend fun isWithinWindow(authority: String, windowMs: Long): Boolean
    
    /**
     * علامت‌گذاری transaction به عنوان verified
     */
    suspend fun markAsVerified(authority: String, refId: String, timestamp: Long)
    
    /**
     * بررسی مناعی verified transactions
     */
    suspend fun getVerifiedCount(): Int
}

/**
 * In-memory cache implementation برای verification state
 * 
 * وقتاً شوما production-ready هستید، 
 * همینطور به Room database migrate کنید
 */
@Singleton
class InMemoryPaymentVerificationCache : PaymentVerificationCache {
    private val verifiedTransactions = mutableMapOf<String, VerificationRecord>()
    private val lock = java.util.concurrent.locks.ReentrantReadWriteLock()
    
    override suspend fun isAlreadyVerified(authority: String): Boolean {
        return withContext(Dispatchers.Default) {
            lock.readLock().lock()
            try {
                verifiedTransactions.containsKey(authority) && 
                verifiedTransactions[authority]?.isVerified == true
            } finally {
                lock.readLock().unlock()
            }
        }
    }
    
    override suspend fun isWithinWindow(authority: String, windowMs: Long): Boolean {
        return withContext(Dispatchers.Default) {
            lock.readLock().lock()
            try {
                val record = verifiedTransactions[authority]
                if (record == null) return@withContext true // No record = within window
                
                val elapsed = System.currentTimeMillis() - record.createdAt
                elapsed < windowMs
            } finally {
                lock.readLock().unlock()
            }
        }
    }
    
    override suspend fun markAsVerified(authority: String, refId: String, timestamp: Long) {
        return withContext(Dispatchers.Default) {
            lock.writeLock().lock()
            try {
                verifiedTransactions[authority] = VerificationRecord(
                    authority = authority,
                    refId = refId,
                    createdAt = System.currentTimeMillis(),
                    verifiedAt = timestamp,
                    isVerified = true
                )
            } finally {
                lock.writeLock().unlock()
            }
        }
    }
    
    override suspend fun getVerifiedCount(): Int {
        return withContext(Dispatchers.Default) {
            lock.readLock().lock()
            try {
                verifiedTransactions.values.count { it.isVerified }
            } finally {
                lock.readLock().unlock()
            }
        }
    }
    
    /**
     * پاان کردن cache (برای testing)
     */
    fun clear() {
        lock.writeLock().lock()
        try {
            verifiedTransactions.clear()
        } finally {
            lock.writeLock().unlock()
        }
    }
}

data class VerificationRecord(
    val authority: String,
    val refId: String,
    val createdAt: Long,
    val verifiedAt: Long,
    val isVerified: Boolean = true
)
