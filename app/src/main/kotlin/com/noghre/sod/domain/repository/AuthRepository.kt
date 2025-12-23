package com.noghre.sod.domain.repository

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication and user operations
 */
interface AuthRepository {
    /**
     * Register new user
     */
    suspend fun register(
        email: String,
        phone: String,
        password: String,
        firstName: String,
        lastName: String,
    ): Result<AuthToken>

    /**
     * Login with email and password
     */
    suspend fun login(
        email: String,
        password: String,
    ): Result<AuthToken>

    /**
     * Login with phone number
     */
    suspend fun loginWithPhone(
        phone: String,
        code: String, // OTP code
    ): Result<AuthToken>

    /**
     * Request OTP for phone login
     */
    suspend fun requestOTP(phone: String): Result<Unit>

    /**
     * Verify OTP
     */
    suspend fun verifyOTP(
        phone: String,
        code: String,
    ): Result<Boolean>

    /**
     * Refresh authentication token
     */
    suspend fun refreshToken(refreshToken: String): Result<AuthToken>

    /**
     * Logout
     */
    suspend fun logout(): Result<Unit>

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Flow<Result<Boolean>>

    /**
     * Get current user
     */
    fun getCurrentUser(): Flow<Result<User>>

    /**
     * Update user profile
     */
    suspend fun updateProfile(
        firstName: String? = null,
        lastName: String? = null,
        profileImage: String? = null,
        bio: String? = null,
        birthDate: String? = null,
    ): Result<User>

    /**
     * Change password
     */
    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
    ): Result<Unit>

    /**
     * Request password reset
     */
    suspend fun requestPasswordReset(email: String): Result<Unit>

    /**
     * Reset password with code
     */
    suspend fun resetPassword(
        email: String,
        code: String,
        newPassword: String,
    ): Result<Unit>

    /**
     * Add shipping address
     */
    suspend fun addShippingAddress(address: Address): Result<Address>

    /**
     * Update shipping address
     */
    suspend fun updateShippingAddress(
        addressId: String,
        address: Address,
    ): Result<Address>

    /**
     * Delete shipping address
     */
    suspend fun deleteShippingAddress(addressId: String): Result<Unit>

    /**
     * Get shipping addresses
     */
    fun getShippingAddresses(): Flow<Result<List<Address>>>

    /**
     * Set default shipping address
     */
    suspend fun setDefaultShippingAddress(addressId: String): Result<Unit>

    /**
     * Update user preferences
     */
    suspend fun updatePreferences(preferences: UserPreferences): Result<UserPreferences>

    /**
     * Enable two-factor authentication
     */
    suspend fun enableTwoFactor(): Result<TwoFactorSetup>

    /**
     * Disable two-factor authentication
     */
    suspend fun disableTwoFactor(code: String): Result<Unit>

    /**
     * Verify two-factor code
     */
    suspend fun verifyTwoFactorCode(code: String): Result<Boolean>

    /**
     * Delete account
     */
    suspend fun deleteAccount(password: String): Result<Unit>

    /**
     * Get user security settings
     */
    fun getSecuritySettings(): Flow<Result<SecuritySettings>>
}

/**
 * Two-factor setup result
 */
data class TwoFactorSetup(
    val secret: String,
    val qrCode: String, // Base64 encoded QR code image
    val backupCodes: List<String>,
)

/**
 * Security settings for user account
 */
data class SecuritySettings(
    val twoFactorEnabled: Boolean,
    val lastPasswordChangeDate: java.time.LocalDateTime? = null,
    val activeDevices: List<DeviceInfo> = emptyList(),
    val loginHistory: List<LoginHistory> = emptyList(),
    val blockedIPs: List<String> = emptyList(),
)

/**
 * Device information
 */
data class DeviceInfo(
    val deviceId: String,
    val deviceName: String,
    val deviceType: String, // MOBILE, DESKTOP, TABLET
    val lastActiveAt: java.time.LocalDateTime,
    val isCurrent: Boolean,
)

/**
 * Login history entry
 */
data class LoginHistory(
    val id: String,
    val timestamp: java.time.LocalDateTime,
    val ipAddress: String,
    val location: String? = null,
    val deviceInfo: String? = null,
    val success: Boolean,
)
