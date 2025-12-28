package com.noghre.sod.data.repository

import com.noghre.sod.core.error.AppException
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.local.TokenManager
import com.noghre.sod.data.local.UserDao
import com.noghre.sod.data.remote.UserApi
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.repository.UserRepository
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userDao: UserDao,
    private val tokenManager: TokenManager
) : UserRepository {
    
    override suspend fun login(email: String, password: String): Result<User> = try {
        Timber.d("Logging in user: $email")
        
        val response = userApi.login(email, password)
        
        // Save token
        tokenManager.saveToken(response.token)
        Timber.d("Token saved")
        
        // Save user
        userDao.insert(response.user)
        Timber.d("User saved to local DB")
        
        Result.Success(response.user)
    } catch (e: Exception) {
        Timber.e("Login failed: ${e.message}")
        Result.Error(AppException.AuthException("Login failed"))
    }
    
    override suspend fun register(name: String, email: String, password: String): Result<User> = try {
        Timber.d("Registering user: $email")
        
        val response = userApi.register(name, email, password)
        
        // Save token
        tokenManager.saveToken(response.token)
        Timber.d("Token saved")
        
        // Save user
        userDao.insert(response.user)
        Timber.d("User saved to local DB")
        
        Result.Success(response.user)
    } catch (e: Exception) {
        Timber.e("Registration failed: ${e.message}")
        Result.Error(AppException.AuthException("Registration failed"))
    }
    
    override suspend fun getCurrentUser(): Result<User> = try {
        Timber.d("Fetching current user")
        
        val response = userApi.getCurrentUser()
        
        // Save user
        userDao.insert(response)
        Timber.d("Current user saved to local DB")
        
        Result.Success(response)
    } catch (e: Exception) {
        Timber.e("Error fetching current user: ${e.message}")
        // Fallback to local data
        try {
            val localUser = userDao.getCurrentUser()
            if (localUser != null) {
                Timber.d("Returning local user")
                Result.Success(localUser)
            } else {
                Result.Error(AppException.AuthException("Not authenticated"))
            }
        } catch (ex: Exception) {
            Timber.e("Error loading local user: ${ex.message}")
            Result.Error(AppException.AuthException("Not authenticated"))
        }
    }
    
    override suspend fun updateProfile(name: String?, phone: String?, avatar: String?): Result<Unit> = try {
        Timber.d("Updating profile")
        
        val updateData = mutableMapOf<String, String>()
        name?.let { updateData["name"] = it }
        phone?.let { updateData["phone"] = it }
        avatar?.let { updateData["avatar"] = it }
        
        userApi.updateProfile(updateData)
        
        // Update local data
        val currentUser = userDao.getCurrentUser()
        if (currentUser != null) {
            val updatedUser = currentUser.copy(
                name = name ?: currentUser.name,
                phone = phone ?: currentUser.phone,
                avatar = avatar ?: currentUser.avatar
            )
            userDao.update(updatedUser)
            Timber.d("Profile updated")
        }
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error updating profile: ${e.message}")
        Result.Error(AppException.NetworkException(e.message ?: "Update failed"))
    }
    
    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> = try {
        Timber.d("Changing password")
        
        userApi.changePassword(oldPassword, newPassword)
        Timber.d("Password changed")
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error changing password: ${e.message}")
        Result.Error(AppException.NetworkException(e.message ?: "Change password failed"))
    }
    
    override suspend fun logout(): Result<Unit> = try {
        Timber.d("Logging out")
        
        try {
            userApi.logout()
        } catch (e: Exception) {
            Timber.e("API logout failed: ${e.message}, continuing with local logout")
        }
        
        // Clear token
        tokenManager.clearToken()
        
        // Clear user data
        userDao.deleteAll()
        Timber.d("User data cleared")
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error during logout: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Logout failed"))
    }
    
    override suspend fun isAuthenticated(): Result<Boolean> = try {
        Timber.d("Checking authentication")
        val token = tokenManager.getToken()
        val isAuth = token != null && token.isNotBlank()
        Timber.d("Authentication status: $isAuth")
        Result.Success(isAuth)
    } catch (e: Exception) {
        Timber.e("Error checking authentication: ${e.message}")
        Result.Success(false)
    }
    
    override suspend fun refreshToken(): Result<String> = try {
        Timber.d("Refreshing token")
        
        val response = userApi.refreshToken()
        
        // Save new token
        tokenManager.saveToken(response.token)
        Timber.d("Token refreshed")
        
        Result.Success(response.token)
    } catch (e: Exception) {
        Timber.e("Error refreshing token: ${e.message}")
        Result.Error(AppException.AuthException("Token refresh failed"))
    }
}
