package com.noghre.sod.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val phone: String,
    val firstName: String,
    val lastName: String,
    val profileImage: String?,
    val membershipTier: String = "STANDARD",
    val totalOrders: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "user_addresses")
data class AddressEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val recipientName: String,
    val phone: String,
    val province: String,
    val city: String,
    val street: String,
    val postalCode: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey
    val userId: String,
    val language: String = "fa",
    val currency: String = "IRR",
    val emailNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "auth_tokens")
data class AuthTokenEntity(
    @PrimaryKey
    val id: String = "current_token",
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val createdAt: Long = System.currentTimeMillis(),
)
