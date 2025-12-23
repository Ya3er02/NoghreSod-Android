package com.noghre.sod.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for Category local caching.
 */
@Entity(
    tableName = "categories",
    indices = [
        Index("parent_id"),
        Index("cached_at")
    ]
)
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "name_en")
    val nameEn: String?,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "icon_url")
    val iconUrl: String?,

    @ColumnInfo(name = "parent_id")
    val parentId: String?,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean,

    @ColumnInfo(name = "display_order")
    val displayOrder: Int,

    @ColumnInfo(name = "product_count")
    val productCount: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: String?,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String?,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)
