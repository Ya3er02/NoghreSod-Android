package com.noghre.sod.data.dto

from kotlinx.serialization.Serializable
import com.noghre.sod.data.model.Product

@Serializable
data class ProductDto(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val category: String,
    val imageUrl: String,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val inStock: Boolean = true,
    val sellerId: String
)

fun ProductDto.toEntity(): Product {
    return Product(
        id = id,
        name = name,
        description = description,
        price = price,
        originalPrice = originalPrice,
        category = category,
        imageUrl = imageUrl,
        rating = rating,
        reviewCount = reviewCount,
        inStock = inStock,
        sellerId = sellerId
    )
}
