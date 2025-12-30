package com.noghre.sod.data.repository

import com.noghre.sod.data.local.preferences.PreferencesManager
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.model.Result
import com.noghre.sod.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository.
 * 
* Handles user authentication, login, registration, and session management.
 * Stores authentication tokens securely using DataStore.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) : AuthRepository {

    /**
     * User login with mobile number and password.
     */
    override suspend fun login(
        mobile: String,
        password: String
    ): Result<User> {
        return try {
            val response = apiService.login(
                mobile = mobile,
                password = password
            )
            
            if (response.isSuccessful) {
                response.body()?.data?.let { loginResponse ->
                    // Save token
                    preferencesManager.setAuthToken(loginResponse.token)
                    preferencesManager.setRefreshToken(loginResponse.refreshToken)
                    
                    // Save user info
                    val user = loginResponse.user
                    preferencesManager.setCurrentUser(user)
                    
                    return Result.Success(user)
                }
            }
            
            Result.Error(
                exception = Exception(response.message()),
                message = "Login failed: ${response.message()}"
            )
        } catch (e: Exception) {
            Timber.e(e, "Login error")
            Result.Error(
                exception = e,
                message = "Login failed: ${e.localizedMessage}"
            )
        }
    }

    /**
     * User registration (signup).
     */
    override suspend fun register(
        name: String,
        mobile: String,
        email: String,
        password: String
    ): Result<User> {
        return try {
            val response = apiService.register(
                name = name,
                mobile = mobile,
                email = email,
                password = password
            )
            
            if (response.isSuccessful) {
                response.body()?.data?.let { registerResponse ->
                    // Save token
                    preferencesManager.setAuthToken(registerResponse.token)
                    preferencesManager.setRefreshToken(registerResponse.refreshToken)
                    
                    // Save user info
                    val user = registerResponse.user
                    preferencesManager.setCurrentUser(user)
                    
                    return Result.Success(user)
                }
            }
            
            Result.Error(
                exception = Exception(response.message()),
                message = "Registration failed"
            )
        } catch (e: Exception) {
            Timber.e(e, "Registration error")
            Result.Error(
                exception = e,
                message = "Registration failed"
            )
        }
    }

    /**
     * Logout and clear authentication.
     */
    override suspend fun logout(): Result<Unit> {
        return try {
            // Call logout API
            apiService.logout()
            
            // Clear local data
            preferencesManager.clearAuthData()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Logout error")
            
            // Clear local data even if API call fails
            preferencesManager.clearAuthData()
            
            Result.Error(
                exception = e,
                message = "Logout error"
            )
        }
    }

    /**
     * Refresh authentication token.
     */
    override suspend fun refreshToken(): Result<String> {
        return try {
            val refreshToken = preferencesManager.getRefreshToken() ?: ""
            val response = apiService.refreshToken(refreshToken)
            
            if (response.isSuccessful) {
                response.body()?.token?.let { newToken ->
                    preferencesManager.setAuthToken(newToken)
                    return Result.Success(newToken)
                }
            }
            
            Result.Error(
                exception = Exception(response.message()),
                message = "Token refresh failed"
            )
        } catch (e: Exception) {
            Timber.e(e, "Token refresh error")
            Result.Error(
                exception = e,
                message = "Token refresh failed"
            )
        }
    }

    /**
     * Get current user profile.
     */
    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = apiService.getUserProfile()
            
            if (response.isSuccessful) {
                response.body()?.data?.let { user ->
                    preferencesManager.setCurrentUser(user)
                    return Result.Success(user)
                }
            }
            
            Result.Error(
                exception = Exception(response.message()),
                message = "Failed to fetch user profile"
            )
        } catch (e: Exception) {
            Timber.e(e, "Error fetching user profile")
            Result.Error(
                exception = e,
                message = "Failed to fetch user profile"
            )
        }
    }

    /**
     * Check if user is authenticated.
     */
    override suspend fun isAuthenticated(): Boolean {
        return preferencesManager.getAuthToken() != null
    }

    /**
     * Get stored auth token.
     */
    override suspend fun getAuthToken(): String? {
        return preferencesManager.getAuthToken()
    }

    /**
     * Request password reset.
     */
    override suspend fun requestPasswordReset(mobile: String): Result<Unit> {
        return try {
            val response = apiService.requestPasswordReset(mobile)
            
            if (response.isSuccessful) {
                return Result.Success(Unit)
            }
            
            Result.Error(
                exception = Exception(response.message()),
                message = "Password reset request failed"
            )
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Password reset request failed"
            )
        }
    }

    /**
     * Update user profile.
     */
    override suspend fun updateProfile(user: User): Result<User> {
        return try {
            val response = apiService.updateProfile(user.toDto())
            
            if (response.isSuccessful) {
                response.body()?.data?.let { updatedUser ->
                    preferencesManager.setCurrentUser(updatedUser)
                    return Result.Success(updatedUser)
                }
            }
            
            Result.Error(
                exception = Exception(response.message()),
                message = "Profile update failed"
            )
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Profile update failed"
            )
        }
    }
}
