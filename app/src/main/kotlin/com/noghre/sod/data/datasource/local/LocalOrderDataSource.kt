package com.noghre.sod.data.datasource.local

import com.noghre.sod.data.local.dao.OrderDao
import com.noghre.sod.data.local.entity.OrderEntity
import com.noghre.sod.data.local.entity.OrderTrackingEntity
import com.noghre.sod.data.local.entity.ReturnRequestEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalOrderDataSource @Inject constructor(
    private val orderDao: OrderDao,
) {

    suspend fun insertOrder(order: OrderEntity) {
        orderDao.insertOrder(order)
    }

    suspend fun insertOrders(orders: List<OrderEntity>) {
        orderDao.insertOrders(orders)
    }

    fun getOrder(orderId: String): Flow<OrderEntity?> {
        return orderDao.getOrder(orderId)
    }

    fun getUserOrders(offset: Int, pageSize: Int): Flow<List<OrderEntity>> {
        return orderDao.getUserOrders(offset, pageSize)
    }

    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>> {
        return orderDao.getOrdersByStatus(status)
    }

    suspend fun updateOrder(order: OrderEntity) {
        orderDao.updateOrder(order)
    }

    suspend fun deleteOrder(order: OrderEntity) {
        orderDao.deleteOrder(order)
    }

    suspend fun deleteAllOrders() {
        orderDao.deleteAllOrders()
    }

    suspend fun insertTracking(tracking: OrderTrackingEntity) {
        orderDao.insertTracking(tracking)
    }

    fun getTracking(orderId: String): Flow<OrderTrackingEntity?> {
        return orderDao.getTracking(orderId)
    }

    suspend fun updateTracking(tracking: OrderTrackingEntity) {
        orderDao.updateTracking(tracking)
    }

    suspend fun insertReturnRequest(returnRequest: ReturnRequestEntity) {
        orderDao.insertReturnRequest(returnRequest)
    }

    fun getReturnRequest(orderId: String): Flow<ReturnRequestEntity?> {
        return orderDao.getReturnRequest(orderId)
    }

    fun getReturnRequestsByStatus(status: String): Flow<List<ReturnRequestEntity>> {
        return orderDao.getReturnRequestsByStatus(status)
    }

    suspend fun updateReturnRequest(returnRequest: ReturnRequestEntity) {
        orderDao.updateReturnRequest(returnRequest)
    }
}
