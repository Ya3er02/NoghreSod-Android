package com.noghre.sod.data.mapper

import com.noghre.sod.data.database.entity.CartEntity
import com.noghre.sod.data.network.dto.CartDto
import com.noghre.sod.domain.model.Cart

/**
 * Mapper for Cart DTOs to Domain models.
 *
 * @author NoghreSod Team
 */
object CartMapper {

    fun CartDto.toDomain(): Cart = Cart(
        id = id ?: "",
        userId = userId ?: "",
        items = items?.map { cartItemDto ->
            cartItemDto.let {
                com.noghre.sod.domain.model.CartItem(
                    id = it.id ?: "",
                    productId = it.productId ?: "",
                    productName = it.productName ?: "",
                    price = it.price ?: 0.0,
                    quantity = it.quantity ?: 1,
                    selectedColor = it.selectedColor,
                    selectedSize = it.selectedSize,
                    image = it.image
                )
            }
        } ?: emptyList(),
        totalPrice = totalPrice ?: 0.0,
        itemCount = itemCount ?: 0,
        lastUpdated = System.currentTimeMillis()
    )

    fun CartEntity.toDomain(): Cart = Cart(
        id = id,
        userId = userId,
        items = items,
        totalPrice = totalPrice,
        itemCount = itemCount,
        lastUpdated = lastUpdated
    )

    fun Cart.toEntity(): CartEntity = CartEntity(
        id = id,
        userId = userId,
        items = items,
        totalPrice = totalPrice,
        itemCount = itemCount,
        lastUpdated = lastUpdated
    )
}
