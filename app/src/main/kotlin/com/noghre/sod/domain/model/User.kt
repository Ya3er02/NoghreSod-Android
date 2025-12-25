package com.noghre.sod.domain.model

import java.time.LocalDateTime

/**
 * Domain model representing a user in the NoghreSod application.
 * 
 * @property id Unique user identifier
 * @property firstName User's first name
 * @property lastName User's last name
 * @property email User's email address (used for login)
 * @property phone User's phone number
 * @property profileImage URL to user's profile picture
 * @property address User's shipping address
 * @property city City of residence
 * @property country Country of residence
 * @property postalCode Postal/ZIP code
 * @property isVerified Whether email is verified
 * @property preferredCurrency Preferred currency for prices
 * @property createdAt Account creation date
 * @property lastLogin Last login timestamp
 * @property isActive Account status
 * 
 * @since 1.0.0
 */
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val profileImage: String?,
    val address: String?,
    val city: String?,
    val country: String?,
    val postalCode: String?,
    val isVerified: Boolean = false,
    val preferredCurrency: String = "USD",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastLogin: LocalDateTime? = null,
    val isActive: Boolean = true
) {
    
    /**
     * Get user's full name.
     */
    fun getFullName(): String = "$firstName $lastName"
    
    /**
     * Check if user profile is complete.
     */
    fun isProfileComplete(): Boolean {
        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank() &&
                address != null &&
                city != null &&
                country != null
    }
}
