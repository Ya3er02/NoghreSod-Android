package com.noghre.sod.domain.model

import java.time.LocalDateTime

/**
 * User domain model representing customer profile
 */
data class User(
    val id: String,
    val email: String,
    val phone: String,
    val firstName: String,
    val lastName: String,
    val firstNameFarsi: String? = null,
    val lastNameFarsi: String? = null,
    val profileImage: String? = null,
    val bio: String? = null,
    val birthDate: String? = null, // Format: yyyy-MM-dd
    val nationalId: String? = null,
    val shippingAddresses: List<Address> = emptyList(),
    val billingAddress: Address? = null,
    val preferredShippingAddress: String? = null, // Address ID
    val phoneVerified: Boolean = false,
    val emailVerified: Boolean = false,
    val twoFactorEnabled: Boolean = false,
    val totalOrders: Int = 0,
    val totalSpent: Long = 0,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val favoriteProducts: List<String> = emptyList(), // Product IDs
    val blockedSellers: List<String> = emptyList(), // Seller IDs
    val loyaltyPoints: Long = 0,
    val membershipTier: MembershipTier = MembershipTier.STANDARD,
    val preferences: UserPreferences = UserPreferences(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val lastLoginAt: LocalDateTime? = null,
    val isActive: Boolean = true,
    val isBanned: Boolean = false,
)

/**
 * User address information
 */
data class Address(
    val id: String? = null,
    val title: String, // Home, Work, etc. (فارسی: خانه، محل کار)
    val recipientName: String,
    val phoneNumber: String,
    val province: String, // استان
    val city: String, // شهر
    val district: String? = null, // منطقه
    val street: String, // خیابان
    val buildingNumber: String? = null,
    val buildingUnit: String? = null, // واحد
    val postalCode: String,
    val coordinates: LocationCoordinates? = null,
    val isDefault: Boolean = false,
    val instructions: String? = null, // Special delivery instructions
)

/**
 * Location coordinates for map display
 */
data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double,
)

/**
 * User preferences for notifications and display
 */
data class UserPreferences(
    val emailNotifications: Boolean = true,
    val smsNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val orderUpdates: Boolean = true,
    val promotionNotifications: Boolean = true,
    val newProductNotifications: Boolean = true,
    val languagePreference: LanguagePreference = LanguagePreference.PERSIAN,
    val currencyPreference: Currency = Currency.TOMAN,
    val darkMode: Boolean? = null, // null = system preference
)

/**
 * Language preference
 */
enum class LanguagePreference {
    PERSIAN,  // فارسی
    ENGLISH,
}

/**
 * Currency preference
 */
enum class Currency {
    TOMAN,    // تومان
    RIAL,     // ریال
    USD,
    EUR,
}

/**
 * User membership tier for loyalty program
 */
enum class MembershipTier {
    STANDARD,  // Standard member
    SILVER,    // Silver member with benefits
    GOLD,      // Gold member with more benefits
    PLATINUM,  // Platinum VIP member
}

/**
 * Compact user information for display purposes
 */
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val profileImage: String?,
    val membershipTier: MembershipTier,
    val totalOrders: Int,
    val rating: Float,
)

/**
 * Authentication credentials (only for login/registration)
 * Should never be stored in app, only in secure storage
 */
data class AuthCredentials(
    val email: String,
    val password: String,
)

/**
 * Authentication token response
 */
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long, // Seconds until expiration
    val user: User,
)
