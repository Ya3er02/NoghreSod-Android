package com.noghre.sod.data.repository

import com.noghre.sod.core.error.*
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.local.ProductDao
import com.noghre.sod.data.remote.ApiService
import com.noghre.sod.data.remote.dto.ProductDto
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import com.noghre.sod.core.di.IoDispatcher
import com.noghre.sod.core.network.NetworkMonitor

/**
 * 🏭 Product Repository Implementation
 * 
 * Implements offline-first strategy with caching and comprehensive error handling.
 * All operations return new Result<T> with AppError classification.
 */
@ViewModelScoped
class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val productDao: ProductDao,
    private val networkMonitor: NetworkMonitor,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val exceptionHandler: GlobalExceptionHandler
) : ProductRepository {
    
    /**
     * 📦 Get products with pagination
     * 
     * First tries network, falls back to cache.
     * Returns Result<List<Product>> with proper error classification.
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
                Timber.d("[PRODUCT] Loading products: page=$page, size=$pageSize, query=$query")
                
                if (networkMonitor.isOnline) {
                    try {
                        // Fetch from network
                        Timber.d("[PRODUCT] Fetching from network")
                        val response = apiService.getProducts(
                            page = page,
                            limit = pageSize,
                            search = query,
                            categoryId = categoryId
                        )
                        
                        // Check HTTP response
                        if (response.isSuccessful) {
                            val products = response.data?.map { it.toDomain() } ?: emptyList()
                            
                            // Cache to database
                            try {
                                productDao.insertAll(products.map { it.toEntity() })
                                Timber.d("[PRODUCT] Cached ${products.size} products")
                            } catch (e: Exception) {
                                Timber.w(e, "[PRODUCT] Failed to cache products")
                            }
                            
                            Timber.d("[PRODUCT] Successfully loaded ${products.size} products")
                            emit(Result.Success(products))
                        } else {
                            // HTTP error
                            Timber.w("[PRODUCT] HTTP error: ${response.code()}")
                            throw NetworkException(
                                message = response.message ?: "Failed to load products",
                                statusCode = response.code()
                            )
                        }
                    } catch (e: NetworkException) {
                        Timber.w(e, "[PRODUCT] Network exception")
                        // Try cache first
                        val cached = productDao.getAll().map { it.toDomain() }
                        if (cached.isNotEmpty()) {
                            Timber.d("[PRODUCT] Using cached data (${cached.size} products)")
                            emit(Result.Success(cached))
                        } else {
                            emit(Result.Error(e))
                        }
                    }
                } else {
                    // Offline mode
                    Timber.d("[PRODUCT] Offline mode, using cache")
                    val cached = productDao.getAll().map { it.toDomain() }
                    if (cached.isNotEmpty()) {
                        Timber.d("[PRODUCT] Cache hit: ${cached.size} products")
                        emit(Result.Success(cached))
                    } else {
                        Timber.w("[PRODUCT] No cached data available")
                        emit(Result.Error(AppError.Network(
                            message = "بدون دسترسی به اینترنت و عدم وجود داده‌های ذخیره شده",
                            statusCode = null
                        )))
                    }
                }
            } catch (e: java.net.UnknownHostException) {
                Timber.e(e, "[PRODUCT] No internet connection")
                emit(Result.Error(AppError.Network(
                    message = "بدون دسترسی به اینترنت",
                    statusCode = null
                )))
            } catch (e: java.net.SocketTimeoutException) {
                Timber.e(e, "[PRODUCT] Network timeout")
                emit(Result.Error(AppError.Network(
                    message = "زمان اتصال تمام شد",
                    statusCode = null
                )))
            } catch (e: Exception) {
                Timber.e(e, "[PRODUCT] Unexpected error")
                emit(Result.Error(exceptionHandler.handleException(e)))
            }
        }
    }
    
    /**
     * 🔍 Get single product by ID
     */
    override suspend fun getProductById(productId: String): Result<Product> {
        return withContext(dispatcher) {
            try {
                Timber.d("[PRODUCT] Loading product: $productId")
                
                if (networkMonitor.isOnline) {
                    try {
                        val response = apiService.getProductById(productId)
                        
                        if (response.isSuccessful) {
                            val product = response.data?.toDomain()
                                ?: return@withContext Result.Error(AppError.Network(
                                    message = "سرور پاسخ نامعتبری ارسال کرد",
                                    statusCode = 200
                                ))
                            
                            // Cache
                            try {
                                productDao.insert(product.toEntity())
                            } catch (e: Exception) {
                                Timber.w(e, "[PRODUCT] Failed to cache product")
                            }
                            
                            Timber.d("[PRODUCT] Successfully loaded product: $productId")
                            Result.Success(product)
                        } else {
                            Timber.w("[PRODUCT] HTTP error: ${response.code()}")
                            Result.Error(AppError.Network(
                                message = response.message ?: "Failed to load product",
                                statusCode = response.code()
                            ))
                        }
                    } catch (e: java.net.UnknownHostException) {
                        Timber.e(e, "[PRODUCT] Network error")
                        Result.Error(AppError.Network(
                            message = "بدون دسترسی به اینترنت",
                            statusCode = null
                        ))
                    } catch (e: Exception) {
                        Timber.e(e, "[PRODUCT] Network exception")
                        Result.Error(exceptionHandler.handleException(e))
                    }
                } else {
                    // Try cache
                    val cached = productDao.getById(productId)
                    if (cached != null) {
                        Timber.d("[PRODUCT] Loaded from cache: $productId")
                        Result.Success(cached.toDomain())
                    } else {
                        Timber.w("[PRODUCT] Product not found offline: $productId")
                        Result.Error(AppError.Network(
                            message = "محصول در حالت آفلاین یافت نشد",
                            statusCode = null
                        ))
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "[PRODUCT] Unexpected error loading product")
                Result.Error(exceptionHandler.handleException(e))
            }
        }
    }
    
    /**
     * 🔎 Search products by query
     */
    override fun searchProducts(
        query: String,
        page: Int,
        pageSize: Int
    ): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        
        withContext(dispatcher) {
            try {
                Timber.d("[PRODUCT] Searching: query=$query, page=$page")
                
                if (query.isBlank()) {
                    emit(Result.Error(AppError.Validation(
                        message = "عبارت جستجو نمی‌تواند خالی باشد",
                        field = "query"
                    )))
                    return@withContext
                }
                
                if (networkMonitor.isOnline) {
                    try {
                        val response = apiService.searchProducts(
                            query = query,
                            page = page,
                            limit = pageSize
                        )
                        
                        if (response.isSuccessful) {
                            val products = response.data?.map { it.toDomain() } ?: emptyList()
                            Timber.d("[PRODUCT] Search found ${products.size} results")
                            emit(Result.Success(products))
                        } else {
                            Timber.w("[PRODUCT] Search failed: ${response.code()}")
                            emit(Result.Error(AppError.Network(
                                message = response.message ?: "جستجو ناموفق بود",
                                statusCode = response.code()
                            )))
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "[PRODUCT] Search network error")
                        emit(Result.Error(exceptionHandler.handleException(e)))
                    }
                } else {
                    // Local search
                    try {
                        val cached = productDao.searchByName("%$query%")
                            .map { it.toDomain() }
                        Timber.d("[PRODUCT] Local search found ${cached.size} results")
                        emit(Result.Success(cached))
                    } catch (e: Exception) {
                        Timber.e(e, "[PRODUCT] Local search error")
                        emit(Result.Error(AppError.Database(
                            message = "خطا در جستجوی محلی",
                            operation = "searchByName"
                        )))
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "[PRODUCT] Search unexpected error")
                emit(Result.Error(exceptionHandler.handleException(e)))
            }
        }
    }
    
    /**
     * 📂 Get products by category
     */
    override fun getProductsByCategory(
        categoryId: String,
        page: Int,
        pageSize: Int
    ): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        
        withContext(dispatcher) {
            try {
                Timber.d("[PRODUCT] Loading category: $categoryId, page=$page")
                
                if (categoryId.isBlank()) {
                    emit(Result.Error(AppError.Validation(
                        message = "شناسه دسته‌بندی نمی‌تواند خالی باشد",
                        field = "categoryId"
                    )))
                    return@withContext
                }
                
                if (networkMonitor.isOnline) {
                    try {
                        val response = apiService.getProductsByCategory(
                            categoryId = categoryId,
                            page = page,
                            limit = pageSize
                        )
                        
                        if (response.isSuccessful) {
                            val products = response.data?.map { it.toDomain() } ?: emptyList()
                            Timber.d("[PRODUCT] Category loaded: ${products.size} products")
                            emit(Result.Success(products))
                        } else {
                            Timber.w("[PRODUCT] Category fetch failed: ${response.code()}")
                            emit(Result.Error(AppError.Network(
                                message = response.message ?: "دانلود دسته‌بندی ناموفق",
                                statusCode = response.code()
                            )))
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "[PRODUCT] Category network error")
                        emit(Result.Error(exceptionHandler.handleException(e)))
                    }
                } else {
                    // Local filter
                    try {
                        val cached = productDao.getByCategory(categoryId)
                            .map { it.toDomain() }
                        Timber.d("[PRODUCT] Category cache hit: ${cached.size} products")
                        emit(Result.Success(cached))
                    } catch (e: Exception) {
                        Timber.e(e, "[PRODUCT] Category cache error")
                        emit(Result.Error(AppError.Database(
                            message = "خطا در خواندن دسته‌بندی",
                            operation = "getByCategory"
                        )))
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "[PRODUCT] Category unexpected error")
                emit(Result.Error(exceptionHandler.handleException(e)))
            }
        }
    }
    
    /**
     * 🗑️ Clear all cached products
     */
    override suspend fun clearCache() {
        withContext(dispatcher) {
            try {
                Timber.d("[PRODUCT] Clearing cache")
                productDao.deleteAll()
                Timber.d("[PRODUCT] Cache cleared successfully")
            } catch (e: Exception) {
                Timber.e(e, "[PRODUCT] Failed to clear cache")
            }
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
        try {
            java.time.LocalDateTime.parse(dateString, java.time.format.DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            Timber.w(e, "[PRODUCT] Failed to parse date: $dateString")
            java.time.LocalDateTime.now()
        }
    } else {
        java.time.LocalDateTime.now()
    }
}