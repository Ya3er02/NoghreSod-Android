package com.noghre.sod.data.repository

import com.noghre.sod.data.local.db.product.ProductDao
import com.noghre.sod.data.local.db.product.ProductEntity
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.Result
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ProductRepository.
 * 
* Manages product data from both remote API and local cache.
 * Uses network-first strategy with fallback to cache.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val productDao: ProductDao
) : ProductRepository {

    /**
     * Get products with optional filtering and pagination.
     * 
     * Network-first approach:
     * 1. Try fetching from API
     * 2. Cache result locally
     * 3. If network fails, return cached data
     */
    override fun getProducts(
        page: Int,
        pageSize: Int,
        query: String?,
        categoryId: String?,
        sortBy: String?
    ): Flow<Result<List<Product>>> = flow {
        try {
            // Try network first
            val response = apiService.getProducts(
                page = page,
                pageSize = pageSize,
                search = query,
                category = categoryId,
                sortBy = sortBy
            )
            
            // Cache the response
            if (response.isSuccessful) {
                response.body()?.data?.let { products ->
                    val entities = products.map { it.toEntity() }
                    productDao.insertProducts(entities)
                    emit(Result.Success(products))
                }
            } else {
                emit(Result.Error(
                    exception = Exception(response.message()),
                    message = "Failed to fetch products: ${response.message()}"
                ))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching products from network")
            
            // Fallback to cache
            try {
                val cachedProducts = productDao.getAllProducts()
                    .map { it.toDomain() }
                
                if (cachedProducts.isNotEmpty()) {
                    emit(Result.Success(cachedProducts))
                } else {
                    emit(Result.Error(
                        exception = e,
                        message = "No cached data available"
                    ))
                }
            } catch (cacheError: Exception) {
                emit(Result.Error(
                    exception = cacheError,
                    message = "Failed to fetch products: ${e.localizedMessage}"
                ))
            }
        }
    }.catch { exception ->
        emit(Result.Error(
            exception = exception as Exception,
            message = exception.localizedMessage ?: "Unknown error"
        ))
    }

    /**
     * Search products by query string.
     */
    override fun searchProducts(
        query: String,
        categoryId: String?
    ): Flow<Result<List<Product>>> = flow {
        try {
            val response = apiService.searchProducts(
                query = query,
                category = categoryId
            )
            
            if (response.isSuccessful) {
                emit(Result.Success(response.body()?.data ?: emptyList()))
            } else {
                emit(Result.Error(
                    exception = Exception(response.message()),
                    message = "Search failed"
                ))
            }
        } catch (e: Exception) {
            emit(Result.Error(
                exception = e,
                message = "Search error: ${e.localizedMessage}"
            ))
        }
    }

    /**
     * Get products by category.
     */
    override fun getProductsByCategory(categoryId: String): Flow<Result<List<Product>>> = flow {
        try {
            val response = apiService.getProductsByCategory(categoryId)
            
            if (response.isSuccessful) {
                emit(Result.Success(response.body()?.data ?: emptyList()))
            } else {
                emit(Result.Error(
                    exception = Exception(response.message()),
                    message = "Category fetch failed"
                ))
            }
        } catch (e: Exception) {
            emit(Result.Error(
                exception = e,
                message = "Category error: ${e.localizedMessage}"
            ))
        }
    }

    /**
     * Get single product by ID.
     */
    override fun getProductById(productId: String): Flow<Result<Product>> = flow {
        try {
            val response = apiService.getProductById(productId)
            
            if (response.isSuccessful) {
                response.body()?.data?.let { product ->
                    // Cache it
                    productDao.insertProduct(product.toEntity())
                    emit(Result.Success(product))
                }
            } else {
                emit(Result.Error(
                    exception = Exception(response.message()),
                    message = "Product not found"
                ))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching product details")
            emit(Result.Error(
                exception = e,
                message = "Failed to fetch product: ${e.localizedMessage}"
            ))
        }
    }

    /**
     * Clear all cached products.
     */
    override suspend fun clearCache() {
        try {
            productDao.deleteAllProducts()
            Timber.d("Cache cleared")
        } catch (e: Exception) {
            Timber.e(e, "Error clearing cache")
        }
    }
}
