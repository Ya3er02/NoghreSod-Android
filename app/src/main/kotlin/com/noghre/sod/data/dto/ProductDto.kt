package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Product API responses.
 * Represents the network response model for Product data.
 */
data class ProductDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("name_en")
    val nameEn: String? = null,

    @SerializedName("description")
    val description: String,

    @SerializedName("description_en")
    val descriptionEn: String? = null,

    @SerializedName("price")
    val price: Double,

    @SerializedName("discount_price")
    val discountPrice: Double? = null,

    @SerializedName("images")
    val images: List<String> = emptyList(),

    @SerializedName("category_id")
    val categoryId: String,

    @SerializedName("stock")
    val stock: Int,

    @SerializedName("rating")
    val rating: Float = 0f,

    @SerializedName("review_count")
    val reviewCount: Int = 0,

    @SerializedName("weight")
    val weight: Double? = null,

    @SerializedName("material")
    val material: String,

    @SerializedName("specifications")
    val specifications: Map<String, String>? = null,

    @SerializedName("seller_id")
    val sellerId: String,

    @SerializedName("seller_name")
    val sellerName: String? = null,

    @SerializedName("seller_rating")
    val sellerRating: Float? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
)
