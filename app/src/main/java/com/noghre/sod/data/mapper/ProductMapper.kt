package com.noghre.sod.data.mapper

import com.google.gson.Gson
import com.noghre.sod.data.dto.ProductDto
import com.noghre.sod.data.local.entity.ProductEntity

/**
 * Mapper for converting between ProductDto and ProductEntity
 */
class ProductMapper {
    fun toEntity(dto: ProductDto): ProductEntity {
        return ProductEntity(
            id = dto.id,
            name = dto.name,
            nameEn = dto.nameEn,
            description = dto.description,
            price = dto.price,
            originalPrice = dto.originalPrice,
            images = Gson().toJson(dto.images),
            categoryId = dto.categoryId,
            stock = dto.stock,
            rating = dto.rating,
            discount = dto.discount,
            material = dto.material,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    fun toDto(entity: ProductEntity): ProductDto {
        return ProductDto(
            id = entity.id,
            name = entity.name,
            nameEn = entity.nameEn,
            description = entity.description,
            price = entity.price,
            originalPrice = entity.originalPrice,
            images = Gson().fromJson(entity.images, Array<String>::class.java).toList(),
            categoryId = entity.categoryId,
            stock = entity.stock,
            rating = entity.rating,
            discount = entity.discount,
            material = entity.material,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntities(dtos: List<ProductDto>): List<ProductEntity> = dtos.map { toEntity(it) }
    fun toDtos(entities: List<ProductEntity>): List<ProductDto> = entities.map { toDto(it) }
}