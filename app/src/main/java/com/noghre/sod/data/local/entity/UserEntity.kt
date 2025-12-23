package com.noghre.sod.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val phone: String,
    val email: String?,
    val name: String,
    val nameEn: String?,
    val profileImage: String?,
    val address: String?,
    val nationalId: String?,
    val birthDate: Long?,
    val gender: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isVerified: Boolean = false,
    val isActive: Boolean = true
)