package com.noghre.sod.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
from kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val id: String,
    val name: String,
    val description: String? = null,
    val iconUrl: String? = null,
    val parentCategoryId: String? = null,
    val productCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
