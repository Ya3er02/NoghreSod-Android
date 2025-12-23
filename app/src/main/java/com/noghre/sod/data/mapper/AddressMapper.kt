package com.noghre.sod.data.mapper

import com.noghre.sod.data.dto.AddressDto
import com.noghre.sod.data.local.entity.AddressEntity

class AddressMapper {
    fun toEntity(dto: AddressDto): AddressEntity {
        return AddressEntity(
            id = dto.id,
            userId = dto.userId,
            title = dto.title,
            firstName = dto.firstName,
            lastName = dto.lastName,
            province = dto.province,
            city = dto.city,
            district = dto.district,
            street = dto.street,
            postalCode = dto.postalCode,
            phoneNumber = dto.phoneNumber,
            isDefault = dto.isDefault,
            createdAt = dto.createdAt
        )
    }

    fun toDto(entity: AddressEntity): AddressDto {
        return AddressDto(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            firstName = entity.firstName,
            lastName = entity.lastName,
            province = entity.province,
            city = entity.city,
            district = entity.district,
            street = entity.street,
            postalCode = entity.postalCode,
            phoneNumber = entity.phoneNumber,
            isDefault = entity.isDefault,
            createdAt = entity.createdAt
        )
    }

    fun toEntities(dtos: List<AddressDto>): List<AddressEntity> = dtos.map { toEntity(it) }
    fun toDtos(entities: List<AddressEntity>): List<AddressDto> = entities.map { toDto(it) }
}