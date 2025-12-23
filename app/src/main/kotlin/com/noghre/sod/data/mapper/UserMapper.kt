package com.noghre.sod.data.mapper

import com.noghre.sod.data.dto.UserDto
import com.noghre.sod.data.local.entity.UserEntity

/**
 * Mapper for User conversions between layers.
 */
object UserMapper {

    /**
     * Convert UserDto to UserEntity.
     */
    fun UserDto.toEntity(): UserEntity {
        return UserEntity(
            id = id,
            phone = phone,
            email = email,
            fullName = fullName,
            avatarUrl = avatarUrl
        )
    }

    /**
     * Convert UserEntity to UserDto.
     */
    fun UserEntity.toDto(): UserDto {
        return UserDto(
            id = id,
            phone = phone,
            email = email,
            fullName = fullName,
            avatarUrl = avatarUrl
        )
    }
}
