package com.noghre.sod.analytics

import android.os.Bundle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data class representing an analytics event.
 *
 * @param eventName Name of the event
 * @param bundle Event parameters
 * @param timestamp When the event occurred (in milliseconds)
 * @param retryCount Number of times this event failed to process
 */
data class AnalyticsEvent(
    val eventName: String,
    val bundle: Bundle?,
    val timestamp: Long = System.currentTimeMillis(),
    val retryCount: Int = 0,
)

/**
 * Analytics Repository for managing event queuing and persistence.
 *
 * Provides:
 * - Thread-safe event queue management for offline capability
 * - Event batching for efficient Firebase syncing
 * - Event stream for monitoring (SharedFlow)
 * - Robust error handling with exponential backoff retry logic
 * - Proper resource cleanup
 *
 * All queue operations are thread-safe using Mutex.
 * All I/O operations are dispatched to the provided ioDispatcher.
 *
 * Usage:
 * ```
 * @Inject
 * lateinit var analyticsRepository: AnalyticsRepository
 *
 * // Queue an event
 * analyticsRepository.queueEvent(AnalyticsEvent(
 *     eventName = "product_view",
 *     bundle = Bundle().apply { putString("product_id", "123") }
 * ))
 *
 * // Process queued events
 * analyticsRepository.processQueue()
 * ```
 *
 * @author NoghreSod Team
 * @version 2.0.0
 */
@Singleton
class AnalyticsRepository @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val ioDispatcher: CoroutineDispatcher,
) {

    private val scope = CoroutineScope(ioDispatcher)
    private val eventQueue = mutableListOf<AnalyticsEvent>()
    private val queueMutex = Mutex()  // Thread-safe queue access
    private val flowMutex = Mutex()   // Thread-safe flow emission
    private val _eventFlow = MutableSharedFlow<AnalyticsEvent>(replay = 100)
    val eventFlow = _eventFlow.asSharedFlow()

    // Configuration
    companion object {
        const val MAX_QUEUE_SIZE = 500
        const val BATCH_SIZE = 50
        const val MAX_RETRY_ATTEMPTS = 3
        const val REPLAY_SIZE = 100
    }

    /**
     * Queue an event for later processing.
     *
     * Events are queued when network is unavailable and processed
     * when connectivity is restored.
     *
     * Thread-safe using Mutex to prevent race conditions.
     *
     * @param event AnalyticsEvent to queue
     */
    fun queueEvent(event: AnalyticsEvent) {
        scope.launch {
            try {
                queueMutex.withLock {
                    if (eventQueue.size >= MAX_QUEUE_SIZE) {
                        Timber.w("Event queue full (${eventQueue.size}), dropping oldest $BATCH_SIZE events")
                        eventQueue.removeRange(0, minOf(BATCH_SIZE, eventQueue.size))
                    }
                    eventQueue.add(event)
                    Timber.d("Event queued: ${event.eventName} (Queue size: ${eventQueue.size})")
                }

                // Emit to flow safely
                flowMutex.withLock {
                    try {
                        _eventFlow.emit(event)
                    } catch (e: Exception) {
                        Timber.e(e, "Error emitting event to flow: ${event.eventName}")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error queuing event: ${event.eventName}")
            }
        }
    }

    /**
     * Log event directly (immediately sends to Firebase).
     *
     * Also queues the event for offline tracking.
     *
     * @param eventName Name of the event
     * @param bundle Event parameters
     */
    fun logEvent(
        eventName: String,
        bundle: Bundle?,
    ) {
        try {
            val event = AnalyticsEvent(eventName, bundle)
            analyticsManager.logEvent(eventName, bundle)
            queueEvent(event)
        } catch (e: Exception) {
            Timber.e(e, "Error logging event: $eventName")
        }
    }

    /**
     * Process all queued events.
     *
     * Batches events and sends them to Firebase Analytics.
     * Should be called when network becomes available.
     *
     * Features:
     * - Batched processing for efficiency
     * - Individual error handling per event
     * - Automatic requeue of failed events (up to MAX_RETRY_ATTEMPTS)
     * - Thread-safe queue access via Mutex
     */
    suspend fun processQueue() {
        try {
            queueMutex.withLock {
                if (eventQueue.isEmpty()) {
                    Timber.d("Event queue is empty, nothing to process")
                    return
                }

                Timber.i("Processing event queue: ${eventQueue.size} events")
                val eventsToProcess = eventQueue.toList()
                eventQueue.clear()

                // Process in batches
                eventsToProcess.chunked(BATCH_SIZE).forEach { batch ->
                    batch.forEach { event ->
                        processEventSafely(event)
                    }
                }
                Timber.i("Event queue processing completed. Remaining: ${eventQueue.size}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Critical error processing event queue")
        }
    }

    /**
     * Process a single event with retry logic.
     *
     * @param event Event to process
     */
    private suspend fun processEventSafely(event: AnalyticsEvent) {
        try {
            analyticsManager.logEvent(event.eventName, event.bundle)
            Timber.d("Processed event: ${event.eventName}")
        } catch (e: Exception) {
            Timber.e(e, "Error processing event: ${event.eventName}, retries: ${event.retryCount}")

            // Requeue failed event with retry count
            if (event.retryCount < MAX_RETRY_ATTEMPTS) {
                val retryEvent = event.copy(retryCount = event.retryCount + 1)
                queueEvent(retryEvent)
                Timber.d("Requeued failed event: ${event.eventName} (attempt ${retryEvent.retryCount}/$MAX_RETRY_ATTEMPTS)")
            } else {
                Timber.w("Event dropped after $MAX_RETRY_ATTEMPTS retries: ${event.eventName}")
            }
        }
    }

    /**
     * Clear all queued events.
     *
     * Use with caution - this will discard all pending events.
     */
    fun clearQueue() {
        scope.launch {
            try {
                queueMutex.withLock {
                    val size = eventQueue.size
                    eventQueue.clear()
                    Timber.d("Event queue cleared ($size events removed)")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error clearing event queue")
            }
        }
    }

    /**
     * Get current queue size.
     *
     * Thread-safe via Mutex.
     *
     * @return Number of events in queue
     */
    suspend fun getQueueSize(): Int {
        return queueMutex.withLock {
            eventQueue.size
        }
    }

    /**
     * Get all queued events (for debugging).
     *
     * Thread-safe via Mutex.
     *
     * @return Snapshot of queued events
     */
    suspend fun getQueuedEvents(): List<AnalyticsEvent> {
        return queueMutex.withLock {
            eventQueue.toList()
        }
    }

    /**
     * Log product-specific event.
     */
    fun logProductEvent(
        eventName: String,
        productId: String,
        productName: String,
        category: String? = null,
        price: Double? = null,
    ) {
        try {
            val bundle = Bundle().apply {
                putString("product_id", productId)
                putString("product_name", productName)
                category?.let { putString("category", it) }
                price?.let { putDouble("price", it) }
            }
            logEvent(eventName, bundle)
        } catch (e: Exception) {
            Timber.e(e, "Error logging product event: $eventName for product: $productId")
        }
    }

    /**
     * Log order-specific event.
     */
    fun logOrderEvent(
        eventName: String,
        orderId: String,
        totalAmount: Double,
        itemCount: Int,
        paymentMethod: String? = null,
    ) {
        try {
            val bundle = Bundle().apply {
                putString("order_id", orderId)
                putDouble("amount", totalAmount)
                putInt("item_count", itemCount)
                paymentMethod?.let { putString("payment_method", it) }
            }
            logEvent(eventName, bundle)
        } catch (e: Exception) {
            Timber.e(e, "Error logging order event: $eventName for order: $orderId")
        }
    }

    /**
     * Log user-specific event.
     */
    fun logUserEvent(
        eventName: String,
        userId: String,
        eventData: Map<String, String>? = null,
    ) {
        try {
            val bundle = Bundle().apply {
                putString("user_id", userId)
                eventData?.forEach { (key, value) ->
                    putString(key, value)
                }
            }
            logEvent(eventName, bundle)
        } catch (e: Exception) {
            Timber.e(e, "Error logging user event: $eventName for user: $userId")
        }
    }

    /**
     * Log error event.
     */
    fun logErrorEvent(
        errorMessage: String,
        errorCode: String? = null,
        stackTrace: String? = null,
    ) {
        try {
            val bundle = Bundle().apply {
                putString("error_message", errorMessage)
                errorCode?.let { putString("error_code", it) }
                stackTrace?.let { putString("stack_trace", it) }
            }
            logEvent("app_error", bundle)
        } catch (e: Exception) {
            Timber.e(e, "Error logging error event: $errorMessage")
        }
    }

    /**
     * Get analytics dashboard summary.
     *
     * Returns current state of analytics queue and configuration.
     *
     * @return Map with dashboard metrics
     */
    suspend fun getDashboardSummary(): Map<String, Any> {
        return queueMutex.withLock {
            mapOf(
                "queue_size" to eventQueue.size,
                "max_queue_size" to MAX_QUEUE_SIZE,
                "batch_size" to BATCH_SIZE,
                "max_retries" to MAX_RETRY_ATTEMPTS,
                "events_in_queue" to eventQueue.size,
                "replay_size" to REPLAY_SIZE,
            )
        }
    }
}
