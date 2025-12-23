package com.noghre.sod.data.mapper

import com.noghre.sod.data.dto.AddressDto

/**
 * Mapper for Address conversions.
 */
object AddressMapper {

    /**
     * Convert AddressDto to domain representation.
     */
    fun AddressDto.toDomain(): Address {
        return Address(
            id = id,
            fullName = fullName,
            phone = phone,
            province = province,
            city = city,
            postalCode = postalCode,
            addressLine = addressLine,
            isDefault = isDefault
        )
    }
}

/**
 * Domain model for Address.
 */
data class Address(
    val id: String?,
    val fullName: String,
    val phone: String,
    val province: String,
    val city: String,
    val postalCode: String,
    val addressLine: String,
    val isDefault: Boolean
)
