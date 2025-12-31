package com.noghre.sod.data.repository.wishlist

import com.noghre.sod.core.result.Result
import com.noghre.sod.data.database.dao.WishlistDao
import com.noghre.sod.data.error.ExceptionHandler
import com.noghre.sod.data.mapper.WishlistMapper.toDomain
import com.noghre.sod.data.mapper.WishlistMapper.toDomainList
import com.noghre.sod.data.mapper.WishlistMapper.toEntity
import com.noghre.sod.data.network.NoghreSodApi
import com.noghre.sod.data.repository.networkBoundResource
import com.noghre.sod.domain.model.WishlistItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

/**
 * Wishlist Repository Implementation.
 *
 * Manages wishlist with:
 * - Local caching of wishlist items
 * - Price drop notifications
 * - Network sync
 * - Real-time price tracking
 *
 * @param api Retrofit API client
 * @param wishlistDao Database access object for wishlist
 * @param ioDispatcher Dispatcher for I/O operations
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
class WishlistRepositoryImpl @Inject constructor(
    private val api: NoghreSodApi,
    private val wishlistDao: WishlistDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IWishlistRepository {

    override fun getWishlist(): Flow<Result<List<WishlistItem>>> = networkBoundResource(
        query = {
            wishlistDao.getAllWishlistItems().map { it.toDomain() }
        },
        fetch = {
            api.getWishlist()
        },
        saveFetchResult = { response ->
            val items = response.toDomainList()
            wishlistDao.insertAll(items.map { it.toEntity() })
            Timber.d("Wishlist cached: ${items.size} items")
        },
        shouldFetch = { localData ->
            localData.isEmpty() || isCacheStale()
        },
        onFetchFailed = { exception ->
            Timber.e(exception, "Failed to fetch wishlist")
        }
    ).flowOn(ioDispatcher)

    override suspend fun isInWishlist(productId: String): Result<Boolean> = try {
        val item = wishlistDao.getWishlistItem(productId)
        Result.success(item != null)
    } catch (e: Exception) {
        Timber.e(e, "Error checking if product in wishlist")
        Result.failure(e)
    }

    override suspend fun addToWishlist(productId: String): Result<WishlistItem> = try {
        val requestBody = mapOf("productId" to productId)
        val response = api.addToWishlist(requestBody)

        if (response.isSuccessful && response.body()?.success == true) {
            val itemDto = response.body()?.data
            if (itemDto != null) {
                val item = itemDto.toDomain()
                wishlistDao.insert(item.toEntity())
                Timber.d("Added to wishlist: $productId")
                Result.success(item)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            Result.failure(Exception("Failed to add to wishlist: ${response.code()}"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "addToWishlist")
        Timber.e(e, "Error adding to wishlist")
        Result.failure(e)
    }

    override suspend fun removeFromWishlist(productId: String): Result<Unit> = try {
        val response = api.removeFromWishlist(productId)

        if (response.isSuccessful && response.body()?.success == true) {
            wishlistDao.deleteByProductId(productId)
            Timber.d("Removed from wishlist: $productId")
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to remove from wishlist"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "removeFromWishlist")
        Timber.e(e, "Error removing from wishlist")
        Result.failure(e)
    }

    override suspend fun enablePriceDropNotification(
        productId: String,
        targetPrice: Double
    ): Result<WishlistItem> = try {
        if (targetPrice <= 0) {
            return Result.failure(Exception("Invalid target price"))
        }

        val requestBody = mapOf(
            "productId" to productId,
            "targetPrice" to targetPrice
        )
        val response = api.enablePriceNotification(requestBody)

        if (response.isSuccessful && response.body()?.success == true) {
            val itemDto = response.body()?.data
            if (itemDto != null) {
                val item = itemDto.toDomain()
                wishlistDao.insert(item.toEntity())
                Timber.d("Price notification enabled: $productId - $targetPrice")
                Result.success(item)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            Result.failure(Exception("Failed to enable notification"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "enablePriceDropNotification")
        Timber.e(e, "Error enabling price notification")
        Result.failure(e)
    }

    override suspend fun disablePriceDropNotification(productId: String): Result<WishlistItem> = try {
        val requestBody = mapOf("productId" to productId)
        val response = api.disablePriceNotification(requestBody)

        if (response.isSuccessful && response.body()?.success == true) {
            val itemDto = response.body()?.data
            if (itemDto != null) {
                val item = itemDto.toDomain()
                wishlistDao.insert(item.toEntity())
                Timber.d("Price notification disabled: $productId")
                Result.success(item)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            Result.failure(Exception("Failed to disable notification"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "disablePriceDropNotification")
        Timber.e(e, "Error disabling price notification")
        Result.failure(e)
    }

    private suspend fun isCacheStale(): Boolean {
        return try {
            val items = wishlistDao.getAllWishlistItems()
            items.isEmpty() || (System.currentTimeMillis() - (items.firstOrNull()?.lastUpdated ?: 0)) > 60 * 60 * 1000  // 1 hour
        } catch (e: Exception) {
            Timber.e(e, "Error checking cache staleness")
            true
        }
    }
}
