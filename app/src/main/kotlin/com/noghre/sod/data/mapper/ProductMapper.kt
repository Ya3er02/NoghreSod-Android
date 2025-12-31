package com.noghre.sod.data.mapper

import com.noghre.sod.data.database.entity.ProductEntity
import com.noghre.sod.data.network.dto.ProductDto
import com.noghre.sod.domain.model.Product

/**
 * Mapper for Product DTOs to Domain models.
 *
 * Handles conversion:
 * - ProductDto (API response) → Product (domain model)
 * - ProductEntity (Database) → Product (domain model)
 * - Product (domain) → ProductEntity (database)
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
object ProductMapper {

    /**
     * Convert ProductDto to Product domain model.
     */
    fun ProductDto.toDomain(): Product = Product(
        id = id ?: "",
        name = name ?: "",
        description = description ?: "",
        price = price ?: 0.0,
        currentPrice = currentPrice ?: price ?: 0.0,
        discount = discount ?: 0,
        images = images ?: emptyList(),
        rating = rating ?: 0.0,
        reviewCount = reviewCount ?: 0,
        weight = weight ?: 0.0,
        gemType = gemType ?: "",
        hallmark = hallmark ?: "925",
        categoryId = categoryId ?: "",
        categoryName = categoryName ?: "",
        isFeatured = isFeatured ?: false,
        onSale = onSale ?: false,
        inStock = inStock ?: true,
        stockCount = stockCount ?: 0,
        variants = variants ?: emptyList(),
        specifications = specifications ?: emptyMap(),
        lastUpdated = System.currentTimeMillis()
    )

    /**
     * Convert ProductEntity to Product domain model.
     */
    fun ProductEntity.toDomain(): Product = Product(
        id = id,
        name = name,
        description = description,
        price = price,
        currentPrice = currentPrice,
        discount = discount,
        images = images,
        rating = rating,
        reviewCount = reviewCount,
        weight = weight,
        gemType = gemType,
        hallmark = hallmark,
        categoryId = categoryId,
        categoryName = categoryName,
        isFeatured = isFeatured,
        onSale = onSale,
        inStock = inStock,
        stockCount = stockCount,
        variants = variants,
        specifications = specifications,
        lastUpdated = lastUpdated
    )

    /**
     * Convert Product domain to ProductEntity for database.
     */
    fun Product.toEntity(): ProductEntity = ProductEntity(
        id = id,
        name = name,
        description = description,
        price = price,
        currentPrice = currentPrice,
        discount = discount,
        images = images,
        rating = rating,
        reviewCount = reviewCount,
        weight = weight,
        gemType = gemType,
        hallmark = hallmark,
        categoryId = categoryId,
        categoryName = categoryName,
        isFeatured = isFeatured,
        onSale = onSale,
        inStock = inStock,
        stockCount = stockCount,
        variants = variants,
        specifications = specifications,
        lastUpdated = lastUpdated
    )

    /**
     * Convert list of DTOs to domain models.
     */
    fun List<ProductDto>.toDomainList(): List<Product> =
        this.map { it.toDomain() }

    /**
     * Convert list of entities to domain models.
     */
    fun List<ProductEntity>.toDomainListFromEntity(): List<Product> =
        this.map { it.toDomain() }
}
