package com.noghre.sod.data.mapper

import com.noghre.sod.data.dto.UserDto
import com.noghre.sod.data.local.entity.UserEntity

class UserMapper {
    fun toEntity(dto: UserDto): UserEntity {
        return UserEntity(
            id = dto.id,
            phone = dto.phone,
            email = dto.email,
            name = dto.name,
            nameEn = dto.nameEn,
            profileImage = dto.profileImage,
            address = dto.address,
            nationalId = dto.nationalId,
            birthDate = dto.birthDate,
            gender = dto.gender,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            isVerified = dto.isVerified,
            isActive = dto.isActive
        )
    }

    fun toDto(entity: UserEntity): UserDto {
        return UserDto(
            id = entity.id,
            phone = entity.phone,
            email = entity.email,
            name = entity.name,
            nameEn = entity.nameEn,
            profileImage = entity.profileImage,
            address = entity.address,
            nationalId = entity.nationalId,
            birthDate = entity.birthDate,
            gender = entity.gender,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            isVerified = entity.isVerified,
            isActive = entity.isActive
        )
    }
}