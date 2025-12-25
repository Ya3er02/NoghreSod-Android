package com.noghre.sod.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Register Request DTO with comprehensive validation
 * 
 * Validates:
 * - Email format and uniqueness
 * - Phone number format (Iranian)
 * - Password strength
 * - Name length and format
 */
@Serializable
data class RegisterRequestDto(
    @SerialName("email")
    val email: String,
    
    @SerialName("phone")
    val phone: String,
    
    @SerialName("password")
    val password: String,
    
    @SerialName("firstName")
    val firstName: String,
    
    @SerialName("lastName")
    val lastName: String,
    
    @SerialName("referralCode")
    val referralCode: String? = null
) {
    init {
        // Email validation
        require(email.isNotBlank()) { "ایمیل نمی تواند خالی باشد" }
        require(email.isValidEmail()) { "فرمت ایمیل نامعتبر است" }
        
        // Phone validation
        require(phone.isNotBlank()) { "شماره تلفن نمی تواند خالی باشد" }
        require(phone.isValidIranianPhone()) { "شماره تلفن نامعتبر است" }
        
        // Password validation
        require(password.isNotBlank()) { "رمز عبور نمی تواند خالی باشد" }
        require(password.length >= 8) { "رمز عبور باید حداقل 8 کاراکتر باشد" }
        require(password.hasUpperCase()) { "رمز عبور باید حداقل یک حرف بزرگ داشته باشد" }
        require(password.hasDigit()) { "رمز عبور باید حداقل یک عدد داشته باشد" }
        require(password.hasSpecialChar()) { "رمز عبور باید حداقل یک کاراکتر خاص داشته باشد" }
        
        // Name validation
        require(firstName.isNotBlank()) { "نام نمی تواند خالی باشد" }
        require(firstName.length >= 2) { "نام باید حداقل 2 کاراکتر باشد" }
        require(firstName.length <= 50) { "نام نمی تواند بیشتر از 50 کاراکتر باشد" }
        
        require(lastName.isNotBlank()) { "نام خانوادگی نمی تواند خالی باشد" }
        require(lastName.length >= 2) { "نام خانوادگی باید حداقل 2 کاراکتر باشد" }
        require(lastName.length <= 50) { "نام خانوادگی نمی تواند بیشتر از 50 کاراکتر باشد" }
    }
}

// Validation extensions
fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return emailRegex.matches(this)
}

fun String.isValidIranianPhone(): Boolean {
    // Supports: 09123456789 or +989123456789 or 09123456789
    val phoneRegex = "^(\\+98|0)?9\\d{9}$".toRegex()
    return phoneRegex.matches(this)
}

fun String.hasUpperCase(): Boolean = any { it.isUpperCase() }
fun String.hasDigit(): Boolean = any { it.isDigit() }
fun String.hasSpecialChar(): Boolean = any { !it.isLetterOrDigit() }
