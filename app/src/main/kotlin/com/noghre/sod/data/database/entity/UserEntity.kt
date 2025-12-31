package com.noghre.sod.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * UserEntity - Database representation of a user.
 * 
 * Stores user profile information and authentication data.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    
    // Authentication
    val email: String,
    val phoneNumber: String? = null,
    val passwordHash: String? = null,
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    
    // Profile
    val firstName: String? = null,
    val lastName: String? = null,
    val avatar: String? = null,
    val bio: String? = null,
    
    // Contact information
    val defaultAddressId: String? = null,
    val shippingAddressId: String? = null,
    val billingAddressId: String? = null,
    
    // Preferences
    val language: String = "fa", // Persian by default
    val currency: String = "IRR",
    val notificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,
    val smsNotificationsEnabled: Boolean = false,
    
    // Account status
    val isActive: Boolean = true,
    val isBlocked: Boolean = false,
    val accountStatus: String = "active", // active, suspended, deleted, pending_verification
    
    // Membership
    val membershipTier: String = "bronze", // bronze, silver, gold, platinum
    val membershipPoints: Long = 0,
    val loyaltyBalance: Double = 0.0,
    
    // Statistics
    val totalOrderCount: Int = 0,
    val totalSpent: Double = 0.0,
    val averageRating: Double? = null,
    val reviewCount: Int = 0,
    
    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val lastLoginAt: Date? = null,
    val lastActivityAt: Date? = null,
    
    // Sync
    val isSynced: Boolean = true,
    val lastSyncedAt: Date = Date(),
    val authToken: String? = null,
    val tokenExpiresAt: Date? = null
)
