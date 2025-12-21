package com.noghre.sod.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
from kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val profileImageUrl: String? = null,
    val bio: String? = null,
    val addresses: List<Address> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class Address(
    val id: String,
    val label: String, // Home, Office, etc.
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val isDefault: Boolean = false
)
