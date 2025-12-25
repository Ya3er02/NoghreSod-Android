package com.noghre.sod.data.repository

import android.util.Log
import com.noghre.sod.core.di.IoDispatcher
import com.noghre.sod.core.network.NetworkMonitor
import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.mapper.toDomain
import com.noghre.sod.data.mapper.toDomainList
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ProductRepository using offline-first strategy.
 * 
 * Flow:
 * 1. Try to fetch from network
 * 2. Cache results in local database
 * 3. Return cached data if network fails
 */
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val productDao: ProductDao,
    private val networkMonitor: NetworkMonitor,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ProductRepository {

    private companion object {
        const val TAG = "ProductRepository"
    }

    /**
     * Get all products with offline-first strategy.
     */
    override fun getProducts(
        page: Int,
        pageSize: Int,
        query: String?,
        categoryId: String?,
        sortBy: String?
    ): Flow<Result<List<Product>>> = flow {
        withContext(dispatcher) {
            try {
                if (networkMonitor.isOnline) {
                    // Fetch from network
                    val response = apiService.getProducts(
                        page = page,
                        pageSize = pageSize,
                        query = query,
                        categoryId = categoryId,
                        sortBy = sortBy
                    )

                    if (response.isSuccessful) {
                        val products = response.body()?.data?.toDomainList() ?: emptyList()
                        
                        // Cache to database
                        productDao.insertAll(products.map { it.toEntity() })
                        
                        Log.d(TAG, "Fetched ${products.size} products from network")
                        emit(Result.success(products))
                    } else {
                        val cachedProducts = getCachedProducts(query, categoryId)
                        if (cachedProducts.isNotEmpty()) {
                            Log.w(TAG, "Network failed, returning cached data")
                            emit(Result.success(cachedProducts))
                        } else {
                            emit(Result.failure(Exception("Server error: ${response.code()}")))
                        }
                    }
                } else {
                    // Offline mode
                    val cachedProducts = getCachedProducts(query, categoryId)
                    if (cachedProducts.isNotEmpty()) {
                        Log.d(TAG, "Offline mode, returning ${cachedProducts.size} cached products")
                        emit(Result.success(cachedProducts))
                    } else {
                        emit(Result.failure(Exception("No internet and no cached data")))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching products", e)
                // Try to return cached data
                val cachedProducts = getCachedProducts(query, categoryId)
                if (cachedProducts.isNotEmpty()) {
                    emit(Result.success(cachedProducts))
                } else {
                    emit(Result.failure(e))
                }
            }
        }
    }.catch { e ->
        Log.e(TAG, "Flow error", e)
        emit(Result.failure(e))
    }

    /**
     * Get product by ID.
     */
    override suspend fun getProductById(productId: String): Result<Product> = withContext(dispatcher) {
        try {
            if (networkMonitor.isOnline) {
                val response = apiService.getProductById(productId)
                if (response.isSuccessful) {
                    val product = response.body()?.data?.toDomain()
                    if (product != null) {
                        productDao.insert(product.toEntity())
                        Result.success(product)
                    } else {
                        Result.failure(Exception("Empty response"))
                    }
                } else {
                    // Try cache
                    val cached = productDao.getById(productId)
                    if (cached != null) {
                        Result.success(cached.toDomain())
                    } else {
                        Result.failure(Exception("Server error: ${response.code()}"))
                    }
                }
            } else {
                // Offline mode
                val cached = productDao.getById(productId)
                if (cached != null) {
                    Result.success(cached.toDomain())
                } else {
                    Result.failure(Exception("No internet and product not cached"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching product $productId", e)
            val cached = productDao.getById(productId)
            if (cached != null) {
                Result.success(cached.toDomain())
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Search products.
     */
    override suspend fun searchProducts(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<List<Product>> = withContext(dispatcher) {
        try {
            if (networkMonitor.isOnline) {
                val response = apiService.searchProducts(
                    query = query,
                    page = page,
                    pageSize = pageSize
                )
                
                if (response.isSuccessful) {
                    val products = response.body()?.data?.toDomainList() ?: emptyList()
                    Result.success(products)
                } else {
                    Result.failure(Exception("Search failed: ${response.code()}"))
                }
            } else {
                // Local search
                val products = productDao.searchByName(query)
                Result.success(products.map { it.toDomain() })
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching products", e)
            Result.failure(e)
        }
    }

    /**
     * Get cached products based on filters.
     */
    private suspend fun getCachedProducts(
        query: String? = null,
        categoryId: String? = null
    ): List<Product> = withContext(dispatcher) {
        try {
            val products = when {
                !query.isNullOrBlank() -> productDao.searchByName(query)
                !categoryId.isNullOrBlank() -> productDao.getByCategory(categoryId)
                else -> productDao.getAll()
            }
            products.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cached products", e)
            emptyList()
        }
    }
}