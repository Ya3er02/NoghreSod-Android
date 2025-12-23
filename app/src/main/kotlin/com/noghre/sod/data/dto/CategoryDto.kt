package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Category API responses.
 */
data class CategoryDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("name_en")
    val nameEn: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("icon_url")
    val iconUrl: String? = null,

    @SerializedName("parent_id")
    val parentId: String? = null,

    @SerializedName("is_active")
    val isActive: Boolean = true,

    @SerializedName("display_order")
    val displayOrder: Int = 0,

    @SerializedName("product_count")
    val productCount: Int = 0,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
)
