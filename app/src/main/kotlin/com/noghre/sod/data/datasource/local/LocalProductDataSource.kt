package com.noghre.sod.data.datasource.local

import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.local.entity.FavoriteProductEntity
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.local.entity.ProductReviewEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalProductDataSource @Inject constructor(
    private val productDao: ProductDao,
) {

    suspend fun insertProduct(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    suspend fun insertProducts(products: List<ProductEntity>) {
        productDao.insertProducts(products)
    }

    fun getProduct(productId: String): Flow<ProductEntity?> {
        return productDao.getProduct(productId)
    }

    fun getAllProducts(offset: Int, pageSize: Int): Flow<List<ProductEntity>> {
        return productDao.getAllProducts(offset, pageSize)
    }

    fun getProductsByCategory(category: String, offset: Int, pageSize: Int): Flow<List<ProductEntity>> {
        return productDao.getProductsByCategory(category, offset, pageSize)
    }

    fun searchProducts(query: String, offset: Int, pageSize: Int): Flow<List<ProductEntity>> {
        return productDao.searchProducts(query, offset, pageSize)
    }

    fun getFeaturedProducts(limit: Int = 10): Flow<List<ProductEntity>> {
        return productDao.getFeaturedProducts(limit)
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productDao.deleteProduct(product)
    }

    suspend fun deleteAllProducts() {
        productDao.deleteAllProducts()
    }

    suspend fun addToFavorites(productId: String) {
        productDao.addToFavorites(FavoriteProductEntity(productId))
    }

    suspend fun removeFromFavorites(productId: String) {
        productDao.removeFromFavorites(productId)
    }

    fun getFavoriteProducts(): Flow<List<ProductEntity>> {
        return productDao.getFavoriteProducts()
    }

    fun isFavorite(productId: String): Flow<Boolean> {
        return productDao.isFavorite(productId)
    }

    suspend fun insertReview(review: ProductReviewEntity) {
        productDao.insertReview(review)
    }

    fun getProductReviews(productId: String): Flow<List<ProductReviewEntity>> {
        return productDao.getProductReviews(productId)
    }
}
