package com.noghre.sod.data.mapper

import com.noghre.sod.data.database.entity.CartEntity
import com.noghre.sod.data.dto.remote.CartDto
import com.noghre.sod.data.dto.remote.CartItemDto
import com.noghre.sod.domain.model.Cart
import com.noghre.sod.domain.model.CartItem
import kotlinx.serialization.json.Json

/**
 * Mapper for Cart-related data transformations.
 *
 * Converts between:
 * - DTO ↔ Domain
 * - Entity ↔ Domain
 * - Entity ↔ DTO
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
object CartMapper {

    // ============ DTO TO DOMAIN ============

    /**
     * Convert CartDto to Cart domain model.
     */
    fun CartDto.toDomain(): Cart = Cart(
        id = this.id,
        userId = this.userId,
        items = this.items.map { it.toDomain() },
        totalPrice = this.totalPrice,
        itemCount = this.itemCount
    )

    /**
     * Convert CartItemDto to CartItem domain model.
     */
    fun CartItemDto.toDomain(): CartItem = CartItem(
        id = this.id,
        cartId = this.cartId,
        product = this.product.toDomain(),
        quantity = this.quantity,
        selectedColor = this.selectedColor,
        selectedSize = this.selectedSize,
        subtotal = this.subtotal
    )

    // ============ ENTITY TO DOMAIN ============

    /**
     * Convert CartEntity to Cart domain model.
     * Deserializes JSON items string.
     */
    fun CartEntity.toDomain(): Cart = try {
        Cart(
            id = this.id,
            userId = this.userId,
            items = try {
                Json.decodeFromString<List<CartItem>>(this.itemsJson)
            } catch (e: Exception) {
                emptyList()
            },
            totalPrice = this.totalPrice,
            itemCount = this.itemCount
        )
    } catch (e: Exception) {
        Cart(
            id = this.id,
            userId = this.userId,
            items = emptyList(),
            totalPrice = 0.0,
            itemCount = 0
        )
    }

    // ============ DOMAIN TO ENTITY ============

    /**
     * Convert Cart domain model to CartEntity.
     * Serializes items to JSON for storage.
     */
    fun Cart.toEntity(): CartEntity = CartEntity(
        id = this.id,
        userId = this.userId,
        itemsJson = try {
            Json.encodeToString(this.items)
        } catch (e: Exception) {
            "[]"
        },
        totalPrice = this.totalPrice,
        itemCount = this.itemCount,
        lastUpdated = System.currentTimeMillis()
    )

    // ============ DTO TO ENTITY ============

    /**
     * Convert CartDto to CartEntity for caching.
     */
    fun CartDto.toEntity(): CartEntity = CartEntity(
        id = this.id,
        userId = this.userId,
        itemsJson = try {
            Json.encodeToString(this.items.map { it.toDomain() })
        } catch (e: Exception) {
            "[]"
        },
        totalPrice = this.totalPrice,
        itemCount = this.itemCount,
        lastUpdated = System.currentTimeMillis()
    )

    // ============ BATCH CONVERSIONS ============

    /**
     * Convert list of CartDtos to domain models.
     */
    fun List<CartDto>.toDomainList(): List<Cart> = map { it.toDomain() }

    /**
     * Convert list of CartEntities to domain models.
     */
    fun List<CartEntity>.toDomainList(): List<Cart> = map { it.toDomain() }
}
