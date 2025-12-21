package com.noghre.sod.utils.validation

/**
 * Utilities for validating user input.
 * Optimized for Iran-specific use cases (phone, postal code formats).
 */
object InputValidators {
    private val EMAIL_REGEX =
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    private val PHONE_REGEX = "^09\\d{9}$".toRegex()  // Iranian phone format
    private val POSTAL_CODE_REGEX = "^\\d{10}$".toRegex()  // Iranian postal code

    // Quick validation functions
    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email)

    fun isValidIranianPhone(phone: String): Boolean = PHONE_REGEX.matches(phone)

    fun isValidPostalCode(code: String): Boolean = POSTAL_CODE_REGEX.matches(code)

    fun isValidPassword(password: String): Boolean = password.length >= 8

    fun isValidName(name: String): Boolean = name.trim().length >= 2

    fun isNotEmpty(value: String): Boolean = value.trim().isNotEmpty()

    // Validation result types
    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val message: String) : ValidationResult()
    }

    // Comprehensive validation functions
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult.Invalid("Email is required")
            !isValidEmail(email) -> ValidationResult.Invalid("Invalid email format")
            else -> ValidationResult.Valid
        }
    }

    fun validatePhone(phone: String): ValidationResult {
        return when {
            phone.isEmpty() -> ValidationResult.Invalid("Phone is required")
            !isValidIranianPhone(phone) ->
                ValidationResult.Invalid("Invalid phone format (09XXXXXXXXX)")

            else -> ValidationResult.Valid
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult.Invalid("Password is required")
            !isValidPassword(password) ->
                ValidationResult.Invalid("Password must be at least 8 characters")

            else -> ValidationResult.Valid
        }
    }

    fun validateName(name: String): ValidationResult {
        return when {
            name.isEmpty() -> ValidationResult.Invalid("Name is required")
            !isValidName(name) -> ValidationResult.Invalid("Name must be at least 2 characters")
            else -> ValidationResult.Valid
        }
    }

    fun validatePostalCode(code: String): ValidationResult {
        return when {
            code.isEmpty() -> ValidationResult.Invalid("Postal code is required")
            !isValidPostalCode(code) ->
                ValidationResult.Invalid("Invalid postal code format (10 digits)")

            else -> ValidationResult.Valid
        }
    }
}
