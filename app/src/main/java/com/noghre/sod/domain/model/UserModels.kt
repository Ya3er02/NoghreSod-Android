package com.noghre.sod.domain.model

import java.time.LocalDateTime

// ============== USER ==============

/**
 * User domain model.
 * Contains all user information and preferences.
 */
data class User(
    val id: String,
    val email: Email,
    val phoneNumber: PhoneNumber?,
    val profile: UserProfile,
    val addresses: List<Address> = emptyList(),
    val preferences: UserPreferences = UserPreferences(),
    val isVerified: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    init {
        require(id.isNotBlank()) { "User ID cannot be blank" }
    }
}

// ============== EMAIL ==============

/**
 * Email value object with validation.
 * Ensures only valid email addresses are created.
 */
data class Email(val value: String) {
    init {
        require(isValid(value)) { "Invalid email format: $value" }
    }

    companion object {
        // RFC 5322 simplified regex for email validation
        private val EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        /**
         * Validate email format.
         */
        fun isValid(email: String): Boolean {
            return email.matches(EMAIL_REGEX) && email.length <= 254
        }
    }

    override fun toString(): String = value
}

// ============== PHONE NUMBER ==============

/**
 * Iranian phone number value object with validation.
 * Validates Iranian mobile phone numbers.
 */
data class PhoneNumber(val value: String) {
    init {
        require(isValid(value)) { "Invalid phone number format: $value" }
    }

    companion object {
        // Iranian mobile: 09xx xxx xxxx
        private val MOBILE_REGEX = "^09[0-9]{9}$".toRegex()

        /**
         * Validate Iranian phone number.
         */
        fun isValid(phone: String): Boolean {
            val cleaned = phone.replace(Regex("[\\s-()]"), "")
            return cleaned.matches(MOBILE_REGEX)
        }

        /**
         * Format phone number for display.
         */
        fun format(phone: String): String {
            val cleaned = phone.replace(Regex("[\\s-()]"), "")
            return if (cleaned.length == 11) {
                "${cleaned.substring(0, 4)} ${cleaned.substring(4, 7)} ${cleaned.substring(7)}"
            } else {
                phone
            }
        }
    }

    override fun toString(): String = format(value)
}

// ============== USER PROFILE ==============

/**
 * User profile information.
 * Contains personal and biographical data.
 */
data class UserProfile(
    val firstName: String,
    val lastName: String,
    val avatarUrl: String? = null,
    val nationalId: NationalId? = null,
    val dateOfBirth: LocalDateTime? = null,
    val gender: Gender? = null
) {
    init {
        require(firstName.isNotBlank()) { "First name cannot be blank" }
        require(lastName.isNotBlank()) { "Last name cannot be blank" }
    }

    val fullName: String
        get() = "$firstName $lastName"
}

// ============== NATIONAL ID ==============

/**
 * Iranian National ID value object with validation.
 * Validates using Luhn-like check digit algorithm.
 */
data class NationalId(val value: String) {
    init {
        require(isValid(value)) { "Invalid national ID: $value" }
    }

    companion object {
        /**
         * Validate Iranian National ID with check digit.
         */
        fun isValid(nationalId: String): Boolean {
            if (nationalId.length != 10) return false
            if (!nationalId.all { it.isDigit() }) return false

            // Check digit validation algorithm
            val check = nationalId[9].digitToInt()
            val sum = (0..8).sumOf {
                nationalId[it].digitToInt() * (10 - it)
            }
            val remainder = sum % 11

            return (remainder < 2 && check == remainder) ||
                   (remainder >= 2 && check == 11 - remainder)
        }
    }

    override fun toString(): String = value
}

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}

// ============== ADDRESS ==============

/**
 * User address.
 * Contains complete delivery address information.
 */
data class Address(
    val id: String,
    val title: String,
    val recipientName: String,
    val recipientPhone: PhoneNumber,
    val province: String,
    val city: String,
    val district: String? = null,
    val street: String,
    val postalCode: PostalCode,
    val fullAddress: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isDefault: Boolean = false
) {
    init {
        require(id.isNotBlank()) { "Address ID cannot be blank" }
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(province.isNotBlank()) { "Province cannot be blank" }
        require(city.isNotBlank()) { "City cannot be blank" }
        require(street.isNotBlank()) { "Street cannot be blank" }
    }
}

// ============== POSTAL CODE ==============

/**
 * Iranian postal code value object with validation.
 * Iranian postal codes are 10 digits.
 */
data class PostalCode(val value: String) {
    init {
        require(isValid(value)) { "Invalid postal code: $value" }
    }

    companion object {
        // Iranian postal code: 10 digits without dash
        private val POSTAL_CODE_REGEX = "^[0-9]{10}$".toRegex()

        /**
         * Validate Iranian postal code format.
         */
        fun isValid(postalCode: String): Boolean {
            val cleaned = postalCode.replace("-", "")
            return cleaned.matches(POSTAL_CODE_REGEX)
        }

        /**
         * Format postal code for display.
         */
        fun format(postalCode: String): String {
            val cleaned = postalCode.replace("-", "")
            return if (cleaned.length == 10) {
                "${cleaned.substring(0, 5)}-${cleaned.substring(5)}"
            } else {
                postalCode
            }
        }
    }

    override fun toString(): String = format(value)
}

// ============== USER PREFERENCES ==============

/**
 * User preferences and settings.
 * Contains user's language, theme, and notification preferences.
 */
data class UserPreferences(
    val language: Language = Language.PERSIAN,
    val currency: Currency = Currency.IRR,
    val notificationsEnabled: Boolean = true,
    val emailNotifications: Boolean = true,
    val smsNotifications: Boolean = true,
    val theme: Theme = Theme.SYSTEM
)

enum class Language(val code: String, val displayName: String) {
    PERSIAN("fa", "فارسی"),
    ENGLISH("en", "English")
}

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM
}
