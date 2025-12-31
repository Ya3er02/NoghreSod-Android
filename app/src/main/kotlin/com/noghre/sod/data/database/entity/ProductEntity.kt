package com.noghre.sod.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * ProductEntity - Database representation of a Product.
 * 
 * Used for local caching and offline-first support.
 * Synced with remote API and cached locally for performance.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    
    // Basic info
    val name: String,
    val description: String? = null,
    val category: String? = null,
    val subcategory: String? = null,
    
    // Pricing
    val price: Double,
    val originalPrice: Double? = null,
    val currency: String = "IRR",
    
    // Inventory
    val inStock: Boolean = true,
    val quantity: Int = 0,
    val weight: Double? = null, // in grams - important for jewelry
    val sku: String? = null,
    
    // Images
    val imageUrl: String? = null,
    val imageUrls: String? = null, // JSON array stored as string
    val thumbnailUrl: String? = null,
    
    // Specifications (jewelry specific)
    val hallmark: String? = null, // e.g., "925" for silver
    val material: String? = null, // e.g., "Silver", "Gold"
    val gemType: String? = null, // e.g., "Diamond", "Emerald"
    val platingType: String? = null, // e.g., "Gold Plated", "Rose Gold"
    val purity: String? = null, // e.g., "99.9%"
    
    // Ratings and reviews
    val rating: Double? = null,
    val reviewCount: Int = 0,
    val averageRating: Double? = null,
    
    // Metadata
    val isFeatured: Boolean = false,
    val isNew: Boolean = false,
    val isOnSale: Boolean = false,
    
    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val lastSyncedAt: Date = Date(),
    
    // Remote sync state
    val remoteId: String? = null,
    val isSynced: Boolean = true,
    val syncVersion: Int = 0
)
