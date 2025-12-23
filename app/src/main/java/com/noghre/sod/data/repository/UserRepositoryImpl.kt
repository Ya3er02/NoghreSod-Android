package com.noghre.sod.data.repository

import com.noghre.sod.data.dto.UserDto
import com.noghre.sod.data.dto.request.LoginRequest
import com.noghre.sod.data.dto.request.RegisterRequest
import com.noghre.sod.data.dto.request.UpdateProfileRequest
import com.noghre.sod.data.local.dao.UserDao
import com.noghre.sod.data.local.prefs.TokenManager
import com.noghre.sod.data.mapper.UserMapper
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.exception.ApiException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class UserRepositoryImpl(
    private val apiService: NoghreSodApiService,
    private val userDao: UserDao,
    private val tokenManager: TokenManager,
    private val mapper: UserMapper
) : UserRepository {

    override suspend fun login(phone: String, password: String): Result<UserDto> {
        return try {
            val response = apiService.login(LoginRequest(phone, password))
            if (response.success && response.data != null) {
                tokenManager.saveAccessToken(response.data.accessToken)
                tokenManager.saveRefreshToken(response.data.refreshToken)
                tokenManager.saveUserId(response.data.userId)
                Result.Success(UserDto(
                    id = response.data.userId,
                    phone = phone,
                    name = "",
                    email = null
                ))
            } else {
                Result.Error(ApiException.Unauthorized())
            }
        } catch (e: Exception) {
            Timber.e(e, "Login failed")
            Result.Error(handleException(e))
        }
    }

    override suspend fun register(
        phone: String,
        password: String,
        name: String,
        email: String?
    ): Result<UserDto> {
        return try {
            val response = apiService.register(RegisterRequest(phone, password, name, email))
            if (response.success && response.data != null) {
                tokenManager.saveAccessToken(response.data.accessToken)
                tokenManager.saveRefreshToken(response.data.refreshToken)
                Result.Success(UserDto(
                    id = response.data.userId,
                    phone = phone,
                    name = name,
                    email = email
                ))
            } else {
                Result.Error(ApiException.ServerError())
            }
        } catch (e: Exception) {
            Timber.e(e, "Registration failed")
            Result.Error(handleException(e))
        }
    }

    override fun getProfile(): Flow<Result<UserDto>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getProfile()
            if (response.success && response.data != null) {
                userDao.insertUser(mapper.toEntity(response.data))
                emit(Result.Success(response.data))
            }
        } catch (e: Exception) {
            emit(Result.Error(handleException(e)))
        }
    }

    override fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    override fun logout() {
        tokenManager.clearTokens()
        Timber.d("User logged out")
    }

    private fun handleException(e: Exception): ApiException {
        return when (e) {
            is ApiException -> e
            else -> ApiException.UnknownError(e.message ?: "Unknown error")
        }
    }
}

interface UserRepository {
    suspend fun login(phone: String, password: String): Result<UserDto>
    suspend fun register(phone: String, password: String, name: String, email: String?): Result<UserDto>
    fun getProfile(): Flow<Result<UserDto>>
    fun isLoggedIn(): Boolean
    fun logout()
}