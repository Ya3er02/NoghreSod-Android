package com.noghre.sod.domain.repository

import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.model.OrderTrackingInfo

interface OrderRepository {
    suspend fun createOrder(
        addressId: String,
        paymentMethod: String,
        notes: String? = null
    ): Order

    suspend fun getOrders(
        page: Int = 1,
        status: String? = null
    ): List<Order>

    suspend fun getOrderById(orderId: String): Order

    suspend fun cancelOrder(
        orderId: String,
        reason: String? = null
    ): Order

    suspend fun trackOrder(orderId: String): OrderTrackingInfo
}
