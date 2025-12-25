package com.noghre.sod.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Full-Text Search (FTS) entity for fast product searching.
 * FTS4 provides indexed search capabilities for product names, descriptions, and categories.
 */
@Entity(tableName = "products_fts")
@Fts4(contentEntity = com.noghre.sod.data.local.entity.ProductEntity::class)
data class ProductSearchFts(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "rowid")
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "category")
    val category: String
)

/**
 * Enhanced ProductEntity with FTS4 search capabilities and proper indexing.
 * Indexes are added for frequently searched fields.
 */
@Entity(
    tableName = "products_indexed",
    indices = [
        Index(value = ["name"], unique = false),
        Index(value = ["category"], unique = false),
        Index(value = ["price"], unique = false),
        Index(value = ["rating"], unique = false),
        Index(value = ["in_stock"], unique = false),
        Index(value = ["created_at"], unique = false),
        Index(value = ["name", "category"], unique = false) // Composite index
    ]
)
data class ProductEntityIndexed(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "price")
    val price: Double,

    @ColumnInfo(name = "discount_percentage")
    val discountPercentage: Double,

    @ColumnInfo(name = "image")
    val image: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "rating")
    val rating: Double,

    @ColumnInfo(name = "in_stock")
    val inStock: Boolean,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long
)

/**
 * Search result combining FTS ranking with additional product data.
 */
data class ProductSearchResult(
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "price")
    val price: Double,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "rank")
    val rank: Double // FTS rank score
)
