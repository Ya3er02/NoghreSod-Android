package com.noghre.sod.data.repository.wishlist

import com.noghre.sod.core.result.Result
import com.noghre.sod.domain.model.WishlistItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for wishlist operations.
 *
 * Handles:
 * - Adding/removing items from wishlist
 * - Getting wishlist items
 * - Managing price drop notifications
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
interface IWishlistRepository {

    /**
     * Get all wishlist items for current user.
     *
     * @return Flow emitting Result with list of WishlistItems
     */
    fun getWishlist(): Flow<Result<List<WishlistItem>>>

    /**
     * Check if product is in wishlist.
     *
     * @param productId Product ID to check
     * @return Result with Boolean (true if in wishlist)
     */
    suspend fun isInWishlist(productId: String): Result<Boolean>

    /**
     * Add product to wishlist.
     *
     * @param productId Product ID to add
     * @return Result with WishlistItem
     */
    suspend fun addToWishlist(productId: String): Result<WishlistItem>

    /**
     * Remove product from wishlist.
     *
     * @param productId Product ID to remove
     * @return Result<Unit> indicating success or failure
     */
    suspend fun removeFromWishlist(productId: String): Result<Unit>

    /**
     * Enable price drop notification for wishlist item.
     *
     * @param productId Product ID to enable notification for
     * @param targetPrice Target price for notification
     * @return Result with updated WishlistItem
     */
    suspend fun enablePriceDropNotification(
        productId: String,
        targetPrice: Double
    ): Result<WishlistItem>

    /**
     * Disable price drop notification for wishlist item.
     *
     * @param productId Product ID to disable notification for
     * @return Result with updated WishlistItem
     */
    suspend fun disablePriceDropNotification(productId: String): Result<WishlistItem>
}
