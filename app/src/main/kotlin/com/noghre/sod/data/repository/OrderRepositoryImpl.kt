package com.noghre.sod.data.repository

import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.model.OrderStatus
import com.noghre.sod.domain.model.OrderStatusHistory
import com.noghre.sod.domain.model.PaymentMethod
import com.noghre.sod.domain.repository.OrderRepository
import com.noghre.sod.domain.usecase.order.CreateOrderResult
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class OrderRepositoryImpl @Inject constructor() : OrderRepository {
    override suspend fun createOrder(
        addressId: String,
        paymentMethod: PaymentMethod,
        notes: String?
    ): CreateOrderResult {
        return CreateOrderResult(false, message = "خطای در سرویس")
    }

    override suspend fun getOrders(page: Int, status: OrderStatus?): List<Order> {
        return emptyList()
    }

    override suspend fun getOrderById(orderId: String): Order {
        throw NotImplementedError()
    }

    override suspend fun cancelOrder(orderId: String, reason: String?): Order {
        throw NotImplementedError()
    }

    override suspend fun getOrderTracking(orderId: String): List<OrderStatusHistory> {
        return emptyList()
    }
}
