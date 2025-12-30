package com.noghre.sod.data.repository

import com.noghre.sod.core.error.AppException
import com.noghre.sod.core.util.Result
import com.noghre.sod.core.util.map
import com.noghre.sod.data.local.ProductDao
import com.noghre.sod.data.remote.ProductApi
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * Product Repository Implementation with smart caching strategy
 * - Implements TTL (Time-To-Live) for cache invalidation
 * - Handles pagination correctly with proper offset
 * - Syncs favorites with network when possible
 */
class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi,
    private val productDao: ProductDao
) : ProductRepository {
    
    companion object {
        // Cache TTL: 24 hours
        private const val CACHE_TTL_MS = 24 * 60 * 60 * 1000L
        // Pagination
        private const val PAGE_SIZE = 20
    }
    
    override suspend fun getProducts(page: Int): Result<List<Product>> = try {
        require(page > 0) { "Page must be greater than 0" }
        Timber.d("Fetching products from API, page: $page")
        
        val response = productApi.getProducts(page)
        
        // ✅ Fix 2.1: Only clear cache on first page (fresh load)
        if (page == 1) {
            Timber.d("First page requested - clearing old cache")
            productDao.deleteAllProducts()
        }
        
        // Insert products with current timestamp for TTL tracking
        response.products.forEach { product ->
            productDao.insert(product)
        }
        Timber.d("Products saved to local DB: ${response.products.size}")
        
        Result.Success(response.products)
    } catch (e: Exception) {
        Timber.e("Error fetching products: ${e.message}")
        // ✅ Fix 2.2: Fallback respects pagination
        return try {
            val offset = (page - 1) * PAGE_SIZE
            val localProducts = productDao.getProductsPaginated(PAGE_SIZE, offset)
            
            if (localProducts.isNotEmpty()) {
                Timber.d("Returning local products for page $page: ${localProducts.size}")
                Result.Success(localProducts)
            } else {
                Timber.w("No cached data available for page: $page")
                Result.Error(AppException.NetworkException("No products available"))
            }
        } catch (ex: Exception) {
            Timber.e("Error loading local products: ${ex.message}")
            Result.Error(AppException.DatabaseException(ex.message ?: "Unknown error"))
        }
    }
    
    override suspend fun getProductById(id: String): Result<Product> = try {
        Timber.d("Fetching product by ID: $id")
        val response = productApi.getProductById(id)
        
        // Save to local database with timestamp
        productDao.insert(response)
        Timber.d("Product saved to local DB: ${response.name}")
        
        Result.Success(response)
    } catch (e: Exception) {
        Timber.e("Error fetching product: ${e.message}")
        // Fallback to local data
        return try {
            val localProduct = productDao.getProductById(id)
            if (localProduct != null) {
                Timber.d("Returning cached product: ${localProduct.name}")
                // Check if cache is expired
                if (isCacheExpired(localProduct.cachedAt)) {
                    Timber.w("Cache for product $id is expired but using as fallback")
                }
                Result.Success(localProduct)
            } else {
                Timber.w("Product not found in cache: $id")
                Result.Error(AppException.NetworkException("Product not found"))
            }
        } catch (ex: Exception) {
            Timber.e("Error loading local product: ${ex.message}")
            Result.Error(AppException.DatabaseException(ex.message ?: "Unknown error"))
        }
    }
    
    override suspend fun searchProducts(query: String): Result<List<Product>> = try {
        Timber.d("Searching products: $query")
        val results = productDao.searchProducts("%$query%")
        
        if (results.isEmpty()) {
            Timber.d("No products found for query: $query")
            Result.Success(emptyList())
        } else {
            Timber.d("Found ${results.size} products for: $query")
            Result.Success(results)
        }
    } catch (e: Exception) {
        Timber.e("Error searching products: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Search failed"))
    }
    
    override suspend fun getFavorites(): Result<List<Product>> = try {
        Timber.d("Fetching favorites")
        val favorites = productDao.getFavorites()
        Timber.d("Favorites count: ${favorites.size}")
        Result.Success(favorites)
    } catch (e: Exception) {
        Timber.e("Error fetching favorites: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Unknown error"))
    }
    
    override suspend fun toggleFavorite(productId: String): Result<Unit> = try {
        Timber.d("Toggling favorite for product: $productId")
        
        val product = productDao.getProductById(productId)
        if (product != null) {
            val newFavoriteState = !product.isFavorite
            val updatedProduct = product.copy(isFavorite = newFavoriteState)
            productDao.update(updatedProduct)
            
            // ✅ Fix 2.3: Try to sync with backend, but don't fail if network is down
            try {
                if (newFavoriteState) {
                    productApi.addToFavorites(productId)
                    Timber.d("Synced favorite addition to server: $productId")
                } else {
                    productApi.removeFromFavorites(productId)
                    Timber.d("Synced favorite removal to server: $productId")
                }
            } catch (networkError: Exception) {
                Timber.w("Failed to sync favorite with server, but local update succeeded: ${networkError.message}")
                // Continue anyway - local state is updated
            }
            
            Timber.d("Favorite toggled for: $productId, new state: $newFavoriteState")
            Result.Success(Unit)
        } else {
            Timber.w("Product not found: $productId")
            Result.Error(AppException.NotFound("Product not found"))
        }
    } catch (e: Exception) {
        Timber.e("Error toggling favorite: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Unknown error"))
    }
    
    override suspend fun removeFavorite(productId: String): Result<Unit> = try {
        Timber.d("Removing from favorites: $productId")
        
        val product = productDao.getProductById(productId)
        if (product != null) {
            val updatedProduct = product.copy(isFavorite = false)
            productDao.update(updatedProduct)
            
            // Try to sync with backend
            try {
                productApi.removeFromFavorites(productId)
                Timber.d("Synced favorite removal to server: $productId")
            } catch (networkError: Exception) {
                Timber.w("Failed to sync removal with server: ${networkError.message}")
            }
            
            Timber.d("Removed from favorites: $productId")
            Result.Success(Unit)
        } else {
            Timber.w("Product not found: $productId")
            Result.Error(AppException.NotFound("Product not found"))
        }
    } catch (e: Exception) {
        Timber.e("Error removing favorite: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Unknown error"))
    }
    
    override fun observeProducts(): Flow<List<Product>> {
        Timber.d("Observing products")
        return productDao.observeAllProducts()
    }
    
    override fun observeFavorites(): Flow<List<Product>> {
        Timber.d("Observing favorites")
        return productDao.observeFavorites()
    }
    
    /**
     * Check if cache entry has expired based on TTL
     */
    private fun isCacheExpired(cachedAtMs: Long): Boolean {
        val ageMs = System.currentTimeMillis() - cachedAtMs
        return ageMs > CACHE_TTL_MS
    }
    
    /**
     * Clear all expired cache entries
     */
    suspend fun clearExpiredCache() {
        try {
            val allProducts = productDao.getAllProducts()
            val expiredProducts = allProducts.filter { isCacheExpired(it.cachedAt) }
            
            if (expiredProducts.isNotEmpty()) {
                expiredProducts.forEach { productDao.delete(it) }
                Timber.i("Cleared ${expiredProducts.size} expired cache entries")
            }
        } catch (e: Exception) {
            Timber.e("Error clearing expired cache: ${e.message}")
        }
    }
}
