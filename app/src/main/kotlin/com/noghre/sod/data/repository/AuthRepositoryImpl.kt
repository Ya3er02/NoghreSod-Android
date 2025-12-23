package com.noghre.sod.data.repository

import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.remote.dto.LoginRequestDto
import com.noghre.sod.data.remote.dto.RegisterRequestDto
import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.*
import com.noghre.sod.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : AuthRepository {

    override suspend fun register(
        email: String,
        phone: String,
        password: String,
        firstName: String,
        lastName: String,
    ): Result<AuthToken> {
        return try {
            val request = RegisterRequestDto(email, phone, password, firstName, lastName)
            val response = apiService.register(request)
            if (response.success && response.data != null) {
                Result.Success(response.data.toAuthToken())
            } else {
                Result.Error(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<AuthToken> {
        return try {
            val request = LoginRequestDto(email, password)
            val response = apiService.login(request)
            if (response.success && response.data != null) {
                Result.Success(response.data.toAuthToken())
            } else {
                Result.Error(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun loginWithPhone(phone: String, code: String): Result<AuthToken> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun requestOTP(phone: String): Result<Unit> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun verifyOTP(phone: String, code: String): Result<Boolean> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<AuthToken> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val response = apiService.logout()
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun isLoggedIn(): Flow<Result<Boolean>> = flow {
        emit(Result.Success(true)) // TODO: Check token existence
    }

    override fun getCurrentUser(): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getUserProfile()
            if (response.success && response.data != null) {
                emit(Result.Success(response.data.toUser()))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun updateProfile(
        firstName: String?,
        lastName: String?,
        profileImage: String?,
        bio: String?,
        birthDate: String?,
    ): Result<User> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun addShippingAddress(address: Address): Result<Address> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateShippingAddress(addressId: String, address: Address): Result<Address> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteShippingAddress(addressId: String): Result<Unit> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getShippingAddresses(): Flow<Result<List<Address>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getShippingAddresses()
            if (response.success && response.data != null) {
                emit(Result.Success(response.data.map { it.toAddress() }))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun setDefaultShippingAddress(addressId: String): Result<Unit> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updatePreferences(preferences: UserPreferences): Result<UserPreferences> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun enableTwoFactor(): Result<TwoFactorSetup> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun disableTwoFactor(code: String): Result<Unit> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun verifyTwoFactorCode(code: String): Result<Boolean> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteAccount(password: String): Result<Unit> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getSecuritySettings(): Flow<Result<SecuritySettings>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getSecuritySettings()
            if (response.success && response.data != null) {
                emit(Result.Success(response.data.toSecuritySettings()))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    // Mapper functions
    private fun com.noghre.sod.data.remote.dto.AuthTokenDto.toAuthToken(): AuthToken {
        return AuthToken(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = expiresIn,
            user = user.toUser(),
        )
    }

    private fun com.noghre.sod.data.remote.dto.UserDto.toUser(): User {
        return User(
            id = id,
            email = email,
            phone = phone,
            firstName = firstName,
            lastName = lastName,
            profileImage = profileImage,
            membershipTier = membershipTier,
        )
    }

    private fun com.noghre.sod.data.remote.dto.AddressDto.toAddress(): Address {
        return Address(
            id = id ?: "",
            title = title,
            recipientName = recipientName,
            phone = phone,
            province = province,
            city = city,
            street = street,
            postalCode = postalCode,
            isDefault = isDefault,
        )
    }

    private fun com.noghre.sod.data.remote.dto.SecuritySettingsDto.toSecuritySettings(): SecuritySettings {
        return SecuritySettings(
            twoFactorEnabled = twoFactorEnabled,
            activeDevices = activeDevices.map {
                DeviceInfo(it.id, it.name, it.type, java.time.LocalDateTime.now(), false)
            },
        )
    }
}
