package com.noghre.sod.analytics

import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data class representing an analytics event.
 *
 * @param eventName Name of the event
 * @param bundle Event parameters
 * @param timestamp When the event occurred
 */
data class AnalyticsEvent(
    val eventName: String,
    val bundle: Bundle?,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Analytics Repository for managing event queuing and persistence.
 *
 * Provides:
 * - Event queue management for offline capability
 * - Event batching for efficient Firebase syncing
 * - Local persistence of events (when implemented with Room)
 * - Event stream for monitoring
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
 * @version 1.0.0
 */
@Singleton
class AnalyticsRepository @Inject constructor(
    private val analyticsManager: AnalyticsManager
) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val eventQueue = mutableListOf<AnalyticsEvent>()
    private val _eventFlow = MutableSharedFlow<AnalyticsEvent>(replay = 100)
    val eventFlow = _eventFlow.asSharedFlow()

    // Configuration
    companion object {
        const val MAX_QUEUE_SIZE = 500
        const val BATCH_SIZE = 50
    }

    /**
     * Queue an event for later processing.
     *
     * Events are queued when network is unavailable and processed
     * when connectivity is restored.
     *
     * @param event AnalyticsEvent to queue
     */
    fun queueEvent(event: AnalyticsEvent) {
        synchronized(eventQueue) {
            if (eventQueue.size >= MAX_QUEUE_SIZE) {
                Timber.w("Event queue full, dropping oldest events")
                eventQueue.removeRange(0, BATCH_SIZE)
            }
            eventQueue.add(event)
        }

        scope.launch {
            _eventFlow.emit(event)
        }

        Timber.d("Event queued: ${event.eventName} (Queue size: ${eventQueue.size})")
    }

    /**
     * Log event directly (immediately sends to Firebase).
     */
    fun logEvent(
        eventName: String,
        bundle: Bundle?
    ) {
        val event = AnalyticsEvent(eventName, bundle)
        analyticsManager.logEvent(eventName, bundle)
        queueEvent(event)
    }

    /**
     * Process all queued events.
     *
     * Batches events and sends them to Firebase Analytics.
     * Should be called when network becomes available.
     */
    suspend fun processQueue() {
        synchronized(eventQueue) {
            if (eventQueue.isEmpty()) {
                Timber.d("Event queue is empty")
                return
            }

            Timber.i("Processing event queue: ${eventQueue.size} events")
            val eventsToProcess = eventQueue.toList()
            eventQueue.clear()

            // Process in batches
            eventsToProcess.chunked(BATCH_SIZE).forEach { batch ->
                batch.forEach { event ->
                    try {
                        analyticsManager.logEvent(event.eventName, event.bundle)
                        Timber.d("Processed event: ${event.eventName}")
                    } catch (e: Exception) {
                        Timber.e(e, "Error processing event: ${event.eventName}")
                        // Requeue failed event
                        queueEvent(event)
                    }
                }
            }
        }
    }

    /**
     * Clear all queued events.
     */
    fun clearQueue() {
        synchronized(eventQueue) {
            val size = eventQueue.size
            eventQueue.clear()
            Timber.d("Event queue cleared ($size events removed)")
        }
    }

    /**
     * Get current queue size.
     */
    fun getQueueSize(): Int {
        synchronized(eventQueue) {
            return eventQueue.size
        }
    }

    /**
     * Get all queued events (for debugging).
     */
    fun getQueuedEvents(): List<AnalyticsEvent> {
        synchronized(eventQueue) {
            return eventQueue.toList()
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
        price: Double? = null
    ) {
        val bundle = Bundle().apply {
            putString("product_id", productId)
            putString("product_name", productName)
            category?.let { putString("category", it) }
            price?.let { putDouble("price", it) }
        }
        logEvent(eventName, bundle)
    }

    /**
     * Log order-specific event.
     */
    fun logOrderEvent(
        eventName: String,
        orderId: String,
        totalAmount: Double,
        itemCount: Int,
        paymentMethod: String? = null
    ) {
        val bundle = Bundle().apply {
            putString("order_id", orderId)
            putDouble("amount", totalAmount)
            putInt("item_count", itemCount)
            paymentMethod?.let { putString("payment_method", it) }
        }
        logEvent(eventName, bundle)
    }

    /**
     * Log user-specific event.
     */
    fun logUserEvent(
        eventName: String,
        userId: String,
        eventData: Map<String, String>? = null
    ) {
        val bundle = Bundle().apply {
            putString("user_id", userId)
            eventData?.forEach { (key, value) ->
                putString(key, value)
            }
        }
        logEvent(eventName, bundle)
    }

    /**
     * Log error event.
     */
    fun logErrorEvent(
        errorMessage: String,
        errorCode: String? = null,
        stackTrace: String? = null
    ) {
        val bundle = Bundle().apply {
            putString("error_message", errorMessage)
            errorCode?.let { putString("error_code", it) }
            stackTrace?.let { putString("stack_trace", it) }
        }
        logEvent("app_error", bundle)
    }

    /**
     * Get analytics dashboard summary.
     */
    fun getDashboardSummary(): Map<String, Any> {
        return mapOf(
            "queue_size" to getQueueSize(),
            "max_queue_size" to MAX_QUEUE_SIZE,
            "batch_size" to BATCH_SIZE,
            "events_in_queue" to getQueuedEvents().size
        )
    }
}
