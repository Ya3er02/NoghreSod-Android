package com.noghre.sod.data.mapper

import com.noghre.sod.data.dto.ProductDto
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.model.Product

/**
 * Mapper functions for converting between Product representations:
 * - ProductDto (API response)
 * - ProductEntity (database)
 * - Product (domain model)
 */

/**
 * Converts ProductDto (API response) to ProductEntity (database).
 * Joins images list into comma-separated string for storage.
 */
fun ProductDto.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    nameEn = nameEn,
    description = description,
    price = price,
    images = images.joinToString(","),
    categoryId = categoryId,
    weight = weight,
    purity = purity,
    stock = stock,
    createdAt = createdAt,
    updatedAt = updatedAt,
    cachedAt = System.currentTimeMillis()
)

/**
 * Converts ProductEntity (database) to ProductDto (API format).
 * Splits comma-separated images string back into list.
 */
fun ProductEntity.toDto(): ProductDto = ProductDto(
    id = id,
    name = name,
    nameEn = nameEn,
    description = description,
    price = price,
    images = images.split(",").filter { it.isNotEmpty() },
    categoryId = categoryId,
    weight = weight,
    purity = purity,
    stock = stock,
    createdAt = createdAt,
    updatedAt = updatedAt
)

/**
 * Converts ProductEntity (database) to Product (domain model).
 * Does not include category object - category must be fetched separately.
 */
fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    nameEn = nameEn,
    description = description,
    price = price,
    images = images.split(",").filter { it.isNotEmpty() },
    category = null, // Category must be joined separately
    weight = weight,
    purity = purity,
    stock = stock,
    createdAt = createdAt,
    updatedAt = updatedAt
)

/**
 * Converts ProductEntity (database) to Product (domain model) with category.
 */
fun ProductEntity.toDomain(category: Category?): Product = Product(
    id = id,
    name = name,
    nameEn = nameEn,
    description = description,
    price = price,
    images = images.split(",").filter { it.isNotEmpty() },
    category = category,
    weight = weight,
    purity = purity,
    stock = stock,
    createdAt = createdAt,
    updatedAt = updatedAt
)

/**
 * Converts ProductDto (API response) to Product (domain model).
 * Does not include category object.
 */
fun ProductDto.toDomain(): Product = Product(
    id = id,
    name = name,
    nameEn = nameEn,
    description = description,
    price = price,
    images = images,
    category = null,
    weight = weight,
    purity = purity,
    stock = stock,
    createdAt = createdAt,
    updatedAt = updatedAt
)

/**
 * Converts a list of ProductEntity to a list of Product (domain model).
 */
fun List<ProductEntity>.toDomain(): List<Product> = map { it.toDomain() }

/**
 * Converts a list of ProductDto to a list of Product (domain model).
 */
fun List<ProductDto>.toDomain(): List<Product> = map { it.toDomain() }
