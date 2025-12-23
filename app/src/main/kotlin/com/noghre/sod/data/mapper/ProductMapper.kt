package com.noghre.sod.data.mapper

import com.noghre.sod.data.dto.ProductDto
import com.noghre.sod.data.local.entity.ProductEntity
import com.noghre.sod.domain.model.Product

/**
 * Mapper for Product conversions between layers (DTO, Entity, Domain).
 */
object ProductMapper {

    /**
     * Convert ProductDto (from API) to Product (domain model).
     */
    fun ProductDto.toDomain(): Product {
        return Product(
            id = id,
            name = name,
            nameEn = nameEn,
            description = description,
            descriptionEn = descriptionEn,
            price = price,
            discountPrice = discountPrice,
            images = images,
            category = category,
            stock = stock,
            rating = rating,
            reviewCount = reviewCount,
            weight = weight,
            material = material,
            isFavorite = false,
            specifications = specifications ?: emptyMap()
        )
    }

    /**
     * Convert Product (domain model) to ProductEntity (for local cache).
     */
    fun Product.toEntity(): ProductEntity {
        return ProductEntity(
            id = id,
            name = name,
            nameEn = nameEn,
            description = description,
            descriptionEn = descriptionEn,
            price = price,
            discountPrice = discountPrice,
            images = com.google.gson.Gson().toJson(images),
            categoryId = category,
            stock = stock,
            rating = rating,
            reviewCount = reviewCount,
            weight = weight,
            material = material,
            specifications = com.google.gson.Gson().toJson(specifications),
            sellerId = "unknown",
            sellerName = null,
            sellerRating = null,
            isFavorite = isFavorite
        )
    }

    /**
     * Convert ProductEntity (from cache) to Product (domain model).
     */
    fun ProductEntity.toDomain(): Product {
        val gson = com.google.gson.Gson()
        val images = try {
            gson.fromJson(this.images, List::class.java) as? List<String> ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        val specs = try {
            specifications?.let {
                gson.fromJson(it, Map::class.java) as? Map<String, String> ?: emptyMap()
            } ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }

        return Product(
            id = id,
            name = name,
            nameEn = nameEn,
            description = description,
            descriptionEn = descriptionEn,
            price = price,
            discountPrice = discountPrice,
            images = images,
            category = categoryId,
            stock = stock,
            rating = rating,
            reviewCount = reviewCount,
            weight = weight,
            material = material,
            isFavorite = isFavorite,
            specifications = specs
        )
    }
}
