package com.noghre.sod.data.repository

import com.noghre.sod.data.local.ProductDao
import com.noghre.sod.data.remote.ApiService
import com.noghre.sod.data.model.Product
import com.noghre.sod.data.dto.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

/**
 * Repository for product-related operations.
 */
class ProductRepository @Inject constructor(
    private val apiService: ApiService,
    private val productDao: ProductDao
) {

    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

    fun getProductsByCategory(category: String): Flow<List<Product>> =
        productDao.getProductsByCategory(category)

    fun searchProducts(query: String): Flow<List<Product>> =
        productDao.searchProducts(query)

    fun getProductById(id: String): Flow<Product?> = flow {
        val cachedProduct = productDao.getProductById(id)
        emit(cachedProduct)
        
        try {
            val response = apiService.getProductById(id)
            if (response.success && response.data != null) {
                val product = response.data.toEntity()
                productDao.insertProduct(product)
                emit(product)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching product")
        }
    }

    fun fetchProducts(page: Int = 1): Flow<Result<List<Product>>> = flow {
        try {
            emit(Result.loading())
            val response = apiService.getProducts(page = page)
            if (response.success && response.data != null) {
                val products = response.data.items.map { it.toEntity() }
                productDao.insertProducts(products)
                emit(Result.success(products))
            } else {
                emit(Result.error(response.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching products")
            emit(Result.error(e.message ?: "Unknown error"))
        }
    }

    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }
}

sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val message: String) : Result<T>()
    class Loading<T> : Result<T>()

    companion object {
        fun <T> success(data: T) = Success(data)
        fun <T> error(message: String) = Error<T>(message)
        fun <T> loading() = Loading<T>()
    }
}
