package com.noghre.sod.data.repository

import com.noghre.sod.core.error.*
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.local.dao.FavoriteDao
import com.noghre.sod.data.local.entity.FavoriteEntity
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.core.network.NetworkMonitor
import timber.log.Timber
import javax.inject.Inject

/**
 * ❤️ Favorite Repository Implementation
 * 
 * Manages favorite products with local/remote sync and error handling.
 * Uses offline-first strategy with AppError classification.
 */
class FavoriteRepositoryImpl @Inject constructor(
    private val api: NoghreSodApiService,
    private val favoriteDao: FavoriteDao,
    private val networkMonitor: NetworkMonitor,
    private val exceptionHandler: GlobalExceptionHandler
) {

    /**
     * 📚 Get all favorite products
     */
    suspend fun getFavorites(): Result<Unit> {
        return try {
            Timber.d("[FAVORITE] Getting favorites")
            
            if (!networkMonitor.isOnline) {
                Timber.d("[FAVORITE] Offline mode, using local favorites")
                return Result.Success(Unit)
            }

            try {
                val response = api.getFavorites()
                
                if (response.isSuccessful) {
                    Timber.d("[FAVORITE] Favorites fetched successfully")
                    Result.Success(Unit)
                } else {
                    Timber.w("[FAVORITE] Get favorites failed: ${response.code()}")
                    Result.Error(AppError.Network(
                        message = response.message ?: "درافت علاقمندان ناموفق",
                        statusCode = response.code()
                    ))
                }
            } catch (e: java.net.UnknownHostException) {
                Timber.e(e, "[FAVORITE] Network error")
                Result.Error(AppError.Network(
                    message = "بدون دسترسی به اینترنت",
                    statusCode = null
                ))
            } catch (e: java.net.SocketTimeoutException) {
                Timber.e(e, "[FAVORITE] Network timeout")
                Result.Error(AppError.Network(
                    message = "زمان اتصال تمام شد",
                    statusCode = null
                ))
            }
        } catch (e: Exception) {
            Timber.e(e, "[FAVORITE] Get favorites error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * ➕ Add product to favorites
     */
    suspend fun addToFavorites(productId: String): Result<Unit> {
        return try {
            Timber.d("[FAVORITE] Adding to favorites: $productId")
            
            // Validate input
            if (productId.isBlank()) {
                Timber.w("[FAVORITE] Invalid product ID")
                return Result.Error(AppError.Validation(
                    message = "شناسه محصول نامعتبر",
                    field = "productId"
                ))
            }
            
            // Add to local favorites first
            try {
                favoriteDao.addToFavorites(FavoriteEntity(productId = productId))
                Timber.d("[FAVORITE] Added to local favorites")
            } catch (e: Exception) {
                Timber.e(e, "[FAVORITE] Failed to add to local database")
                return Result.Error(AppError.Database(
                    message = "افزودن به علاقمندان ناموفق",
                    operation = "addToFavorites"
                ))
            }

            // Sync with server if online
            if (networkMonitor.isOnline) {
                try {
                    val response = api.addToFavorites(productId)
                    
                    if (response.isSuccessful) {
                        Timber.d("[FAVORITE] Synced with server")
                        Result.Success(Unit)
                    } else {
                        Timber.w("[FAVORITE] Server sync failed: ${response.code()}")
                        // Keep local copy
                        Result.Success(Unit)
                    }
                } catch (e: Exception) {
                    Timber.w(e, "[FAVORITE] Server sync error, keeping local copy")
                    // Keep local copy
                    Result.Success(Unit)
                }
            } else {
                Timber.d("[FAVORITE] Offline mode, saved locally")
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Timber.e(e, "[FAVORITE] Add to favorites error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * ➖ Remove product from favorites
     */
    suspend fun removeFromFavorites(productId: String): Result<Unit> {
        return try {
            Timber.d("[FAVORITE] Removing from favorites: $productId")
            
            // Validate input
            if (productId.isBlank()) {
                Timber.w("[FAVORITE] Invalid product ID for remove")
                return Result.Error(AppError.Validation(
                    message = "شناسه محصول نامعتبر",
                    field = "productId"
                ))
            }
            
            // Remove from local favorites first
            try {
                val favorite = favoriteDao.getFavoriteByProductId(productId)
                if (favorite != null) {
                    favoriteDao.removeFromFavorites(favorite)
                    Timber.d("[FAVORITE] Removed from local favorites")
                }
            } catch (e: Exception) {
                Timber.e(e, "[FAVORITE] Failed to remove from local database")
                return Result.Error(AppError.Database(
                    message = "حذف از علاقمندان ناموفق",
                    operation = "removeFromFavorites"
                ))
            }

            // Sync with server if online
            if (networkMonitor.isOnline) {
                try {
                    val response = api.removeFromFavorites(productId)
                    
                    if (response.isSuccessful) {
                        Timber.d("[FAVORITE] Synced removal with server")
                        Result.Success(Unit)
                    } else {
                        Timber.w("[FAVORITE] Server sync failed: ${response.code()}")
                        // Local removal already done
                        Result.Success(Unit)
                    }
                } catch (e: Exception) {
                    Timber.w(e, "[FAVORITE] Server sync error, local removal kept")
                    // Local removal already done
                    Result.Success(Unit)
                }
            } else {
                Timber.d("[FAVORITE] Offline mode, removed locally")
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Timber.e(e, "[FAVORITE] Remove from favorites error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * ✅ Check if product is favorite
     */
    suspend fun isFavorite(productId: String): Result<Boolean> {
        return try {
            Timber.d("[FAVORITE] Checking if favorite: $productId")
            
            if (productId.isBlank()) {
                Timber.w("[FAVORITE] Invalid product ID for check")
                return Result.Success(false)
            }
            
            val isFavorite = favoriteDao.isFavorite(productId) > 0
            Timber.d("[FAVORITE] Is favorite: $isFavorite")
            Result.Success(isFavorite)
        } catch (e: Exception) {
            Timber.e(e, "[FAVORITE] Error checking favorite status")
            Result.Error(AppError.Database(
                message = "بررسی وضعیت علاقمندی ناموفق",
                operation = "isFavorite"
            ))
        }
    }

    /**
     * 🗑️ Clear all favorites
     */
    suspend fun clearFavorites(): Result<Unit> {
        return try {
            Timber.d("[FAVORITE] Clearing all favorites")
            
            try {
                favoriteDao.clearFavorites()
                Timber.d("[FAVORITE] All favorites cleared")
                Result.Success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "[FAVORITE] Failed to clear favorites")
                Result.Error(AppError.Database(
                    message = "حذف علاقمندان ناموفق",
                    operation = "clearFavorites"
                ))
            }
        } catch (e: Exception) {
            Timber.e(e, "[FAVORITE] Clear favorites error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }
}