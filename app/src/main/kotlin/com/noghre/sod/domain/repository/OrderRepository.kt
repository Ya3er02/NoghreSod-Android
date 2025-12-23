package com.noghre.sod.domain.repository

import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.model.OrderStatus
import com.noghre.sod.domain.model.OrderStatusHistory
import com.noghre.sod.domain.model.PaymentMethod

interface OrderRepository {
    suspend fun createOrder(
        addressId: String,
        paymentMethod: PaymentMethod,
        notes: String? = null
    ): com.noghre.sod.domain.usecase.order.CreateOrderResult

    suspend fun getOrders(page: Int = 1, status: OrderStatus? = null): List<Order>
    suspend fun getOrderById(orderId: String): Order
    suspend fun cancelOrder(orderId: String, reason: String? = null): Order
    suspend fun getOrderTracking(orderId: String): List<OrderStatusHistory>
}
