package com.noghre.sod.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for Search History.
 */
@Entity(
    tableName = "search_history",
    indices = [
        Index("searched_at")
    ]
)
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "query")
    val query: String,

    @ColumnInfo(name = "searched_at")
    val searchedAt: Long = System.currentTimeMillis()
)
