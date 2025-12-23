package com.noghre.sod.data.repository

import com.noghre.sod.data.local.dao.UserDao
import com.noghre.sod.data.local.prefs.TokenManager
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.api.request.LoginRequest
import com.noghre.sod.data.remote.api.request.OtpRequest
import com.noghre.sod.data.remote.api.request.RegisterRequest
import com.noghre.sod.data.remote.api.request.UpdateProfileRequest
import com.noghre.sod.data.remote.exception.ApiException
import com.noghre.sod.data.remote.network.NetworkMonitor
import com.noghre.sod.domain.Result
import timber.log.Timber
import javax.inject.Inject

/**
 * User Repository Implementation with token and profile management.
 */
class UserRepositoryImpl @Inject constructor(
    private val api: NoghreSodApiService,
    private val userDao: UserDao,
    private val tokenManager: TokenManager,
    private val networkMonitor: NetworkMonitor
) {

    /**
     * Login with phone and password.
     */
    suspend fun login(phone: String, password: String): Result<Unit> = try {
        if (!networkMonitor.isNetworkAvailable()) {
            return Result.Error("No internet connection")
        }

        val response = api.login(LoginRequest(phone, password))
        if (response.success && response.data != null) {
            tokenManager.saveTokens(
                response.data.accessToken,
                response.data.refreshToken,
                response.data.expiresIn
            )
            // Cache user profile
            response.data.user.let { user ->
                userDao.insertUser(
                    com.noghre.sod.data.local.entity.UserEntity(
                        id = user.id,
                        phone = user.phone,
                        email = user.email,
                        fullName = user.fullName,
                        avatarUrl = user.avatarUrl
                    )
                )
            }
            Timber.d("Login successful")
            Result.Success(Unit)
        } else {
            Result.Error(response.message ?: "Login failed")
        }
    } catch (e: ApiException) {
        Timber.e(e, "API error during login")
        Result.Error(e.message ?: "Network error")
    } catch (e: Exception) {
        Timber.e(e, "Error during login")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Register new user.
     */
    suspend fun register(
        phone: String,
        fullName: String,
        password: String,
        passwordConfirm: String
    ): Result<Unit> = try {
        if (!networkMonitor.isNetworkAvailable()) {
            return Result.Error("No internet connection")
        }

        val response = api.register(
            RegisterRequest(
                phone = phone,
                fullName = fullName,
                password = password,
                passwordConfirmation = passwordConfirm
            )
        )
        if (response.success) {
            Result.Success(Unit)
        } else {
            Result.Error(response.message ?: "Registration failed")
        }
    } catch (e: ApiException) {
        Timber.e(e, "API error during registration")
        Result.Error(e.message ?: "Network error")
    } catch (e: Exception) {
        Timber.e(e, "Error during registration")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Verify OTP for phone authentication.
     */
    suspend fun verifyOtp(phone: String, code: String): Result<Unit> = try {
        if (!networkMonitor.isNetworkAvailable()) {
            return Result.Error("No internet connection")
        }

        val response = api.verifyOtp(OtpRequest(phone, code))
        if (response.success && response.data != null) {
            tokenManager.saveTokens(
                response.data.accessToken,
                response.data.refreshToken,
                response.data.expiresIn
            )
            Result.Success(Unit)
        } else {
            Result.Error(response.message ?: "OTP verification failed")
        }
    } catch (e: Exception) {
        Timber.e(e, "Error verifying OTP")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Logout user.
     */
    suspend fun logout(): Result<Unit> = try {
        if (networkMonitor.isNetworkAvailable()) {
            try {
                api.logout()
            } catch (e: Exception) {
                Timber.e(e, "Error calling logout endpoint")
            }
        }
        tokenManager.clearTokens()
        userDao.deleteUser()
        Timber.d("Logout successful")
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error during logout")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Get user profile.
     */
    suspend fun getProfile(): Result<Unit> = try {
        if (!networkMonitor.isNetworkAvailable()) {
            return Result.Error("No internet connection")
        }

        val response = api.getProfile()
        if (response.success && response.data != null) {
            userDao.insertUser(
                com.noghre.sod.data.local.entity.UserEntity(
                    id = response.data.id,
                    phone = response.data.phone,
                    email = response.data.email,
                    fullName = response.data.fullName,
                    avatarUrl = response.data.avatarUrl
                )
            )
            Result.Success(Unit)
        } else {
            Result.Error(response.message ?: "Failed to fetch profile")
        }
    } catch (e: Exception) {
        Timber.e(e, "Error getting profile")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Update user profile.
     */
    suspend fun updateProfile(fullName: String, email: String?): Result<Unit> = try {
        if (!networkMonitor.isNetworkAvailable()) {
            return Result.Error("No internet connection")
        }

        val response = api.updateProfile(UpdateProfileRequest(fullName, email))
        if (response.success) {
            Result.Success(Unit)
        } else {
            Result.Error(response.message ?: "Update failed")
        }
    } catch (e: Exception) {
        Timber.e(e, "Error updating profile")
        Result.Error(e.message ?: "Unknown error")
    }
}
