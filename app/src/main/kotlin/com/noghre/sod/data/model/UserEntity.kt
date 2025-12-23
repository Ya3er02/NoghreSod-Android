package com.noghre.sod.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User Entity for Room Database
 * Stores user account information and authentication details
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val phone: String,
    val email: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val profileImage: String? = null,
    val nationalId: String? = null,
    val verificationStatus: String = "UNVERIFIED", // UNVERIFIED, PENDING, VERIFIED
    val accountStatus: String = "ACTIVE", // ACTIVE, SUSPENDED, DELETED
    val createdAt: Long,
    val updatedAt: Long,
    val lastLoginAt: Long? = null
) {
    val fullName: String
        get() = "$firstName $lastName".trim()

    val isVerified: Boolean
        get() = verificationStatus == "VERIFIED"

    val isActive: Boolean
        get() = accountStatus == "ACTIVE"
}
