package com.noghre.sod.data.repository

import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.remote.api.ProductApi
import com.noghre.sod.data.remote.dto.ProductDto
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.Result
import com.noghre.sod.domain.model.safeCall
import com.noghre.sod.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of ProductRepository.
 * 
 * Handles data operations with caching strategy:
 * - Try network first
 * - Cache result locally
 * - Fallback to cache on network failure
 * - Return most recent data from cache while refreshing from network
 * 
 * @property productApi Remote API client
 * @property productDao Local database access
 * 
 * @since 1.0.0
 */
class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi,
    private val productDao: ProductDao
) : ProductRepository {
    
    override suspend fun getProducts(
        page: Int,
        limit: Int
    ): Result<List<Product>> = safeCall {
        Timber.d("Fetching products - page: $page, limit: $limit")
        
        return@safeCall try {
            // Try fetching from network
            val remoteProducts = productApi.getProducts(page, limit)
            
            // Cache the results
            val entities = remoteProducts.map { it.toEntity() }
            productDao.insertProducts(entities)
            
            Timber.i("Fetched ${remoteProducts.size} products from network")
            remoteProducts.map { it.toDomain() }
        } catch (e: Exception) {
            Timber.w(e, "Network fetch failed, using cache")
            // Fallback to cache
            productDao.getAllProducts().map { it.toDomain() }
        }
    }
    
    override suspend fun getProductById(id: String): Result<Product> = safeCall {
        Timber.d("Fetching product - id: $id")
        
        return@safeCall try {
            // Try network first
            val remoteProduct = productApi.getProductById(id)
            
            // Cache it
            productDao.insertProduct(remoteProduct.toEntity())
            
            Timber.i("Fetched product from network - id: $id")
            remoteProduct.toDomain()
        } catch (e: Exception) {
            Timber.w(e, "Network fetch failed for product $id, using cache")
            // Fallback to cache
            productDao.getProductById(id)
                ?.toDomain()
                ?: throw Exception("Product not found - id: $id")
        }
    }
    
    override suspend fun searchProducts(
        query: String,
        category: String?
    ): Result<List<Product>> = safeCall {
        Timber.d("Searching products - query: $query, category: $category")
        
        return@safeCall try {
            val results = productApi.searchProducts(query, category)
            results.map { it.toDomain() }
        } catch (e: Exception) {
            Timber.w(e, "Search failed")
            emptyList()
        }
    }
    
    override suspend fun getProductsByCategory(
        category: String,
        page: Int
    ): Result<List<Product>> = safeCall {
        Timber.d("Fetching products by category - category: $category, page: $page")
        
        return@safeCall try {
            val remoteProducts = productApi.getProductsByCategory(category, page)
            
            val entities = remoteProducts.map { it.toEntity() }
            productDao.insertProducts(entities)
            
            Timber.i("Fetched ${remoteProducts.size} products for category: $category")
            remoteProducts.map { it.toDomain() }
        } catch (e: Exception) {
            Timber.w(e, "Category fetch failed, using cache")
            productDao.getProductsByCategory(category).map { it.toDomain() }
        }
    }
    
    override fun observeProducts(): Flow<Result<List<Product>>> = flow {
        try {
            emit(Result.Loading())
            
            productDao.observeAllProducts().collect { entities ->
                val products = entities.map { it.toDomain() }
                emit(Result.Success(products))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error observing products")
            emit(Result.Error(e))
        }
    }
    
    override suspend fun refreshProducts(): Result<Unit> = safeCall {
        Timber.d("Refreshing products from network")
        
        try {
            val products = productApi.getProducts(page = 1, limit = 100)
            
            // Clear old data and insert new
            productDao.deleteAllProducts()
            productDao.insertProducts(products.map { it.toEntity() })
            
            Timber.i("Products refreshed - count: ${products.size}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to refresh products")
            throw e
        }
    }
    
    override suspend fun cacheProduct(product: Product): Result<Unit> = safeCall {
        productDao.insertProduct(product.toEntity())
        Timber.d("Product cached - id: ${product.id}")
    }
    
    override suspend fun clearCache(): Result<Unit> = safeCall {
        productDao.deleteAllProducts()
        Timber.i("Product cache cleared")
    }
    
    override suspend fun getProductCount(): Result<Int> = safeCall {
        productDao.getProductCount()
    }
    
    /**
     * Extension function to map DTO to Entity
     */
    private fun ProductDto.toEntity(): ProductEntity {
        return ProductEntity(
            id = this.id,
            name = this.name,
            description = this.description,
            price = this.price,
            originalPrice = this.originalPrice,
            discountPercentage = this.discountPercentage,
            category = this.category,
            imageUrls = this.images,
            rating = this.rating,
            totalReviews = this.totalReviews,
            inStock = this.inStock,
            weight = this.weight,
            material = this.material,
            dimensions = this.dimensions,
            tags = this.tags,
            sku = this.sku,
            createdAt = this.createdAt,
            lastUpdated = this.updatedAt,
            isFeatured = this.isFeatured,
            isNew = this.isNew
        )
    }
    
    /**
     * Extension function to map DTO to Domain Model
     */
    private fun ProductDto.toDomain(): Product {
        return Product(
            id = this.id,
            name = this.name,
            description = this.description,
            price = this.price,
            originalPrice = this.originalPrice,
            discountPercentage = this.discountPercentage,
            category = this.category,
            images = this.images,
            rating = this.rating,
            totalReviews = this.totalReviews,
            inStock = this.inStock,
            weight = this.weight,
            material = this.material,
            dimensions = this.dimensions,
            createdAt = this.createdAt,
            lastUpdated = this.updatedAt,
            tags = this.tags,
            sku = this.sku
        )
    }
    
    /**
     * Extension function to map Entity to Domain Model
     */
    private fun ProductEntity.toDomain(): Product {
        return Product(
            id = this.id,
            name = this.name,
            description = this.description,
            price = this.price,
            originalPrice = this.originalPrice,
            discountPercentage = this.discountPercentage,
            category = this.category,
            images = this.imageUrls,
            rating = this.rating,
            totalReviews = this.totalReviews,
            inStock = this.inStock,
            weight = this.weight,
            material = this.material,
            dimensions = this.dimensions,
            createdAt = this.createdAt,
            lastUpdated = this.lastUpdated,
            tags = this.tags,
            sku = this.sku
        )
    }
}
