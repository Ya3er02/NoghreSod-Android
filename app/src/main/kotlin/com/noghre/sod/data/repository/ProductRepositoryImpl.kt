package com.noghre.sod.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.noghre.sod.core.config.AppConfig
import com.noghre.sod.core.result.Result
import com.noghre.sod.data.local.AppDatabase
import com.noghre.sod.data.mapper.toDomain
import com.noghre.sod.data.mapper.toEntity
import com.noghre.sod.data.remote.ApiService
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementation of ProductRepository.
 * Implements offline-first strategy using Room and Retrofit.
 *
 * Data flow:
 * 1. CACHE FIRST: Load from local Room database
 * 2. CHECK VALIDITY: If cache is fresh (< 24h), return cached data
 * 3. NETWORK: If cache is stale, fetch from API
 * 4. SAVE: Store API response in Room database
 * 5. EMIT: Return updated data to UI
 *
 * @param apiService Retrofit API client
 * @param database Room database instance
 * @param ioDispatcher IO dispatcher for background operations
 */
class ProductRepositoryImpl(
    private val apiService: ApiService,
    private val database: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : ProductRepository {

    private val productDao = database.productDao()
    private val categoryDao = database.categoryDao()

    /**
     * Gets paginated list of products with offline-first strategy.
     * 
     * Flow:
     * 1. Emit cached products as Loading state
     * 2. Fetch from API if cache is invalid
     * 3. Save to database
     * 4. Emit fresh products as Success
     * 5. On error, emit Error with cached data as fallback
     */
    override fun getProducts(
        page: Int,
        limit: Int,
        category: String?,
        sort: String?
    ): Flow<Result<List<Product>>> = networkBoundResource(
        query = {
            if (category != null) {
                productDao.getProductsByCategoryFlow(category)
            } else {
                productDao.getAllProductsFlow()
            }
        },
        fetch = {
            val response = if (category != null) {
                apiService.getProductsByCategory(category, page, limit).body()
            } else {
                apiService.getProducts(page, limit, category, sort).body()
            }
            response?.data ?: emptyList()
        },
        saveFetchResult = { products ->
            withContext(ioDispatcher) {
                productDao.insertProducts(products.map { it.toEntity() })
            }
        },
        shouldFetch = { cached ->
            cached.isEmpty() || cached.firstOrNull()?.isCacheValid(24) == false
        }
    ).map { result ->
        when (result) {
            is Result.Loading -> Result.Loading(result.data?.toDomain())
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> Result.Error(result.error, result.data?.toDomain())
        }
    }

    /**
     * Gets infinite scrolling paginated products using Paging 3.
     * 
     * Supports automatic page loading with:
     * - Pull-to-refresh
     * - Infinite scroll
     * - Cache + Network coordination via RemoteMediator
     */
    @OptIn(ExperimentalPagingApi::class)
    override fun getProductsPaged(
        category: String?
    ): Flow<PagingData<Product>> = Pager(
        config = PagingConfig(
            pageSize = AppConfig.Pagination.DEFAULT_PAGE_SIZE,
            prefetchDistance = AppConfig.Pagination.PREFETCH_DISTANCE,
            enablePlaceholders = true
        ),
        remoteMediator = ProductRemoteMediator(
            apiService = apiService,
            database = database,
            category = category
        ),
        pagingSourceFactory = {
            if (category != null) {
                productDao.pagingSourceByCategory(category)
            } else {
                productDao.pagingSource()
            }
        }
    ).flow.map { pagingData ->
        pagingData.map { entity -> entity.toDomain() }
    }

    /**
     * Gets a single product with offline-first approach.
     */
    override fun getProductDetail(id: String): Flow<Result<Product>> = networkBoundResource(
        query = { productDao.getProductFlow(id) },
        fetch = {
            val response = apiService.getProductDetail(id).body()
            response?.data ?: throw Exception("Product not found")
        },
        saveFetchResult = { product ->
            withContext(ioDispatcher) {
                productDao.insertProduct(product.toEntity())
            }
        }
    ).map { result ->
        when (result) {
            is Result.Loading -> Result.Loading(result.data?.toDomain())
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> Result.Error(result.error, result.data?.toDomain())
        }
    }

    /**
     * Searches products locally and remotely.
     */
    override fun searchProducts(
        query: String,
        page: Int,
        limit: Int
    ): Flow<Result<List<Product>>> = networkBoundResource(
        query = { productDao.searchProducts(query) },
        fetch = {
            val response = apiService.searchProducts(query, page, limit).body()
            response?.data ?: emptyList()
        },
        saveFetchResult = { products ->
            withContext(ioDispatcher) {
                productDao.insertProducts(products.map { it.toEntity() })
            }
        }
    ).map { result ->
        when (result) {
            is Result.Loading -> Result.Loading(result.data?.toDomain())
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> Result.Error(result.error, result.data?.toDomain())
        }
    }

    /**
     * Gets products filtered by category.
     */
    override fun getProductsByCategory(
        categoryId: String,
        page: Int,
        limit: Int
    ): Flow<Result<List<Product>>> = getProducts(
        page = page,
        limit = limit,
        category = categoryId
    )

    /**
     * Gets products within a price range.
     * Note: Filtering is done locally; consider adding server-side filtering.
     */
    override fun getProductsByPriceRange(
        minPrice: Double,
        maxPrice: Double
    ): Flow<Result<List<Product>>> = flowOf(
        try {
            val cached = productDao.getProductsByPriceRange(minPrice, maxPrice).lastOrNull() ?: emptyList()
            Result.Success(cached.toDomain())
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    )

    /**
     * Manually refresh products cache.
     * Forces fetch from API regardless of cache validity.
     */
    override fun refreshProducts(): Flow<Result<Unit>> = networkBoundResource(
        query = { flowOf(Unit) },
        fetch = {
            val response = apiService.getProducts(1, AppConfig.Pagination.DEFAULT_PAGE_SIZE).body()
            response?.data ?: emptyList()
        },
        saveFetchResult = { products ->
            withContext(ioDispatcher) {
                productDao.deleteAllProducts()
                productDao.insertProducts(products.map { it.toEntity() })
            }
        },
        shouldFetch = { true } // Always fetch
    ).map { Result.Success(Unit) }

    /**
     * Clears all cached products.
     */
    override suspend fun clearCache(): Result<Unit> = withContext(ioDispatcher) {
        try {
            productDao.deleteAllProducts()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}
