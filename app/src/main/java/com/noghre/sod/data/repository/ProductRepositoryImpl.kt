package com.noghre.sod.data.repository

import com.noghre.sod.data.dto.ProductDto
import com.noghre.sod.data.local.cache.CachePolicy
import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.mapper.ProductMapper
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.exception.ApiException
import com.noghre.sod.data.remote.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.IOException

/**
 * Product repository with offline-first strategy
 * Returns cached data if offline, fetches fresh data if online
 */
class ProductRepositoryImpl(
    private val apiService: NoghreSodApiService,
    private val productDao: ProductDao,
    private val networkMonitor: NetworkMonitor,
    private val mapper: ProductMapper
) : ProductRepository {

    override fun getProducts(page: Int): Flow<Result<List<ProductDto>>> = flow {
        try {
            emit(Result.Loading)
            networkMonitor.isConnected.collect { isOnline ->
                if (isOnline) {
                    try {
                        val response = apiService.getProducts(page)
                        if (response.success && response.data.isNotEmpty()) {
                            productDao.insertProducts(mapper.toEntities(response.data))
                            emit(Result.Success(response.data))
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to fetch products from API")
                        emitCachedData()
                    }
                } else {
                    emitCachedData()
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in getProducts")
            emit(Result.Error(handleException(e)))
        }
    }

    override fun getProductById(id: String): Flow<Result<ProductDto>> = flow {
        try {
            emit(Result.Loading)
            productDao.getProductById(id).collect { entity ->
                if (entity != null) {
                    emit(Result.Success(mapper.toDto(entity)))
                    if (shouldRefreshCache(entity.updatedAt)) {
                        refreshProductFromAPI(id)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in getProductById")
            emit(Result.Error(handleException(e)))
        }
    }

    override fun getProductsByCategory(categoryId: String): Flow<Result<List<ProductDto>>> = flow {
        try {
            emit(Result.Loading)
            networkMonitor.isConnected.collect { isOnline ->
                if (isOnline) {
                    try {
                        val response = apiService.getProductsByCategory(categoryId)
                        if (response.success) {
                            productDao.insertProducts(mapper.toEntities(response.data))
                            emit(Result.Success(response.data))
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to fetch category products")
                        emitCategoryCache(categoryId)
                    }
                } else {
                    emitCategoryCache(categoryId)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in getProductsByCategory")
            emit(Result.Error(handleException(e)))
        }
    }

    override fun searchProducts(query: String): Flow<Result<List<ProductDto>>> = flow {
        try {
            emit(Result.Loading)
            networkMonitor.isConnected.collect { isOnline ->
                if (isOnline) {
                    try {
                        val response = apiService.searchProducts(query)
                        if (response.success) {
                            emit(Result.Success(response.data))
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Search failed")
                        emitSearchCache(query)
                    }
                } else {
                    emitSearchCache(query)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in searchProducts")
            emit(Result.Error(handleException(e)))
        }
    }

    private suspend fun emitCachedData() {
        productDao.getRecentProducts().collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Result.Success(mapper.toDtos(entities)))
            }
        }
    }

    private suspend fun emitCategoryCache(categoryId: String) {
        productDao.getProductsByCategory(categoryId).collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Result.Success(mapper.toDtos(entities)))
            }
        }
    }

    private suspend fun emitSearchCache(query: String) {
        productDao.searchProducts(query).collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Result.Success(mapper.toDtos(entities)))
            }
        }
    }

    private suspend fun refreshProductFromAPI(id: String) {
        try {
            val response = apiService.getProductById(id)
            if (response.success && response.data != null) {
                productDao.insertProduct(mapper.toEntity(response.data))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to refresh product")
        }
    }

    private fun shouldRefreshCache(lastUpdate: Long): Boolean {
        return CachePolicy.isCacheExpired(lastUpdate + CachePolicy.PRODUCT_CACHE_DURATION)
    }

    private fun handleException(e: Exception): ApiException {
        return when (e) {
            is IOException -> ApiException.NetworkError("Network connection failed")
            is ApiException -> e
            else -> ApiException.UnknownError(e.message ?: "Unknown error")
        }
    }
}

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: ApiException) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

interface ProductRepository {
    fun getProducts(page: Int): Flow<Result<List<ProductDto>>>
    fun getProductById(id: String): Flow<Result<ProductDto>>
    fun getProductsByCategory(categoryId: String): Flow<Result<List<ProductDto>>>
    fun searchProducts(query: String): Flow<Result<List<ProductDto>>>
}