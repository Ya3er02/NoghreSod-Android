package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Product API responses
 * Represents a product in the jewelry e-commerce application
 *
 * @property id Unique product identifier
 * @property name Persian name of the product
 * @property nameEn English name of the product
 * @property description Detailed product description
 * @property price Product price in Toman (نتوما)
 * @property originalPrice Original price before discount
 * @property images List of product image URLs
 * @property categoryId Reference to product category
 * @property stock Available inventory count
 * @property rating Average customer rating (0-5)
 * @property discount Discount percentage (0-100)
 * @property material Material composition (e.g., Silver 925)
 */
data class ProductDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("nameEn")
    val nameEn: String? = null,
    @SerializedName("description")
    val description: String,
    @SerializedName("price")
    val price: Long,
    @SerializedName("originalPrice")
    val originalPrice: Long? = null,
    @SerializedName("images")
    val images: List<String> = emptyList(),
    @SerializedName("categoryId")
    val categoryId: String,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("rating")
    val rating: Float = 0f,
    @SerializedName("discount")
    val discount: Int = 0,
    @SerializedName("material")
    val material: String = "Silver 925",
    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),
    @SerializedName("updatedAt")
    val updatedAt: Long = System.currentTimeMillis()
)