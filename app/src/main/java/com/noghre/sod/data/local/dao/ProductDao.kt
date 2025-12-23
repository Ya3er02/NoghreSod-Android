package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentProducts(limit: Int = 20): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun getProductsByCategory(categoryId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE stock > 0 ORDER BY rating DESC LIMIT :limit")
    fun getTopRatedProducts(limit: Int = 10): Flow<List<ProductEntity>>

    @Query("DELETE FROM products WHERE cacheExpiry < :currentTime")
    suspend fun deleteExpiredCache(currentTime: Long = System.currentTimeMillis())

    @Query("DELETE FROM products")
    suspend fun clearAll()
}