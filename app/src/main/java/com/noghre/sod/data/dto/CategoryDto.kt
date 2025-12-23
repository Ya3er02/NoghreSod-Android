package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Category API responses
 *
 * @property id Unique category identifier
 * @property name Persian category name
 * @property nameEn English category name
 * @property description Category description
 * @property image Category thumbnail image URL
 * @property isActive Whether category is active/visible
 */
data class CategoryDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("nameEn")
    val nameEn: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("isActive")
    val isActive: Boolean = true,
    @SerializedName("sortOrder")
    val sortOrder: Int = 0
)