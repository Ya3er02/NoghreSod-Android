package com.noghre.sod.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.noghre.sod.data.model.UserEntity

/**
 * User Authentication DTOs
 */

@Serializable
data class LoginRequestDto(
    @SerialName("phone")
    val phone: String,
    @SerialName("password")
    val password: String
)

@Serializable
data class LoginResponseDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: UserTokenDto,
    @SerialName("message")
    val message: String? = null
)

@Serializable
data class UserTokenDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("token")
    val token: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("expires_in")
    val expiresIn: Long,
    @SerialName("token_type")
    val tokenType: String = "Bearer"
) {
    fun toEntity(): UserEntity = UserEntity(
        id = userId,
        phone = phone,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}

@Serializable
data class UserProfileDto(
    @SerialName("id")
    val id: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("email")
    val email: String? = null,
    @SerialName("first_name")
    val firstName: String = "",
    @SerialName("last_name")
    val lastName: String = "",
    @SerialName("profile_image")
    val profileImage: String? = null,
    @SerialName("national_id")
    val nationalId: String? = null,
    @SerialName("verification_status")
    val verificationStatus: String = "UNVERIFIED",
    @SerialName("account_status")
    val accountStatus: String = "ACTIVE",
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long
) {
    fun toEntity(): UserEntity = UserEntity(
        id = id,
        phone = phone,
        email = email,
        firstName = firstName,
        lastName = lastName,
        profileImage = profileImage,
        nationalId = nationalId,
        verificationStatus = verificationStatus,
        accountStatus = accountStatus,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

@Serializable
data class UpdateUserProfileDto(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("email")
    val email: String? = null,
    @SerialName("profile_image")
    val profileImage: String? = null
)

@Serializable
data class RefreshTokenDto(
    @SerialName("refresh_token")
    val refreshToken: String
)

@Serializable
data class VerifyPhoneDto(
    @SerialName("phone")
    val phone: String
)

@Serializable
data class VerifyCodeDto(
    @SerialName("phone")
    val phone: String,
    @SerialName("code")
    val code: String
)
