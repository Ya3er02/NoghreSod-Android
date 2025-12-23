package com.noghre.sod.data.repository

import com.noghre.sod.data.dto.OrderDto
import com.noghre.sod.data.local.dao.OrderDao
import com.noghre.sod.data.mapper.OrderMapper
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.exception.ApiException
import com.noghre.sod.data.remote.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class OrderRepositoryImpl(
    private val apiService: NoghreSodApiService,
    private val orderDao: OrderDao,
    private val networkMonitor: NetworkMonitor,
    private val mapper: OrderMapper
) : OrderRepository {

    override fun getOrders(page: Int): Flow<Result<List<OrderDto>>> = flow {
        try {
            emit(Result.Loading)
            networkMonitor.isConnected.collect { isOnline ->
                if (isOnline) {
                    try {
                        val response = apiService.getOrders(page)
                        if (response.success) {
                            orderDao.insertOrders(mapper.toEntities(response.data))
                            emit(Result.Success(response.data))
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to fetch orders")
                        emitCached()
                    }
                } else {
                    emitCached()
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(handleException(e)))
        }
    }

    override fun getOrderById(id: String): Flow<Result<OrderDto>> = flow {
        try {
            emit(Result.Loading)
            orderDao.getOrderById(id).collect { entity ->
                if (entity != null) {
                    emit(Result.Success(mapper.toDto(entity)))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(handleException(e)))
        }
    }

    private suspend fun emitCached() {
        emit(Result.Success(emptyList()))
    }

    private suspend fun OrderDao.insertOrders(entities: List<OrderEntity>) {
        entities.forEach { insertOrder(it) }
    }

    private fun handleException(e: Exception): ApiException {
        return when (e) {
            is ApiException -> e
            else -> ApiException.UnknownError(e.message ?: "Unknown error")
        }
    }
}

interface OrderRepository {
    fun getOrders(page: Int): Flow<Result<List<OrderDto>>>
    fun getOrderById(id: String): Flow<Result<OrderDto>>
}