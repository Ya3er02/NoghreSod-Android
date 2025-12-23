package com.noghre.sod.data.repository

import com.noghre.sod.data.dto.CartDto
import com.noghre.sod.data.dto.request.AddToCartRequest
import com.noghre.sod.data.local.dao.CartDao
import com.noghre.sod.data.mapper.CartMapper
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.exception.ApiException
import com.noghre.sod.data.remote.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class CartRepositoryImpl(
    private val apiService: NoghreSodApiService,
    private val cartDao: CartDao,
    private val networkMonitor: NetworkMonitor,
    private val mapper: CartMapper
) : CartRepository {

    override fun getCart(): Flow<Result<List<CartDto>>> = flow {
        try {
            emit(Result.Loading)
            networkMonitor.isConnected.collect { isOnline ->
                if (isOnline) {
                    try {
                        val response = apiService.getCart()
                        if (response.success && response.data != null) {
                            cartDao.clearAll()
                            cartDao.insertProducts(mapper.toEntities(response.data))
                            emit(Result.Success(response.data))
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to fetch cart")
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

    override suspend fun addToCart(productId: String, quantity: Int): Result<CartDto> {
        return try {
            val response = apiService.addToCart(AddToCartRequest(productId, quantity))
            if (response.success && response.data != null) {
                cartDao.addToCart(mapper.toEntity(response.data))
                Result.Success(response.data)
            } else {
                Result.Error(ApiException.ServerError())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    override suspend fun removeFromCart(productId: String): Result<Unit> {
        return try {
            apiService.removeFromCart("${productId}_item")
            cartDao.removeCartItem(productId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    private suspend fun emitCached() {
        // Emit from cache - implementation would depend on userId context
        emit(Result.Success(emptyList()))
    }

    private fun handleException(e: Exception): ApiException {
        return when (e) {
            is ApiException -> e
            else -> ApiException.UnknownError(e.message ?: "Unknown error")
        }
    }
}

interface CartRepository {
    fun getCart(): Flow<Result<List<CartDto>>>
    suspend fun addToCart(productId: String, quantity: Int): Result<CartDto>
    suspend fun removeFromCart(productId: String): Result<Unit>
}