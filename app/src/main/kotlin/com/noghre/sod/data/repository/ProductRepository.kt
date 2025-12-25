package com.noghre.sod.data.repository

import com.noghre.sod.domain.model.Product
import com.noghre.sod.data.remote.api.NoghreSodApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

/**
 * Repository for product-related operations.
 * Implements offline-first approach with local cache.
 *
 * @author Yaser
 * @version 1.0.0
 */
interface IProductRepository {
    suspend fun getProducts(page: Int = 1, limit: Int = 20): Flow<Result<List<Product>>>
    suspend fun getFeaturedProducts(): Flow<Result<List<Product>>>
    suspend fun searchProducts(query: String, page: Int = 1, limit: Int = 20): Flow<Result<List<Product>>>
    suspend fun getProductDetail(productId: String): Flow<Result<Product>>
    suspend fun getCategories(): Flow<Result<List<String>>>
}

class ProductRepository @Inject constructor(
    private val api: NoghreSodApi
) : IProductRepository {

    override suspend fun getProducts(page: Int, limit: Int): Flow<Result<List<Product>>> = flow {
        try {
            val response = api.getProducts(page, limit)
            when {
                response.isSuccessful && response.body()?.success == true -> {
                    val products = response.body()?.data ?: emptyList()
                    val domainProducts = products.map { it.toDomain() }
                    emit(Result.success(domainProducts))
                }
                else -> {
                    val errorMessage = response.body()?.message ?: "Unknown error"
                    emit(Result.failure(Exception(errorMessage)))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching products")
            emit(Result.failure(e))
        }
    }

    override suspend fun getFeaturedProducts(): Flow<Result<List<Product>>> = flow {
        try {
            val response = api.getFeaturedProducts()
            when {
                response.isSuccessful && response.body()?.success == true -> {
                    val products = response.body()?.data ?: emptyList()
                    val domainProducts = products.map { it.toDomain() }
                    emit(Result.success(domainProducts))
                }
                else -> {
                    val errorMessage = response.body()?.message ?: "Unknown error"
                    emit(Result.failure(Exception(errorMessage)))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching featured products")
            emit(Result.failure(e))
        }
    }

    override suspend fun searchProducts(query: String, page: Int, limit: Int): Flow<Result<List<Product>>> = flow {
        try {
            val response = api.searchProducts(query, page, limit)
            when {
                response.isSuccessful && response.body()?.success == true -> {
                    val products = response.body()?.data ?: emptyList()
                    val domainProducts = products.map { it.toDomain() }
                    emit(Result.success(domainProducts))
                }
                else -> {
                    val errorMessage = response.body()?.message ?: "Search failed"
                    emit(Result.failure(Exception(errorMessage)))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error searching products")
            emit(Result.failure(e))
        }
    }

    override suspend fun getProductDetail(productId: String): Flow<Result<Product>> = flow {
        try {
            val response = api.getProductDetail(productId)
            when {
                response.isSuccessful && response.body()?.success == true -> {
                    val product = response.body()?.data?.toDomain()
                    if (product != null) {
                        emit(Result.success(product))
                    } else {
                        emit(Result.failure(Exception("Product not found")))
                    }
                }
                else -> {
                    val errorMessage = response.body()?.message ?: "Product not found"
                    emit(Result.failure(Exception(errorMessage)))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching product detail")
            emit(Result.failure(e))
        }
    }

    override suspend fun getCategories(): Flow<Result<List<String>>> = flow {
        try {
            val response = api.getCategories()
            when {
                response.isSuccessful && response.body()?.success == true -> {
                    val categories = response.body()?.data ?: emptyList()
                    emit(Result.success(categories))
                }
                else -> {
                    val errorMessage = response.body()?.message ?: "Failed to load categories"
                    emit(Result.failure(Exception(errorMessage)))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching categories")
            emit(Result.failure(e))
        }
    }
}

// Extension function to convert DTO to Domain Model
fun com.noghre.sod.data.remote.dto.ProductDto.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        originalPrice = this.originalPrice,
        category = this.category,
        imageUrl = this.imageUrl,
        images = this.images ?: emptyList(),
        rating = this.rating,
        reviewCount = this.reviewCount,
        quantity = this.quantity,
        weight = this.weight,
        material = this.material,
        colors = this.colors ?: emptyList(),
        sizes = this.sizes ?: emptyList(),
        inStock = this.inStock,
        createdAt = this.createdAt
    )
}
