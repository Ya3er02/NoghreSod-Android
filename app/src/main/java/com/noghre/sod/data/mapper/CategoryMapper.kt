package com.noghre.sod.data.mapper

import com.noghre.sod.data.dto.CategoryDto
import com.noghre.sod.data.local.entity.CategoryEntity

class CategoryMapper {
    fun toEntity(dto: CategoryDto): CategoryEntity {
        return CategoryEntity(
            id = dto.id,
            name = dto.name,
            nameEn = dto.nameEn,
            description = dto.description,
            image = dto.image,
            isActive = dto.isActive,
            sortOrder = dto.sortOrder
        )
    }

    fun toDto(entity: CategoryEntity): CategoryDto {
        return CategoryDto(
            id = entity.id,
            name = entity.name,
            nameEn = entity.nameEn,
            description = entity.description,
            image = entity.image,
            isActive = entity.isActive,
            sortOrder = entity.sortOrder
        )
    }

    fun toEntities(dtos: List<CategoryDto>): List<CategoryEntity> = dtos.map { toEntity(it) }
    fun toDtos(entities: List<CategoryEntity>): List<CategoryDto> = entities.map { toDto(it) }
}