package com.noghre.sod.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for User Favorites.
 */
@Entity(
    tableName = "favorites",
    indices = [
        Index("user_id"),
        Index("added_at")
    ]
)
data class FavoriteEntity(
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    val productId: String,

    @ColumnInfo(name = "user_id")
    val userId: String?,

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis()
)
