package com.noghre.sod.domain.model

/**
 * Domain model for User.
 * Represents an authenticated user in the system.
 *
 * @property id Unique user identifier
 * @property email User email address
 * @property phoneNumber User phone number
 * @property firstName User first name
 * @property lastName User last name
 * @property profileImageUrl Profile picture URL
 * @property addresses List of saved addresses
 * @property favoriteProducts List of favorite product IDs
 * @property isVerified Email verification status
 * @property isPhoneVerified Phone verification status
 * @property createdAt Account creation timestamp
 * @property lastLogin Last login timestamp
 */
data class User(
    val id: String,
    val email: String,
    val phoneNumber: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val profileImageUrl: String? = null,
    val addresses: List<Address> = emptyList(),
    val favoriteProducts: List<String> = emptyList(),
    val isVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val createdAt: String = "",
    val lastLogin: String? = null
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}

/**
 * Domain model for User Address.
 *
 * @property id Unique address identifier
 * @property title Address title (Home, Work, etc.)
 * @property fullAddress Complete address
 * @property province Province/State
 * @property city City
 * @property postalCode Postal code
 * @property latitude Latitude coordinate
 * @property longitude Longitude coordinate
 * @property isDefault Default delivery address
 */
data class Address(
    val id: String,
    val title: String,
    val fullAddress: String,
    val province: String,
    val city: String,
    val postalCode: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isDefault: Boolean = false
)
