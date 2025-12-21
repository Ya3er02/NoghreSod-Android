package com.noghre.sod.data.repository

import com.noghre.sod.data.remote.ApiService
import com.noghre.sod.data.remote.ProductDto
import com.noghre.sod.domain.Result
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getProducts(
        page: Int = 1,
        limit: Int = 20,
        category: String? = null
    ): Result<List<ProductDto>> {
        return try {
            val products = apiService.getProducts(page, limit, category)
            Result.Success(products)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch products")
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun getProduct(id: String): Result<ProductDto> {
        return try {
            val product = apiService.getProduct(id)
            Result.Success(product)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch product")
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun searchProducts(query: String): Result<List<ProductDto>> {
        return try {
            val products = apiService.searchProducts(query)
            Result.Success(products)
        } catch (e: Exception) {
            Timber.e(e, "Search failed")
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }
}
