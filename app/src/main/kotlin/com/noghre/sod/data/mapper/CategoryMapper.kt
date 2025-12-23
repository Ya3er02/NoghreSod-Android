package com.noghre.sod.data.mapper

import com.noghre.sod.data.dto.CategoryDto
import com.noghre.sod.data.local.entity.CategoryEntity
import com.noghre.sod.domain.model.Category

/**
 * Mapper functions for converting between Category representations:
 * - CategoryDto (API response)
 * - CategoryEntity (database)
 * - Category (domain model)
 */

/**
 * Converts CategoryDto (API response) to CategoryEntity (database).
 */
fun CategoryDto.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    nameEn = nameEn,
    slug = slug,
    icon = icon,
    cachedAt = System.currentTimeMillis()
)

/**
 * Converts CategoryEntity (database) to CategoryDto (API format).
 */
fun CategoryEntity.toDto(): CategoryDto = CategoryDto(
    id = id,
    name = name,
    nameEn = nameEn,
    slug = slug,
    icon = icon
)

/**
 * Converts CategoryEntity (database) to Category (domain model).
 */
fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    nameEn = nameEn,
    slug = slug,
    icon = icon
)

/**
 * Converts CategoryDto (API response) to Category (domain model).
 */
fun CategoryDto.toDomain(): Category = Category(
    id = id,
    name = name,
    nameEn = nameEn,
    slug = slug,
    icon = icon
)

/**
 * Converts a list of CategoryEntity to a list of Category (domain model).
 */
fun List<CategoryEntity>.toDomain(): List<Category> = map { it.toDomain() }

/**
 * Converts a list of CategoryDto to a list of Category (domain model).
 */
fun List<CategoryDto>.toDomain(): List<Category> = map { it.toDomain() }
