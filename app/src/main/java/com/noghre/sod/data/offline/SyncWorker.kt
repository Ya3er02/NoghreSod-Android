package com.noghre.sod.data.offline

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.noghre.sod.data.local.dao.OfflineOperationDao
import com.noghre.sod.data.local.entity.OfflineOperationEntity
import com.noghre.sod.data.network.NetworkMonitor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * Background worker for syncing offline operations.
 *
 * Uses WorkManager to schedule and execute offline operation syncing
 * with intelligent retry policy and network constraints.
 *
 * @param context Android context
 * @param params Worker parameters from WorkManager
 * @param offlineOperationDao Database access object
 * @param networkMonitor Network connectivity monitor
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: androidx.work.WorkerParameters,
    private val offlineOperationDao: OfflineOperationDao,
    private val networkMonitor: NetworkMonitor
) : CoroutineWorker(context, params) {
    companion object {
        private const val TAG = "SyncWorker"
        private const val UNIQUE_WORK_NAME = "offline_sync_work"
        private const val MAX_SYNC_ATTEMPTS = 5
    }

    override suspend fun doWork(): Result {
        return try {
            // Check network availability
            if (!networkMonitor.isCurrentlyOnline()) {
                Log.d(TAG, "No network available, retrying later")
                return Result.retry()
            }

            Log.d(TAG, "Starting offline sync")

            val pendingCount = offlineOperationDao.getPendingOperationCount()
            if (pendingCount == 0) {
                Log.d(TAG, "No pending operations to sync")
                return Result.success()
            }

            Log.d(TAG, "Found $pendingCount pending operations")

            var syncedCount = 0
            var failedCount = 0

            // Process each pending operation
            while (true) {
                val operation = offlineOperationDao.getNextPendingOperation() ?: break

                try {
                    offlineOperationDao.markAsSyncing(operation.id)
                    Log.d(TAG, "Syncing operation: ${operation.id} (type: ${operation.type})")

                    // Perform the actual sync based on operation type
                    val success = syncOperation(operation)

                    if (success) {
                        offlineOperationDao.markAsSuccessful(operation.id)
                        syncedCount++
                        Log.d(TAG, "Operation ${operation.id} synced successfully")
                    } else {
                        handleFailedOperation(operation)
                        failedCount++
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error syncing operation ${operation.id}", e)
                    handleFailedOperation(operation, e.message)
                    failedCount++
                }
            }

            Log.d(TAG, "Sync complete. Synced: $syncedCount, Failed: $failedCount")

            return if (failedCount == 0) {
                // All operations synced successfully
                Result.success(workDataOf(
                    "synced_count" to syncedCount,
                    "failed_count" to failedCount
                ))
            } else {
                // Some operations failed, will be retried next time
                Result.success(workDataOf(
                    "synced_count" to syncedCount,
                    "failed_count" to failedCount
                ))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in SyncWorker", e)
            Result.retry()
        }
    }

    /**
     * Sync a single operation.
     *
     * @param operation The operation to sync
     * @return True if sync was successful
     */
    private suspend fun syncOperation(operation: OfflineOperationEntity): Boolean {
        // This is a placeholder - actual implementation would call API
        // In real scenario, this would dispatch to appropriate service/repository
        return when (operation.type) {
            OfflineOperationEntity.TYPE_ADD_TO_CART -> syncAddToCart(operation)
            OfflineOperationEntity.TYPE_REMOVE_FROM_CART -> syncRemoveFromCart(operation)
            OfflineOperationEntity.TYPE_UPDATE_QUANTITY -> syncUpdateQuantity(operation)
            OfflineOperationEntity.TYPE_PLACE_ORDER -> syncPlaceOrder(operation)
            OfflineOperationEntity.TYPE_UPDATE_PROFILE -> syncUpdateProfile(operation)
            OfflineOperationEntity.TYPE_APPLY_COUPON -> syncApplyCoupon(operation)
            else -> {
                Log.w(TAG, "Unknown operation type: ${operation.type}")
                false
            }
        }
    }

    /**
     * Handle failed operation with retry logic.
     *
     * @param operation The failed operation
     * @param errorMessage Optional error message
     */
    private suspend fun handleFailedOperation(
        operation: OfflineOperationEntity,
        errorMessage: String? = null
    ) {
        val shouldRetry = operation.shouldRetry()
        if (shouldRetry) {
            offlineOperationDao.markAsFailed(operation.id, errorMessage)
            Log.d(TAG, "Operation ${operation.id} marked for retry")
        } else {
            offlineOperationDao.markAsFailed(operation.id, "Max retries exceeded: $errorMessage")
            Log.e(TAG, "Operation ${operation.id} reached max retries")
        }
    }

    /**
     * Placeholder sync methods for different operation types.
     * These would be implemented with actual API calls.
     */
    private suspend fun syncAddToCart(operation: OfflineOperationEntity): Boolean {
        // TODO: Call CartService.addToCart with payload
        return true
    }

    private suspend fun syncRemoveFromCart(operation: OfflineOperationEntity): Boolean {
        // TODO: Call CartService.removeFromCart with payload
        return true
    }

    private suspend fun syncUpdateQuantity(operation: OfflineOperationEntity): Boolean {
        // TODO: Call CartService.updateQuantity with payload
        return true
    }

    private suspend fun syncPlaceOrder(operation: OfflineOperationEntity): Boolean {
        // TODO: Call OrderService.placeOrder with payload
        return true
    }

    private suspend fun syncUpdateProfile(operation: OfflineOperationEntity): Boolean {
        // TODO: Call UserService.updateProfile with payload
        return true
    }

    private suspend fun syncApplyCoupon(operation: OfflineOperationEntity): Boolean {
        // TODO: Call CartService.applyCoupon with payload
        return true
    }
}

/**
 * Schedule offline sync work with WorkManager.
 *
 * @param context Android context
 * @param networkMonitor Network monitor for constraint checking
 */
fun scheduleSyncWork(context: Context, networkMonitor: NetworkMonitor) {
    if (!networkMonitor.isCurrentlyOnline()) {
        Log.d(SyncWorker.TAG, "No network available, deferring sync scheduling")
        return
    }

    // Create work request with constraints
    val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(false) // Can run on low battery
                .build()
        )
        .setBackoffPolicy(
            BackoffPolicy.EXPONENTIAL,
            15, // Initial backoff: 15 seconds
            TimeUnit.SECONDS
        )
        .addTag("offline_sync")
        .build()

    // Enqueue with REPLACE policy to avoid duplicate work
    WorkManager.getInstance(context).enqueueUniqueWork(
        SyncWorker.UNIQUE_WORK_NAME,
        ExistingWorkPolicy.REPLACE,
        syncWorkRequest
    )

    Log.d(SyncWorker.TAG, "Sync work scheduled")
}

/**
 * Cancel pending sync work.
 *
 * @param context Android context
 */
fun cancelSyncWork(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork(SyncWorker.UNIQUE_WORK_NAME)
    Log.d(SyncWorker.TAG, "Sync work cancelled")
}
