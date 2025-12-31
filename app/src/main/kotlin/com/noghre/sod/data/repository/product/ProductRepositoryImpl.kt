package com.noghre.sod.data.repository.product

import com.noghre.sod.core.result.Result
import com.noghre.sod.data.database.dao.ProductDao
import com.noghre.sod.data.error.ExceptionHandler
import com.noghre.sod.data.mapper.ProductMapper.toDomain
import com.noghre.sod.data.mapper.ProductMapper.toDomainList
import com.noghre.sod.data.mapper.ProductMapper.toEntity
import com.noghre.sod.data.network.NoghreSodApi
import com.noghre.sod.data.repository.networkBoundResource
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.ProductFilter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

/**
 * Product Repository Implementation.
 *
 * Comprehensive product data management:
 * - Product listing with pagination
 * - Advanced filtering (weight, gem type, price range)
 * - Search functionality
 * - Real-time price updates from gold/silver market
 * - Local caching for offline access
 * - Related products and recommendations
 *
 * For jewelry e-commerce optimized for high-res product images
 * and deep zoom functionality (macro shots of hallmarks).
 *
 * @param api Retrofit API client
 * @param productDao Database access object for products
 * @param ioDispatcher Dispatcher for I/O operations
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
class ProductRepositoryImpl @Inject constructor(
    private val api: NoghreSodApi,
    private val productDao: ProductDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IProductRepository {

    override fun getProducts(
        page: Int,
        pageSize: Int
    ): Flow<Result<List<Product>>> = networkBoundResource(
        query = {
            productDao.getProductsWithPagination(pageSize * page, pageSize)
                .map { it.toDomain() }
        },
        fetch = {
            api.getProducts(page, pageSize)
        },
        saveFetchResult = { response ->
            val products = response.toDomainList()
            productDao.insertAll(products.map { it.toEntity() })
            Timber.d("Products cached: ${products.size} items (page $page)")
        },
        shouldFetch = { localData ->
            localData.isEmpty() || isCacheStale()
        },
        onFetchFailed = { exception ->
            Timber.e(exception, "Failed to fetch products")
        }
    ).flowOn(ioDispatcher)

    override fun getProductsByCategory(
        categoryId: String,
        page: Int,
        pageSize: Int
    ): Flow<Result<List<Product>>> = networkBoundResource(
        query = {
            productDao.getProductsByCategory(categoryId, pageSize * page, pageSize)
                .map { it.toDomain() }
        },
        fetch = {
            api.getProductsByCategory(categoryId, page, pageSize)
        },
        saveFetchResult = { response ->
            val products = response.toDomainList()
            productDao.insertAll(products.map { it.toEntity() })
            Timber.d("Category products cached: ${products.size} items")
        },
        shouldFetch = { localData ->
            localData.isEmpty() || isCacheStale()
        },
        onFetchFailed = { exception ->
            Timber.e(exception, "Failed to fetch category products")
        }
    ).flowOn(ioDispatcher)

    override fun searchProducts(
        query: String,
        page: Int,
        pageSize: Int
    ): Flow<Result<List<Product>>> = networkBoundResource(
        query = {
            // Try local search first
            productDao.searchProducts(query, pageSize * page, pageSize)
                .map { it.toDomain() }
        },
        fetch = {
            // Always fetch fresh search results
            api.searchProducts(query, page, pageSize)
        },
        saveFetchResult = { response ->
            val products = response.toDomainList()
            productDao.insertAll(products.map { it.toEntity() })
            Timber.d("Search results cached: ${products.size} items for query '$query'")
        },
        shouldFetch = { _ -> true },  // Always fetch for search
        onFetchFailed = { exception ->
            Timber.e(exception, "Search failed for query: $query")
        }
    ).flowOn(ioDispatcher)

    override fun filterProducts(
        filter: ProductFilter,
        page: Int,
        pageSize: Int
    ): Flow<Result<List<Product>>> = networkBoundResource(
        query = {
            // Try local filtering
            productDao.filterProducts(
                minPrice = filter.priceMin,
                maxPrice = filter.priceMax,
                minWeight = filter.weightMin,
                maxWeight = filter.weightMax,
                gemType = filter.gemType,
                offset = pageSize * page,
                limit = pageSize
            ).map { it.toDomain() }
        },
        fetch = {
            // Fetch filtered results
            api.filterProducts(
                priceMin = filter.priceMin,
                priceMax = filter.priceMax,
                weightMin = filter.weightMin,
                weightMax = filter.weightMax,
                gemType = filter.gemType,
                page = page,
                pageSize = pageSize
            )
        },
        saveFetchResult = { response ->
            val products = response.toDomainList()
            productDao.insertAll(products.map { it.toEntity() })
            Timber.d("Filtered products cached: ${products.size} items")
        },
        shouldFetch = { _ -> true },  // Always fetch for filters
        onFetchFailed = { exception ->
            Timber.e(exception, "Product filtering failed")
        }
    ).flowOn(ioDispatcher)

    override fun getProductDetail(productId: String): Flow<Result<Product>> =
        networkBoundResource(
            query = {
                productDao.getProductById(productId)?.toDomain()
                    ?: throw Exception("Product not found locally")
            },
            fetch = {
                api.getProductDetail(productId)
            },
            saveFetchResult = { response ->
                val product = response.toDomain()
                productDao.insert(product.toEntity())
                Timber.d("Product detail cached: $productId")
            },
            shouldFetch = { _ -> true },  // Always fetch for details
            onFetchFailed = { exception ->
                Timber.e(exception, "Failed to fetch product detail: $productId")
            }
        ).flowOn(ioDispatcher)

    override fun getFeaturedProducts(): Flow<Result<List<Product>>> =
        networkBoundResource(
            query = {
                productDao.getFeaturedProducts().map { it.toDomain() }
            },
            fetch = {
                api.getFeaturedProducts()
            },
            saveFetchResult = { response ->
                val products = response.toDomainList()
                productDao.insertAll(products.map { it.toEntity() })
                Timber.d("Featured products cached: ${products.size} items")
            },
            shouldFetch = { localData ->
                localData.isEmpty() || isCacheStale()
            },
            onFetchFailed = { exception ->
                Timber.e(exception, "Failed to fetch featured products")
            }
        ).flowOn(ioDispatcher)

    override fun getSaleProducts(
        page: Int,
        pageSize: Int
    ): Flow<Result<List<Product>>> = networkBoundResource(
        query = {
            productDao.getSaleProducts(pageSize * page, pageSize)
                .map { it.toDomain() }
        },
        fetch = {
            api.getSaleProducts(page, pageSize)
        },
        saveFetchResult = { response ->
            val products = response.toDomainList()
            productDao.insertAll(products.map { it.toEntity() })
            Timber.d("Sale products cached: ${products.size} items")
        },
        shouldFetch = { localData ->
            localData.isEmpty() || isCacheStale()
        },
        onFetchFailed = { exception ->
            Timber.e(exception, "Failed to fetch sale products")
        }
    ).flowOn(ioDispatcher)

    override fun getRelatedProducts(productId: String): Flow<Result<List<Product>>> =
        networkBoundResource(
            query = {
                productDao.getRelatedProducts(productId, limit = 6)
                    .map { it.toDomain() }
            },
            fetch = {
                api.getRelatedProducts(productId)
            },
            saveFetchResult = { response ->
                val products = response.toDomainList()
                productDao.insertAll(products.map { it.toEntity() })
                Timber.d("Related products cached: ${products.size} items")
            },
            shouldFetch = { localData ->
                localData.isEmpty()
            },
            onFetchFailed = { exception ->
                Timber.e(exception, "Failed to fetch related products")
            }
        ).flowOn(ioDispatcher)

    override fun getMarketPrices(): Flow<Result<Map<String, Double>>> =
        networkBoundResource(
            query = {
                // Get cached prices (last known values)
                val cachedPrices = productDao.getAllProducts()
                    .associate { it.id to it.currentPrice }
                cachedPrices.ifEmpty { emptyMap() }
            },
            fetch = {
                api.getMarketPrices()
            },
            saveFetchResult = { response ->
                // Update product prices with market data
                for ((productId, price) in response) {
                    productDao.updateProductPrice(productId, price)
                }
                Timber.d("Market prices updated: ${response.size} products")
            },
            shouldFetch = { _ ->
                // Always fetch market prices for real-time updates
                true
            },
            onFetchFailed = { exception ->
                Timber.e(exception, "Failed to fetch market prices")
            }
        ).flowOn(ioDispatcher)

    // ============ HELPERS ============

    private suspend fun isCacheStale(): Boolean {
        return try {
            val products = productDao.getAllProducts()
            if (products.isEmpty()) {
                true
            } else {
                val ageInMs = System.currentTimeMillis() - (products.firstOrNull()?.lastUpdated ?: 0)
                ageInMs > 2 * 60 * 60 * 1000  // 2 hours
            }
        } catch (e: Exception) {
            Timber.e(e, "Error checking cache staleness")
            true
        }
    }
}
