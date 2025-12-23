package com.noghre.sod.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.noghre.sod.data.model.ProductEntity

/**
 * Product Data Transfer Object (DTO)
 * Represents product data structure from API responses
 */
@Serializable
data class ProductDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("name_fa")
    val nameFA: String,
    @SerialName("description")
    val description: String,
    @SerialName("description_fa")
    val descriptionFA: String? = null,
    @SerialName("price")
    val price: Long, // Price in Toman
    @SerialName("purity")
    val purity: String, // Silver purity: 925, 950, 999
    @SerialName("weight")
    val weight: Double, // Weight in grams
    @SerialName("labor_cost")
    val laborCost: Long = 0, // Labor cost in Toman
    @SerialName("category_id")
    val categoryId: String,
    @SerialName("category_name")
    val categoryName: String,
    @SerialName("stock")
    val stock: Int,
    @SerialName("images")
    val images: List<String> = emptyList(),
    @SerialName("is_favorite")
    val isFavorite: Boolean = false,
    @SerialName("rating")
    val rating: Float = 0f,
    @SerialName("review_count")
    val reviewCount: Int = 0,
    @SerialName("discount")
    val discount: Int = 0, // Discount percentage
    @SerialName("is_new")
    val isNew: Boolean = false,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long
) {
    fun toEntity(): ProductEntity = ProductEntity(
        id = id,
        name = name,
        nameFA = nameFA,
        description = description,
        descriptionFA = descriptionFA,
        price = price,
        purity = purity,
        weight = weight,
        laborCost = laborCost,
        categoryId = categoryId,
        categoryName = categoryName,
        stock = stock,
        images = images,
        isFavorite = isFavorite,
        rating = rating,
        reviewCount = reviewCount,
        discount = discount,
        isNew = isNew,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastSyncedAt = System.currentTimeMillis()
    )
}

/**
 * API Response wrapper for product list
 */
@Serializable
data class ProductListResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: List<ProductDto> = emptyList(),
    @SerialName("message")
    val message: String? = null,
    @SerialName("pagination")
    val pagination: PaginationDto? = null
)

/**
 * Pagination information from API
 */
@Serializable
data class PaginationDto(
    @SerialName("page")
    val page: Int,
    @SerialName("limit")
    val limit: Int,
    @SerialName("total")
    val total: Int,
    @SerialName("pages")
    val pages: Int
)
