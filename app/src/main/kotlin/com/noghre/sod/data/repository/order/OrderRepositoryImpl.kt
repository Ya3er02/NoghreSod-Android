package com.noghre.sod.data.repository.order

import com.noghre.sod.core.result.Result
import com.noghre.sod.data.database.dao.OrderDao
import com.noghre.sod.data.error.ExceptionHandler
import com.noghre.sod.data.mapper.OrderMapper.toDomain
import com.noghre.sod.data.mapper.OrderMapper.toDomainList
import com.noghre.sod.data.mapper.OrderMapper.toEntity
import com.noghre.sod.data.network.NoghreSodApi
import com.noghre.sod.data.repository.networkBoundResource
import com.noghre.sod.domain.model.Order
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

/**
 * Order Repository Implementation.
 *
 * Manages order data with caching strategy:
 * - Caches orders locally
 * - Syncs with network
 * - Tracks order status and updates
 *
 * @param api Retrofit API client
 * @param orderDao Database access object for orders
 * @param ioDispatcher Dispatcher for I/O operations
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
class OrderRepositoryImpl @Inject constructor(
    private val api: NoghreSodApi,
    private val orderDao: OrderDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IOrderRepository {

    override fun getOrders(): Flow<Result<List<Order>>> = networkBoundResource(
        query = {
            orderDao.getAllOrders().map { it.toDomain() }
        },
        fetch = {
            api.getOrders()
        },
        saveFetchResult = { response ->
            val orders = response.toDomainList()
            orderDao.insertAll(orders.map { it.toEntity() })
            Timber.d("Orders cached: ${orders.size} items")
        },
        shouldFetch = { localData ->
            localData.isEmpty() || isCacheStale()
        },
        onFetchFailed = { exception ->
            Timber.e(exception, "Failed to fetch orders")
        }
    ).flowOn(ioDispatcher)

    override fun getOrderDetail(orderId: String): Flow<Result<Order>> = networkBoundResource(
        query = {
            orderDao.getOrderById(orderId)?.toDomain()
                ?: throw Exception("Order not found locally")
        },
        fetch = {
            api.getOrderDetail(orderId)
        },
        saveFetchResult = { response ->
            val order = response.toDomain()
            orderDao.insert(order.toEntity())
            Timber.d("Order cached: $orderId")
        },
        shouldFetch = { _ -> true },
        onFetchFailed = { exception ->
            Timber.e(exception, "Failed to fetch order detail: $orderId")
        }
    ).flowOn(ioDispatcher)

    override suspend fun createOrder(order: Order): Result<Order> = try {
        val requestBody = mapOf(
            "items" to order.items,
            "shippingAddress" to order.shippingAddress,
            "paymentMethod" to order.paymentMethod
        )

        val response = api.createOrder(requestBody)

        if (response.isSuccessful && response.body()?.success == true) {
            val createdOrderDto = response.body()?.data
            if (createdOrderDto != null) {
                val createdOrder = createdOrderDto.toDomain()
                orderDao.insert(createdOrder.toEntity())
                Timber.d("Order created: ${createdOrder.id}")
                Result.success(createdOrder)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            Result.failure(Exception("Failed to create order: ${response.code()}"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "createOrder")
        Timber.e(e, "Error creating order")
        Result.failure(e)
    }

    override suspend fun getOrderTracking(orderId: String): Result<Order> = try {
        val response = api.getOrderTracking(orderId)

        if (response.isSuccessful && response.body()?.success == true) {
            val orderDto = response.body()?.data
            if (orderDto != null) {
                val order = orderDto.toDomain()
                orderDao.insert(order.toEntity())
                Timber.d("Order tracking retrieved: $orderId")
                Result.success(order)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            Result.failure(Exception("Failed to get tracking"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "getOrderTracking")
        Timber.e(e, "Error getting order tracking")
        Result.failure(e)
    }

    private suspend fun isCacheStale(): Boolean {
        return try {
            val orders = orderDao.getAllOrders()
            orders.isEmpty() || (System.currentTimeMillis() - (orders.firstOrNull()?.lastUpdated ?: 0)) > 30 * 60 * 1000  // 30 min
        } catch (e: Exception) {
            Timber.e(e, "Error checking cache staleness")
            true
        }
    }
}
