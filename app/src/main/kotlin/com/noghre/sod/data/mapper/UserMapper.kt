package com.noghre.sod.data.mapper

import com.noghre.sod.data.database.entity.UserEntity
import com.noghre.sod.data.network.dto.UserDto
import com.noghre.sod.domain.model.User

/**
 * Mapper for User DTOs to Domain models.
 *
 * @author NoghreSod Team
 */
object UserMapper {

    fun UserDto.toDomain(): User = User(
        id = id ?: "",
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        email = email ?: "",
        phone = phone ?: "",
        profileImage = profileImage,
        addresses = addresses ?: emptyList(),
        lastUpdated = System.currentTimeMillis()
    )

    fun UserEntity.toDomain(): User = User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        profileImage = profileImage,
        addresses = addresses,
        lastUpdated = lastUpdated
    )

    fun User.toEntity(): UserEntity = UserEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        profileImage = profileImage,
        addresses = addresses,
        lastUpdated = lastUpdated
    )
}
