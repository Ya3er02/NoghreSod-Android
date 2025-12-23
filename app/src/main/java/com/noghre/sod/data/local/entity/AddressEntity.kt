package com.noghre.sod.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "addresses",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId", name = "idx_address_user")
    ]
)
data class AddressEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val province: String,
    val city: String,
    val district: String?,
    val street: String,
    val postalCode: String,
    val phoneNumber: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)