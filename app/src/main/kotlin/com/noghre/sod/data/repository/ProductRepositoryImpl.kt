package com.noghre.sod.data.repository

import com.noghre.sod.data.dto.ProductDto
import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.mapper.CachePolicy
import com.noghre.sod.data.mapper.ProductMapper.toDomain
import com.noghre.sod.data.mapper.ProductMapper.toEntity
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.exception.ApiException
import com.noghre.sod.data.remote.network.NetworkMonitor
import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * Product Repository Implementation with offline-first strategy.
 * Caches data locally and syncs with server when online.
 */
class ProductRepositoryImpl @Inject constructor(
    private val api: NoghreSodApiService,
    private val productDao: ProductDao,
    private val networkMonitor: NetworkMonitor
) : ProductRepository {

    /**
     * Get products with offline-first strategy:
     * 1. Emit Loading state
     * 2. Try to emit cached data if available
     * 3. Fetch fresh data from API if online
     * 4. Update cache and emit fresh data
     * 5. Emit error if offline and no cache
     */
    override fun getProducts(
        page: Int,
        categoryId: String?,
        sortBy: String?
    ): Flow<Result<List<Product>>> = flow {
        try {
            emit(Result.Loading)

            // Try to emit cached data
            val cachedData = productDao.getRecentProducts(limit = 20)
                .map { entities -> entities.map { it.toDomain() } }
                .catch { Timber.e(it, "Error reading cache") }
                .collect { cached ->
                    if (cached.isNotEmpty()) {
                        emit(Result.Success(cached))
                    }
                }

            // Fetch from API if online
            if (networkMonitor.isNetworkAvailable()) {
                try {
                    val response = api.getProducts(
                        page = page,
                        categoryId = categoryId,
                        sort = sortBy
                    )

                    if (response.success && response.data != null) {
                        val domainProducts = response.data.map { it.toDomain() }
                        val entities = response.data.map { dto ->
                            dto.run {
                                com.noghre.sod.data.local.entity.ProductEntity(
                                    id = id,
                                    name = name,
                                    nameEn = nameEn,
                                    description = description,
                                    descriptionEn = descriptionEn,
                                    price = price,
                                    discountPrice = discountPrice,
                                    images = com.google.gson.Gson().toJson(images),
                                    categoryId = categoryId ?: "",
                                    stock = stock,
                                    rating = rating,
                                    reviewCount = reviewCount,
                                    weight = weight,
                                    material = material,
                                    specifications = specifications?.let {
                                        com.google.gson.Gson().toJson(it)
                                    },
                                    sellerId = sellerId,
                                    sellerName = sellerName,
                                    sellerRating = sellerRating
                                )
                            }
                        }
                        productDao.insertProducts(entities)
                        emit(Result.Success(domainProducts))
                        Timber.d("Products fetched and cached: ${response.data.size} items")
                    } else {
                        emit(Result.Error(response.message ?: "Unknown error"))
                    }
                } catch (e: ApiException) {
                    Timber.e(e, "API Error fetching products")
                    emit(Result.Error(e.message ?: "Network error"))
                } catch (e: Exception) {
                    Timber.e(e, "Unexpected error fetching products")
                    emit(Result.Error(e.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in getProducts")
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getProductById(id: String): Flow<Result<Product>> = flow {
        try {
            emit(Result.Loading)

            // Try cache first
            val cached = productDao.getProductById(id)
            if (cached != null && CachePolicy.isCacheValid(cached.cachedAt)) {
                emit(Result.Success(cached.toDomain()))
                Timber.d("Product from cache: $id")
            }

            // Fetch from API if online
            if (networkMonitor.isNetworkAvailable()) {
                try {
                    val response = api.getProductById(id)
                    if (response.success && response.data != null) {
                        val entity = response.data.run {
                            com.noghre.sod.data.local.entity.ProductEntity(
                                id = id,
                                name = name,
                                nameEn = nameEn,
                                description = description,
                                descriptionEn = descriptionEn,
                                price = price,
                                discountPrice = discountPrice,
                                images = com.google.gson.Gson().toJson(images),
                                categoryId = categoryId,
                                stock = stock,
                                rating = rating,
                                reviewCount = reviewCount,
                                weight = weight,
                                material = material,
                                specifications = specifications?.let {
                                    com.google.gson.Gson().toJson(it)
                                },
                                sellerId = sellerId,
                                sellerName = sellerName,
                                sellerRating = sellerRating
                            )
                        }
                        productDao.insertProducts(listOf(entity))
                        emit(Result.Success(response.data.toDomain()))
                    } else {
                        emit(Result.Error(response.message ?: "Product not found"))
                    }
                } catch (e: ApiException) {
                    Timber.e(e, "API Error fetching product: $id")
                    emit(Result.Error(e.message ?: "Network error"))
                }
            } else if (cached == null) {
                emit(Result.Error("No internet and no cached data"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in getProductById")
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    override fun searchProducts(query: String): Flow<Result<List<Product>>> = flow {
        try {
            emit(Result.Loading)

            // Local search
            val localResults = productDao.searchProducts(query)
                .map { entities -> entities.map { it.toDomain() } }
                .catch { Timber.e(it, "Error searching cache") }

            // Fetch from API
            if (networkMonitor.isNetworkAvailable()) {
                try {
                    val response = api.searchProducts(query)
                    if (response.success && response.data != null) {
                        emit(Result.Success(response.data.map { it.toDomain() }))
                    } else {
                        emit(Result.Error(response.message ?: "Search failed"))
                    }
                } catch (e: ApiException) {
                    Timber.e(e, "API Error searching products")
                    emit(Result.Error(e.message ?: "Network error"))
                }
            } else {
                localResults.collect { emit(Result.Success(it)) }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error searching products")
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getFeaturedProducts(): Flow<Result<List<Product>>> = flow {
        try {
            emit(Result.Loading)

            if (networkMonitor.isNetworkAvailable()) {
                try {
                    val response = api.getFeaturedProducts()
                    if (response.success && response.data != null) {
                        emit(Result.Success(response.data.map { it.toDomain() }))
                    } else {
                        emit(Result.Error(response.message ?: "Failed to fetch"))
                    }
                } catch (e: ApiException) {
                    emit(Result.Error(e.message ?: "Network error"))
                }
            } else {
                emit(Result.Error("No internet connection"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching featured products")
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getNewArrivals(): Flow<Result<List<Product>>> = flow {
        try {
            emit(Result.Loading)

            if (networkMonitor.isNetworkAvailable()) {
                try {
                    val response = api.getNewArrivals()
                    if (response.success && response.data != null) {
                        emit(Result.Success(response.data.map { it.toDomain() }))
                    } else {
                        emit(Result.Error(response.message ?: "Failed to fetch"))
                    }
                } catch (e: ApiException) {
                    emit(Result.Error(e.message ?: "Network error"))
                }
            } else {
                emit(Result.Error("No internet connection"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching new arrivals")
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun refreshProducts(): Result<Unit> = try {
        if (!networkMonitor.isNetworkAvailable()) {
            return Result.Error("No internet connection")
        }

        val response = api.getProducts()
        if (response.success && response.data != null) {
            val entities = response.data.map { dto ->
                dto.run {
                    com.noghre.sod.data.local.entity.ProductEntity(
                        id = id,
                        name = name,
                        nameEn = nameEn,
                        description = description,
                        descriptionEn = descriptionEn,
                        price = price,
                        discountPrice = discountPrice,
                        images = com.google.gson.Gson().toJson(images),
                        categoryId = categoryId,
                        stock = stock,
                        rating = rating,
                        reviewCount = reviewCount,
                        weight = weight,
                        material = material,
                        specifications = specifications?.let {
                            com.google.gson.Gson().toJson(it)
                        },
                        sellerId = sellerId,
                        sellerName = sellerName,
                        sellerRating = sellerRating
                    )
                }
            }
            productDao.insertProducts(entities)
            Result.Success(Unit)
        } else {
            Result.Error(response.message ?: "Refresh failed")
        }
    } catch (e: Exception) {
        Timber.e(e, "Error refreshing products")
        Result.Error(e.message ?: "Unknown error")
    }
}
