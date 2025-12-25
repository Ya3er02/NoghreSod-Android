package com.noghre.sod.data.repository

import com.noghre.sod.data.local.ProductDao
import com.noghre.sod.data.remote.ApiService
import com.noghre.sod.data.remote.dto.ProductDto
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.Result
import com.noghre.sod.domain.repository.ProductRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.noghre.sod.core.di.IoDispatcher
import com.noghre.sod.core.network.NetworkMonitor

/**
 * Product Repository Implementation
 * Implements offline-first strategy with caching
 */
@ViewModelScoped
class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val productDao: ProductDao,
    private val networkMonitor: NetworkMonitor,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ProductRepository {
    
    /**
     * Get products with pagination
     * First tries network, falls back to cache
     */
    override fun getProducts(
        page: Int,
        pageSize: Int,
        query: String?,
        categoryId: String?
    ): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        
        withContext(dispatcher) {
            try {
                if (networkMonitor.isOnline) {
                    // Fetch from network
                    val response = apiService.getProducts(
                        page = page,
                        limit = pageSize,
                        search = query,
                        categoryId = categoryId
                    )
                    
                    val products = response.data.map { it.toDomain() }
                    
                    // Cache to database
                    productDao.insertAll(products.map { it.toEntity() })
                    
                    emit(Result.Success(products))
                } else {
                    // Use cached data
                    val cached = productDao.getAll().map { it.toDomain() }
                    if (cached.isNotEmpty()) {
                        emit(Result.Success(cached))
                    } else {
                        emit(Result.Error(
                            Exception("No internet and no cached data")
                        ))
                    }
                }
            } catch (e: Exception) {
                // Network failed, try cache
                val cached = productDao.getAll().map { it.toDomain() }
                if (cached.isNotEmpty()) {
                    emit(Result.Success(cached))
                } else {
                    emit(Result.Error(e))
                }
            }
        }
    }
    
    /**
     * Get single product by ID
     */
    override suspend fun getProductById(productId: String): Result<Product> {
        return withContext(dispatcher) {
            try {
                if (networkMonitor.isOnline) {
                    val response = apiService.getProductById(productId)
                    val product = response.data.toDomain()
                    
                    // Cache
                    productDao.insert(product.toEntity())
                    
                    Result.Success(product)
                } else {
                    // Try cache
                    val cached = productDao.getById(productId)
                    if (cached != null) {
                        Result.Success(cached.toDomain())
                    } else {
                        Result.Error(Exception("Offline: Product not found"))
                    }
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
    
    /**
     * Search products by query
     */
    override fun searchProducts(
        query: String,
        page: Int,
        pageSize: Int
    ): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        
        withContext(dispatcher) {
            try {
                if (networkMonitor.isOnline) {
                    val response = apiService.searchProducts(
                        query = query,
                        page = page,
                        limit = pageSize
                    )
                    val products = response.data.map { it.toDomain() }
                    emit(Result.Success(products))
                } else {
                    // Local search
                    val cached = productDao.searchByName("%$query%")
                        .map { it.toDomain() }
                    emit(Result.Success(cached))
                }
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }
    }
    
    /**
     * Get products by category
     */
    override fun getProductsByCategory(
        categoryId: String,
        page: Int,
        pageSize: Int
    ): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        
        withContext(dispatcher) {
            try {
                if (networkMonitor.isOnline) {
                    val response = apiService.getProductsByCategory(
                        categoryId = categoryId,
                        page = page,
                        limit = pageSize
                    )
                    val products = response.data.map { it.toDomain() }
                    emit(Result.Success(products))
                } else {
                    // Local filter
                    val cached = productDao.getByCategory(categoryId)
                        .map { it.toDomain() }
                    emit(Result.Success(cached))
                }
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }
    }
    
    /**
     * Clear all cached products
     */
    override suspend fun clearCache() {
        withContext(dispatcher) {
            productDao.deleteAll()
        }
    }
}

// ============================================
// 🔄 DTO to Domain Mapper
// ============================================

fun ProductDto.toDomain(): Product {
    return Product(
        id = this.id.orEmpty(),
        name = this.name.orEmpty(),
        description = this.description.orEmpty(),
        longDescription = this.longDescription,
        price = this.price?.toDomain() ?: throw IllegalArgumentException("Price is required"),
        originalPrice = this.originalPrice?.toDomain(),
        images = this.images?.map { it.toDomain() } ?: emptyList(),
        category = this.category?.toDomain() ?: throw IllegalArgumentException("Category is required"),
        material = this.material.orEmpty(),
        weight = this.weight,
        specifications = this.specifications?.toMap() ?: emptyMap(),
        availability = this.availability?.toDomain() ?: com.noghre.sod.domain.model.StockStatus.OutOfStock,
        rating = this.rating?.toDomain() ?: com.noghre.sod.domain.model.Rating(),
        reviews = this.reviews?.map { it.toDomain() } ?: emptyList(),
        tags = this.tags?.toList() ?: emptyList(),
        isNewProduct = this.isNew ?: false,
        isFeatured = this.featured ?: false,
        createdAt = parseDateTime(this.createdAt),
        updatedAt = parseDateTime(this.updatedAt)
    )
}

private fun parseDateTime(dateString: String?): java.time.LocalDateTime {
    return if (dateString != null) {
        java.time.LocalDateTime.parse(dateString, java.time.format.DateTimeFormatter.ISO_DATE_TIME)
    } else {
        java.time.LocalDateTime.now()
    }
}
