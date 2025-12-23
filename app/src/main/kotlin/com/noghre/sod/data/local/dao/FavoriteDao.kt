package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.noghre.sod.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Favorites management.
 */
@Dao
interface FavoriteDao {

    /**
     * Add product to favorites.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(favorite: FavoriteEntity)

    /**
     * Remove product from favorites.
     */
    @Delete
    suspend fun removeFromFavorites(favorite: FavoriteEntity)

    /**
     * Get all favorite product IDs.
     */
    @Query("SELECT product_id FROM favorites ORDER BY added_at DESC")
    fun getAllFavoriteIds(): Flow<List<String>>

    /**
     * Check if product is favorite.
     */
    @Query("SELECT COUNT(*) FROM favorites WHERE product_id = :productId")
    suspend fun isFavorite(productId: String): Int

    /**
     * Get favorite by product ID.
     */
    @Query("SELECT * FROM favorites WHERE product_id = :productId")
    suspend fun getFavoriteByProductId(productId: String): FavoriteEntity?

    /**
     * Clear all favorites.
     */
    @Query("DELETE FROM favorites")
    suspend fun clearAll()

    /**
     * Get count of favorites.
     */
    @Query("SELECT COUNT(*) FROM favorites")
    fun getFavoritesCount(): Flow<Int>
}
