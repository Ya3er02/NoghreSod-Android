package com.noghre.sod.data.mapper

import com.noghre.sod.data.database.entity.WishlistEntity
import com.noghre.sod.data.network.dto.WishlistItemDto
import com.noghre.sod.domain.model.WishlistItem

/**
 * Mapper for Wishlist DTOs to Domain models.
 *
 * @author NoghreSod Team
 */
object WishlistMapper {

    fun WishlistItemDto.toDomain(): WishlistItem = WishlistItem(
        id = id ?: "",
        productId = productId ?: "",
        productName = productName ?: "",
        image = image,
        price = price ?: 0.0,
        currentPrice = currentPrice ?: price ?: 0.0,
        priceDropNotificationEnabled = priceDropNotificationEnabled ?: false,
        targetPrice = targetPrice,
        addedAt = addedAt ?: System.currentTimeMillis(),
        lastUpdated = System.currentTimeMillis()
    )

    fun WishlistEntity.toDomain(): WishlistItem = WishlistItem(
        id = id,
        productId = productId,
        productName = productName,
        image = image,
        price = price,
        currentPrice = currentPrice,
        priceDropNotificationEnabled = priceDropNotificationEnabled,
        targetPrice = targetPrice,
        addedAt = addedAt,
        lastUpdated = lastUpdated
    )

    fun WishlistItem.toEntity(): WishlistEntity = WishlistEntity(
        id = id,
        productId = productId,
        productName = productName,
        image = image,
        price = price,
        currentPrice = currentPrice,
        priceDropNotificationEnabled = priceDropNotificationEnabled,
        targetPrice = targetPrice,
        addedAt = addedAt,
        lastUpdated = lastUpdated
    )

    fun List<WishlistItemDto>.toDomainList(): List<WishlistItem> =
        this.map { it.toDomain() }

    fun List<WishlistEntity>.toDomainListFromEntity(): List<WishlistItem> =
        this.map { it.toDomain() }
}
