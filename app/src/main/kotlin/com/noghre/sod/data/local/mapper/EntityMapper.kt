package com.noghre.sod.data.local.mapper

import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.data.remote.dto.ProductDto
import com.noghre.sod.domain.model.Product
import timber.log.Timber

/**
 * Mappers for converting between DTOs, Entities, and Domain Models.
 */

// ============== Product Mappers ==============

/**
 * Convert ProductDto (API response) to Product (Domain model).
 */
fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        price = price,
        discountPercentage = discountPercentage ?: 0.0,
        description = description,
        image = image,
        category = category,
        rating = rating ?: 0.0,
        inStock = inStock ?: true,
        createdAt = createdAt
    ).also {
        Timber.d("Mapped ProductDto to Domain: ${it.name}")
    }
}

/**
 * Convert list of ProductDtos to Domain models.
 */
fun List<ProductDto>.toDomain(): List<Product> {
    return map { it.toDomain() }
}

/**
 * Convert Product (Domain model) to ProductEntity (Database model).
 */
fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        price = price,
        discountPercentage = discountPercentage,
        description = description,
        image = image,
        category = category,
        rating = rating,
        inStock = inStock,
        createdAt = createdAt,
        lastUpdated = System.currentTimeMillis()
    ).also {
        Timber.d("Mapped Product to Entity: ${it.name}")
    }
}

/**
 * Convert ProductEntity (Database model) to Product (Domain model).
 */
fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        price = price,
        discountPercentage = discountPercentage,
        description = description,
        image = image,
        category = category,
        rating = rating,
        inStock = inStock,
        createdAt = createdAt
    ).also {
        Timber.d("Mapped ProductEntity to Domain: ${it.name}")
    }
}

/**
 * Convert list of ProductEntities to Domain models.
 */
fun List<ProductEntity>.toDomain(): List<Product> {
    return map { it.toDomain() }
}

/**
 * Convert ProductDto (API response) to ProductEntity (Database model).
 */
fun ProductDto.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        price = price,
        discountPercentage = discountPercentage ?: 0.0,
        description = description,
        image = image,
        category = category,
        rating = rating ?: 0.0,
        inStock = inStock ?: true,
        createdAt = createdAt,
        lastUpdated = System.currentTimeMillis()
    ).also {
        Timber.d("Mapped ProductDto to Entity: ${it.name}")
    }
}

/**
 * Convert ProductEntity (Database model) to ProductDto (API model).
 */
fun ProductEntity.toDto(): ProductDto {
    return ProductDto(
        id = id,
        name = name,
        price = price,
        discountPercentage = discountPercentage,
        description = description,
        image = image,
        category = category,
        rating = rating,
        inStock = inStock,
        createdAt = createdAt
    ).also {
        Timber.d("Mapped ProductEntity to Dto: ${it.name}")
    }
}
