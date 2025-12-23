package com.noghre.sod.data.local.dao

import androidx.room.*
import com.noghre.sod.data.local.entity.FavoriteProductEntity
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.local.entity.ProductReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // ==================== Product Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProduct(productId: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products ORDER BY createdAt DESC LIMIT :pageSize OFFSET :offset")
    fun getAllProducts(offset: Int, pageSize: Int): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY createdAt DESC LIMIT :pageSize OFFSET :offset")
    fun getProductsByCategory(category: String, offset: Int, pageSize: Int): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE name LIKE :query OR nameInFarsi LIKE :query LIMIT :pageSize OFFSET :offset")
    fun searchProducts(query: String, offset: Int, pageSize: Int): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE rating >= 4.0 ORDER BY rating DESC LIMIT :limit")
    fun getFeaturedProducts(limit: Int = 10): Flow<List<ProductEntity>>

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    // ==================== Favorite Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(favorite: FavoriteProductEntity)

    @Query("DELETE FROM favorite_products WHERE productId = :productId")
    suspend fun removeFromFavorites(productId: String)

    @Query("SELECT * FROM products WHERE id IN (SELECT productId FROM favorite_products ORDER BY addedAt DESC)")
    fun getFavoriteProducts(): Flow<List<ProductEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE productId = :productId)")
    fun isFavorite(productId: String): Flow<Boolean>

    // ==================== Review Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ProductReviewEntity)

    @Query("SELECT * FROM product_reviews WHERE productId = :productId ORDER BY createdAt DESC")
    fun getProductReviews(productId: String): Flow<List<ProductReviewEntity>>

    @Delete
    suspend fun deleteReview(review: ProductReviewEntity)
}
