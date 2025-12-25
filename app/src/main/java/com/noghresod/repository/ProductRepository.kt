package com.noghresod.repository

import com.noghresod.network.api.ApiService
import com.noghresod.network.cache.AdvancedCacheManager
import com.noghresod.network.cache.CachePolicy
import com.noghresod.network.cache.StaleWhileRevalidateRepository
import com.noghresod.network.dto.PaginatedResponse
import com.noghresod.network.dto.ProductDetailDto
import com.noghresod.network.dto.ProductDto
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@ViewModelScoped
class ProductRepository @Inject constructor(
    private val apiService: ApiService,
    cacheManager: AdvancedCacheManager
) : StaleWhileRevalidateRepository<Any>(cacheManager) {
    
    /**
     * دريافت لیست محصولات با stale-while-revalidate
     */
    fun getProducts(
        page: Int = 1,
        pageSize: Int = 20
    ): Flow<DataState<PaginatedResponse<ProductDto>>> = flow {
        val cacheKey = "products_${page}_${pageSize}"
        
        // ✅ Check cache first
        val policy = CachePolicy.StaleWhileRevalidate(
            freshDuration = 5.minutes.inWholeMilliseconds,
            staleDuration = 1.hours.inWholeMilliseconds
        )
        
        val cached = cacheManager.getCache<PaginatedResponse<ProductDto>>(cacheKey)
        
        if (cached != null) {
            // ✅ Emit cached data immediately
            if (cacheManager.isCacheFresh(cacheKey, policy)) {
                emit(DataState.Fresh(cached))
                return@flow
            } else {
                emit(DataState.Cached(cached))
            }
        } else {
            emit(DataState.Loading)
        }
        
        // ✅ Fetch fresh data in background
        try {
            val response = apiService.getAllProducts(page, pageSize)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.isSuccessful() == true && body.data != null) {
                    // ✅ Update cache
                    cacheManager.putCache(
                        key = cacheKey,
                        data = body.data,
                        policy = policy
                    )
                    
                    emit(DataState.Fresh(body.data))
                } else {
                    emit(DataState.Error(Exception(body?.getErrorMessage() ?: "Unknown error")))
                }
            } else {
                val error = Exception("HTTP ${response.code()}: ${response.message()}")
                
                if (cached != null) {
                    emit(DataState.Stale(cached, error))
                } else {
                    emit(DataState.Error(error))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to fetch products")
            
            if (cached != null) {
                emit(DataState.Stale(cached, e))
            } else {
                emit(DataState.Error(e))
            }
        }
    }.catch { e ->
        Timber.e(e, "❌ Error in getProducts flow")
        emit(DataState.Error(e))
    }
    
    /**
     * جستجو محصول با ID
     */
    fun getProductById(
        productId: String
    ): Flow<DataState<ProductDetailDto>> = flow {
        val cacheKey = "product_$productId"
        
        val policy = CachePolicy.TimeToLive(30.minutes.inWholeMilliseconds)
        
        // ✅ Check cache
        val cached = cacheManager.getCache<ProductDetailDto>(cacheKey)
        if (cached != null && cacheManager.isCacheValid(cacheKey, policy)) {
            emit(DataState.Fresh(cached))
            return@flow
        }
        
        emit(DataState.Loading)
        
        try {
            val response = apiService.getProductById(productId)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.isSuccessful() == true && body.data != null) {
                    cacheManager.putCache(
                        key = cacheKey,
                        data = body.data,
                        policy = policy
                    )
                    
                    emit(DataState.Fresh(body.data))
                } else {
                    emit(DataState.Error(Exception(body?.getErrorMessage() ?: "Unknown error")))
                }
            } else {
                emit(DataState.Error(Exception("HTTP ${response.code()}: ${response.message()}")))
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to fetch product detail")
            emit(DataState.Error(e))
        }
    }.catch { e ->
        Timber.e(e, "❌ Error in getProductById flow")
        emit(DataState.Error(e))
    }
    
    /**
     * جستجو محصولات featured
     */
    fun getFeaturedProducts(): Flow<DataState<List<ProductDto>>> = flow {
        val cacheKey = "featured_products"
        
        val policy = CachePolicy.TimeToLive(2.hours.inWholeMilliseconds)
        
        val cached = cacheManager.getCache<List<ProductDto>>(cacheKey)
        if (cached != null && cacheManager.isCacheValid(cacheKey, policy)) {
            emit(DataState.Fresh(cached))
            return@flow
        }
        
        emit(DataState.Loading)
        
        try {
            val response = apiService.getFeaturedProducts(limit = 10)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.isSuccessful() == true && body.data != null) {
                    cacheManager.putCache(
                        key = cacheKey,
                        data = body.data,
                        policy = policy
                    )
                    
                    emit(DataState.Fresh(body.data))
                } else {
                    emit(DataState.Error(Exception(body?.getErrorMessage() ?: "Unknown error")))
                }
            } else {
                emit(DataState.Error(Exception("HTTP ${response.code()}: ${response.message()}")))
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to fetch featured products")
            emit(DataState.Error(e))
        }
    }.catch { e ->
        Timber.e(e, "❌ Error in getFeaturedProducts flow")
        emit(DataState.Error(e))
    }
    
    /**
     * جستجو محصولات جديد
     */
    fun getNewProducts(
        limit: Int = 10
    ): Flow<DataState<List<ProductDto>>> = flow {
        val cacheKey = "new_products_$limit"
        
        val policy = CachePolicy.TimeToLive(1.hours.inWholeMilliseconds)
        
        val cached = cacheManager.getCache<List<ProductDto>>(cacheKey)
        if (cached != null && cacheManager.isCacheValid(cacheKey, policy)) {
            emit(DataState.Fresh(cached))
            return@flow
        }
        
        emit(DataState.Loading)
        
        try {
            val response = apiService.getNewProducts(limit = limit)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.isSuccessful() == true && body.data != null) {
                    cacheManager.putCache(
                        key = cacheKey,
                        data = body.data,
                        policy = policy
                    )
                    
                    emit(DataState.Fresh(body.data))
                } else {
                    emit(DataState.Error(Exception(body?.getErrorMessage() ?: "Unknown error")))
                }
            } else {
                emit(DataState.Error(Exception("HTTP ${response.code()}: ${response.message()}")))
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to fetch new products")
            emit(DataState.Error(e))
        }
    }.catch { e ->
        Timber.e(e, "❌ Error in getNewProducts flow")
        emit(DataState.Error(e))
    }
    
    /**
     * جستجو با cursor-based pagination
     */
    fun getProductsFeed(
        cursor: String? = null,
        limit: Int = 20
    ): Flow<DataState<PaginatedResponse<ProductDto>>> = flow {
        val cacheKey = "products_feed_${cursor ?: "initial"}_$limit"
        
        val policy = CachePolicy.TimeToLive(10.minutes.inWholeMilliseconds)
        
        val cached = cacheManager.getCache<PaginatedResponse<ProductDto>>(cacheKey)
        if (cached != null && cacheManager.isCacheValid(cacheKey, policy)) {
            emit(DataState.Fresh(cached))
            return@flow
        }
        
        emit(DataState.Loading)
        
        try {
            val response = apiService.getProductsFeed(cursor, limit)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.isSuccessful() == true && body.data != null) {
                    cacheManager.putCache(
                        key = cacheKey,
                        data = body.data,
                        policy = policy
                    )
                    
                    emit(DataState.Fresh(body.data))
                } else {
                    emit(DataState.Error(Exception(body?.getErrorMessage() ?: "Unknown error")))
                }
            } else {
                emit(DataState.Error(Exception("HTTP ${response.code()}: ${response.message()}")))
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to fetch products feed")
            emit(DataState.Error(e))
        }
    }.catch { e ->
        Timber.e(e, "❌ Error in getProductsFeed flow")
        emit(DataState.Error(e))
    }
    
    /**
     * پاك کردن cacheها برای refresh
     */
    suspend fun invalidateProductsCache() {
        cacheManager.invalidateCache("featured_products", cascade = true)
        cacheManager.invalidateCache("new_products_10", cascade = true)
        
        // Invalidate paginated products
        for (page in 1..5) {
            cacheManager.invalidateCache("products_${page}_20", cascade = false)
        }
        
        Timber.d("✅ Products cache invalidated")
    }
}
