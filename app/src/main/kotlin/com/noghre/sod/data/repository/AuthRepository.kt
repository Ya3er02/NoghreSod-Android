package com.noghre.sod.data.repository

import com.noghre.sod.data.local.PreferencesManager
import com.noghre.sod.data.remote.ApiService
import com.noghre.sod.data.remote.AuthRequest
import com.noghre.sod.data.remote.AuthResponse
import com.noghre.sod.domain.Result
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {

    fun isLoggedIn(): Flow<Boolean> = preferencesManager.isLoggedIn

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.login(AuthRequest(email, password))
            preferencesManager.saveAuthToken(response.token)
            preferencesManager.saveUser(response.user.id, response.user.email)
            Result.Success(response)
        } catch (e: Exception) {
            Timber.e(e, "Login failed")
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun register(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.register(AuthRequest(email, password))
            preferencesManager.saveAuthToken(response.token)
            preferencesManager.saveUser(response.user.id, response.user.email)
            Result.Success(response)
        } catch (e: Exception) {
            Timber.e(e, "Registration failed")
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            apiService.logout()
            preferencesManager.clearAuth()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Logout failed")
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }
}
