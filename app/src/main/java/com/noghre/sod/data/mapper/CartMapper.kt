package com.noghre.sod.data.mapper

import com.noghre.sod.data.dto.CartDto
import com.noghre.sod.data.local.entity.CartEntity

class CartMapper {
    fun toEntity(dto: CartDto): CartEntity {
        return CartEntity(
            userId = dto.userId,
            productId = dto.productId,
            quantity = dto.quantity,
            price = dto.price,
            addedAt = dto.addedAt,
            updatedAt = dto.updatedAt
        )
    }

    fun toDto(entity: CartEntity): CartDto {
        return CartDto(
            id = "${entity.userId}_${entity.productId}",
            userId = entity.userId,
            productId = entity.productId,
            quantity = entity.quantity,
            price = entity.price,
            addedAt = entity.addedAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntities(dtos: List<CartDto>): List<CartEntity> = dtos.map { toEntity(it) }
    fun toDtos(entities: List<CartEntity>): List<CartDto> = entities.map { toDto(it) }
}