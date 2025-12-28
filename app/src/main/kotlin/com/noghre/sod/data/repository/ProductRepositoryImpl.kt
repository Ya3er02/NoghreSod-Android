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

class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi,
    private val productDao: ProductDao
) : ProductRepository {
    
    override suspend fun getProducts(page: Int): Result<List<Product>> = try {
        Timber.d("Fetching products from API, page: $page")
        val response = productApi.getProducts(page)
        
        // Save to local database
        productDao.insertAll(response.products)
        Timber.d("Products saved to local DB: ${response.products.size}")
        
        Result.Success(response.products)
    } catch (e: Exception) {
        Timber.e("Error fetching products: ${e.message}")
        // Fallback to local data
        try {
            val localProducts = productDao.getAllProducts()
            if (localProducts.isNotEmpty()) {
                Timber.d("Returning local products: ${localProducts.size}")
                Result.Success(localProducts)
            } else {
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
        
        // Save to local database
        productDao.insert(response)
        Timber.d("Product saved to local DB: ${response.name}")
        
        Result.Success(response)
    } catch (e: Exception) {
        Timber.e("Error fetching product: ${e.message}")
        // Fallback to local data
        try {
            val localProduct = productDao.getProductById(id)
            if (localProduct != null) {
                Timber.d("Returning local product: ${localProduct.name}")
                Result.Success(localProduct)
            } else {
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
        Timber.d("Favorites: ${favorites.size}")
        Result.Success(favorites)
    } catch (e: Exception) {
        Timber.e("Error fetching favorites: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Unknown error"))
    }
    
    override suspend fun toggleFavorite(productId: String): Result<Unit> = try {
        Timber.d("Toggling favorite for product: $productId")
        
        val product = productDao.getProductById(productId)
        if (product != null) {
            val updatedProduct = product.copy(isFavorite = !product.isFavorite)
            productDao.update(updatedProduct)
            Timber.d("Favorite toggled for: $productId")
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
}
