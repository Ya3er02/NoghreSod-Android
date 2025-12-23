package com.noghre.sod.data.mapper

import com.noghre.sod.data.dto.CategoryDto
import com.noghre.sod.data.local.entity.CategoryEntity
import com.noghre.sod.domain.model.Category

/**
 * Mapper for Category conversions between layers.
 */
object CategoryMapper {

    /**
     * Convert CategoryDto to Category (domain).
     */
    fun CategoryDto.toDomain(): Category {
        return Category(
            id = id,
            name = name,
            nameEn = nameEn,
            description = description,
            iconUrl = iconUrl,
            parentId = parentId,
            isActive = isActive,
            displayOrder = displayOrder,
            productCount = productCount
        )
    }

    /**
     * Convert Category (domain) to CategoryEntity.
     */
    fun Category.toEntity(): CategoryEntity {
        return CategoryEntity(
            id = id,
            name = name,
            nameEn = nameEn,
            description = description,
            iconUrl = iconUrl,
            parentId = parentId,
            isActive = isActive,
            displayOrder = displayOrder,
            productCount = productCount
        )
    }

    /**
     * Convert CategoryEntity to Category (domain).
     */
    fun CategoryEntity.toDomain(): Category {
        return Category(
            id = id,
            name = name,
            nameEn = nameEn,
            description = description,
            iconUrl = iconUrl,
            parentId = parentId,
            isActive = isActive,
            displayOrder = displayOrder,
            productCount = productCount
        )
    }
}
