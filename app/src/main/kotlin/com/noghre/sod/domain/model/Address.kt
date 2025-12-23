package com.noghre.sod.domain.model

data class Address(
    val id: String,
    val userId: String,
    val fullName: String,
    val phoneNumber: String,
    val province: String,
    val city: String,
    val district: String? = null,
    val street: String,
    val buildingNumber: String? = null,
    val postalCode: String,
    val isDefault: Boolean = false,
    val notes: String? = null
)
