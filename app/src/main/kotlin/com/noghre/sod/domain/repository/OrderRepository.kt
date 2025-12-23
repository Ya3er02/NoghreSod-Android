package com.noghre.sod.domain.repository

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for order operations
 */
interface OrderRepository {
    /**
     * Create order from cart
     */
    suspend fun createOrder(
        shippingAddress: Address,
        billingAddress: Address? = null,
        paymentMethod: PaymentMethod,
        notes: String? = null,
    ): Result<Order>

    /**
     * Get order by ID
     */
    fun getOrderById(orderId: String): Flow<Result<Order>>

    /**
     * Get user's orders
     */
    fun getUserOrders(
        page: Int = 0,
        pageSize: Int = 20,
    ): Flow<Result<List<OrderSummary>>>

    /**
     * Get user's orders with filter
     */
    fun getUserOrdersFiltered(
        filter: OrderFilter,
        page: Int = 0,
        pageSize: Int = 20,
    ): Flow<Result<List<OrderSummary>>>

    /**
     * Cancel order
     */
    suspend fun cancelOrder(
        orderId: String,
        reason: String? = null,
    ): Result<Order>

    /**
     * Get order tracking
     */
    fun getOrderTracking(orderId: String): Flow<Result<OrderTracking>>

    /**
     * Request return
     */
    suspend fun requestReturn(
        orderId: String,
        items: List<OrderItem>,
        reason: ReturnReason,
        description: String,
    ): Result<ReturnRequest>

    /**
     * Get return request
     */
    fun getReturnRequest(returnId: String): Flow<Result<ReturnRequest>>

    /**
     * Get user's return requests
     */
    fun getUserReturnRequests(): Flow<Result<List<ReturnRequest>>>

    /**
     * Verify payment
     */
    suspend fun verifyPayment(
        orderId: String,
        transactionId: String,
    ): Result<PaymentVerification>

    /**
     * Request invoice
     */
    suspend fun requestInvoice(orderId: String): Result<Invoice>

    /**
     * Add order note (customer)
     */
    suspend fun addOrderNote(
        orderId: String,
        note: String,
    ): Result<Unit>

    /**
     * Get order events (tracking history)
     */
    fun getOrderEvents(orderId: String): Flow<Result<List<OrderEvent>>>

    /**
     * Get recent orders
     */
    fun getRecentOrders(limit: Int = 5): Flow<Result<List<OrderSummary>>>
}

/**
 * Order tracking information
 */
data class OrderTracking(
    val orderId: String,
    val status: OrderStatus,
    val events: List<TrackingEvent>,
    val currentLocation: String? = null,
    val estimatedDelivery: java.time.LocalDateTime? = null,
    val carrier: ShippingCarrier? = null,
    val trackingNumber: String? = null,
    val carrier_url: String? = null,
)

/**
 * Tracking event with location
 */
data class TrackingEvent(
    val eventId: String,
    val status: OrderStatus,
    val description: String,
    val location: String? = null,
    val timestamp: java.time.LocalDateTime,
    val nextEstimate: java.time.LocalDateTime? = null,
)

/**
 * Payment verification result
 */
data class PaymentVerification(
    val isValid: Boolean,
    val orderId: String,
    val transactionId: String,
    val amount: Long,
    val currency: String,
    val timestamp: java.time.LocalDateTime,
    val message: String? = null,
)

/**
 * Invoice document
 */
data class Invoice(
    val invoiceId: String,
    val orderId: String,
    val invoiceNumber: String,
    val date: java.time.LocalDateTime,
    val items: List<OrderItem>,
    val subtotal: Long,
    val tax: Long,
    val total: Long,
    val businessInfo: BusinessInfo,
    val url: String? = null,
)

/**
 * Business information for invoice
 */
data class BusinessInfo(
    val name: String,
    val nameInFarsi: String,
    val phone: String,
    val email: String,
    val website: String? = null,
    val businessCode: String? = null,
    val taxId: String? = null,
    val address: String,
    val addressInFarsi: String,
)
