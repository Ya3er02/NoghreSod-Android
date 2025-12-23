package com.noghre.sod.data.repository

import com.noghre.sod.data.local.dao.FavoriteDao
import com.noghre.sod.data.local.entity.FavoriteEntity
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.exception.ApiException
import com.noghre.sod.data.remote.network.NetworkMonitor
import com.noghre.sod.domain.Result
import timber.log.Timber
import javax.inject.Inject

/**
 * Favorite Repository Implementation with local and remote sync.
 */
class FavoriteRepositoryImpl @Inject constructor(
    private val api: NoghreSodApiService,
    private val favoriteDao: FavoriteDao,
    private val networkMonitor: NetworkMonitor
) {

    /**
     * Get all favorite products.
     */
    suspend fun getFavorites(): Result<Unit> = try {
        if (!networkMonitor.isNetworkAvailable()) {
            return Result.Success(Unit) // Return local favorites
        }

        val response = api.getFavorites()
        if (response.success) {
            Result.Success(Unit)
        } else {
            Result.Error(response.message ?: "Failed to fetch favorites")
        }
    } catch (e: ApiException) {
        Timber.e(e, "API error fetching favorites")
        Result.Error(e.message ?: "Network error")
    } catch (e: Exception) {
        Timber.e(e, "Error fetching favorites")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Add product to favorites.
     */
    suspend fun addToFavorites(productId: String): Result<Unit> = try {
        // Add to local favorites first
        favoriteDao.addToFavorites(
            FavoriteEntity(productId = productId)
        )

        // Sync with server if online
        if (networkMonitor.isNetworkAvailable()) {
            try {
                val response = api.addToFavorites(productId)
                if (response.success) {
                    Result.Success(Unit)
                } else {
                    Result.Error("Failed to add to favorites")
                }
            } catch (e: ApiException) {
                Timber.e(e, "API error adding to favorites")
                Result.Error(e.message ?: "Network error")
            }
        } else {
            Result.Success(Unit)
        }
    } catch (e: Exception) {
        Timber.e(e, "Error adding to favorites")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Remove product from favorites.
     */
    suspend fun removeFromFavorites(productId: String): Result<Unit> = try {
        val favorite = favoriteDao.getFavoriteByProductId(productId)
        if (favorite != null) {
            favoriteDao.removeFromFavorites(favorite)
        }

        if (networkMonitor.isNetworkAvailable()) {
            try {
                val response = api.removeFromFavorites(productId)
                if (response.success) {
                    Result.Success(Unit)
                } else {
                    Result.Error("Failed to remove from favorites")
                }
            } catch (e: ApiException) {
                Timber.e(e, "API error removing from favorites")
                Result.Error(e.message ?: "Network error")
            }
        } else {
            Result.Success(Unit)
        }
    } catch (e: Exception) {
        Timber.e(e, "Error removing from favorites")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Check if product is favorite.
     */
    suspend fun isFavorite(productId: String): Boolean = try {
        favoriteDao.isFavorite(productId) > 0
    } catch (e: Exception) {
        Timber.e(e, "Error checking favorite")
        false
    }
}
