package com.noghre.sod.presentation.auth

/**
 * UI state for authentication screens
 */
data class AuthUiState(
    val phone: String = "",
    val password: String = "",
    val fullName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isOtpSent: Boolean = false,
    val otpCode: String = "",
    val phoneError: String? = null,
    val passwordError: String? = null,
    val fullNameError: String? = null,
    val otpCountdown: Int = 0
)
