package com.noghre.sod.data.repository

import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.remote.dto.ProductDetailDto
import com.noghre.sod.data.remote.dto.ProductDto
import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.ProductDetail
import com.noghre.sod.domain.model.ProductSummary
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : ProductRepository {

    override fun getAllProducts(page: Int, pageSize: Int): Flow<Result<List<ProductSummary>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getAllProducts(page, pageSize)
            if (response.success && response.data != null) {
                val products = response.data.items.map { it.toProductSummary() }
                emit(Result.Success(products))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun getProductById(productId: String): Flow<Result<ProductDetail>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getProductById(productId)
            if (response.success && response.data != null) {
                val productDetail = response.data.toProductDetail()
                emit(Result.Success(productDetail))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun searchProducts(query: String, page: Int, pageSize: Int): Flow<Result<List<ProductSummary>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.searchProducts(query, page, pageSize)
            if (response.success && response.data != null) {
                val products = response.data.items.map { it.toProductSummary() }
                emit(Result.Success(products))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun getProductsByCategory(category: String, page: Int, pageSize: Int): Flow<Result<List<ProductSummary>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getProductsByCategory(category, page, pageSize)
            if (response.success && response.data != null) {
                val products = response.data.items.map { it.toProductSummary() }
                emit(Result.Success(products))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun getFeaturedProducts(): Flow<Result<List<ProductSummary>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getFeaturedProducts()
            if (response.success && response.data != null) {
                val products = response.data.map { it.toProductSummary() }
                emit(Result.Success(products))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun addToFavorites(productId: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.addToFavorites(productId)
            if (response.success) {
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun removeFromFavorites(productId: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.removeFromFavorites(productId)
            if (response.success) {
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun getFavoriteProducts(): Flow<Result<List<ProductSummary>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getFavoriteProducts()
            if (response.success && response.data != null) {
                val products = response.data.map { it.toProductSummary() }
                emit(Result.Success(products))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    // Mapper functions
    private fun ProductDto.toProductSummary(): ProductSummary {
        return ProductSummary(
            id = id,
            name = name,
            price = price,
            weight = weight,
            purity = purity,
            category = category,
            thumbnailImage = thumbnailImage ?: images.firstOrNull(),
            rating = rating,
            isHandmade = isHandmade,
        )
    }

    private fun ProductDetailDto.toProductDetail(): ProductDetail {
        return ProductDetail(
            productSummary = product.toProductSummary(),
            description = description,
        )
    }
}
