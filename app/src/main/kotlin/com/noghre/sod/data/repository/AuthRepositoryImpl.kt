package com.noghre.sod.data.repository

import com.noghre.sod.core.error.*
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.remote.dto.LoginRequestDto
import com.noghre.sod.data.remote.dto.RegisterRequestDto
import com.noghre.sod.domain.model.*
import com.noghre.sod.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

/**
 * 🔐 Authentication Repository Implementation
 * 
 * Handles user authentication and profile management with comprehensive error handling.
 * All operations return Result<T> with proper error classification.
 */
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val exceptionHandler: GlobalExceptionHandler
) : AuthRepository {

    /**
     * 📋 User registration
     */
    override suspend fun register(
        email: String,
        phone: String,
        password: String,
        firstName: String,
        lastName: String,
    ): Result<AuthToken> {
        return try {
            Timber.d("[AUTH] Registering user: $email")
            
            // Validate inputs
            if (email.isBlank()) {
                Timber.w("[AUTH] Invalid email")
                return Result.Error(AppError.Validation(
                    message = "رایانامه الکترونیکی ضحیح نیست",
                    field = "email"
                ))
            }
            
            if (password.length < 6) {
                Timber.w("[AUTH] Password too short")
                return Result.Error(AppError.Validation(
                    message = "رمز عبور حداقل 6 کاراکتر باشد",
                    field = "password"
                ))
            }
            
            val request = RegisterRequestDto(email, phone, password, firstName, lastName)
            val response = apiService.register(request)
            
            if (response.isSuccessful) {
                if (response.data != null) {
                    Timber.d("[AUTH] Registration successful")
                    Result.Success(response.data.toAuthToken())
                } else {
                    Timber.w("[AUTH] Registration response empty")
                    Result.Error(AppError.Network(
                        message = "پاسخ سرور نامعتبر",
                        statusCode = 200
                    ))
                }
            } else {
                Timber.w("[AUTH] Registration failed: ${response.code()}")
                Result.Error(when (response.code()) {
                    400 -> AppError.Validation(
                        message = response.message ?: "اطلاعات وارد شده نامعتبر است",
                        field = "registration"
                    )
                    409 -> AppError.Authentication(
                        message = "حسابی با این رایانامه قبلا ثبت نام شده است",
                        reason = AuthFailureReason.UNKNOWN
                    )
                    else -> AppError.Network(
                        message = response.message ?: "ثبت نام ام رکارد ناموفق",
                        statusCode = response.code()
                    )
                })
            }
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Registration error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🗓️ User login
     */
    override suspend fun login(email: String, password: String): Result<AuthToken> {
        return try {
            Timber.d("[AUTH] Logging in: $email")
            
            if (email.isBlank() || password.isBlank()) {
                Timber.w("[AUTH] Missing email or password")
                return Result.Error(AppError.Validation(
                    message = "رایانامه و رمز تالایتو تر مهم هستند",
                    field = "credentials"
                ))
            }
            
            val request = LoginRequestDto(email, password)
            val response = apiService.login(request)
            
            if (response.isSuccessful) {
                if (response.data != null) {
                    Timber.d("[AUTH] Login successful")
                    Result.Success(response.data.toAuthToken())
                } else {
                    Timber.w("[AUTH] Login response empty")
                    Result.Error(AppError.Network(
                        message = "پاسخ سرور نامعتبر",
                        statusCode = 200
                    ))
                }
            } else {
                Timber.w("[AUTH] Login failed: ${response.code()}")
                Result.Error(when (response.code()) {
                    401 -> AppError.Authentication(
                        message = "نام کاربری یا رمز عبور اشتباه است",
                        reason = AuthFailureReason.INVALID_CREDENTIALS
                    )
                    403 -> AppError.Authentication(
                        message = "حساب کاربری الڧ شده است",
                        reason = AuthFailureReason.ACCOUNT_LOCKED
                    )
                    else -> AppError.Network(
                        message = response.message ?: "ورود ناموفق",
                        statusCode = response.code()
                    )
                })
            }
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Login error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 💵 Login with phone
     */
    override suspend fun loginWithPhone(phone: String, code: String): Result<AuthToken> {
        return try {
            Timber.d("[AUTH] Phone login: $phone")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Phone login error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 📅 Request OTP
     */
    override suspend fun requestOTP(phone: String): Result<Unit> {
        return try {
            Timber.d("[AUTH] Requesting OTP: $phone")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] OTP request error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * ✅ Verify OTP
     */
    override suspend fun verifyOTP(phone: String, code: String): Result<Boolean> {
        return try {
            Timber.d("[AUTH] Verifying OTP: $phone")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] OTP verification error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🔄 Refresh authentication token
     */
    override suspend fun refreshToken(refreshToken: String): Result<AuthToken> {
        return try {
            Timber.d("[AUTH] Refreshing token")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Token refresh error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🚪 Logout
     */
    override suspend fun logout(): Result<Unit> {
        return try {
            Timber.d("[AUTH] Logging out")
            
            val response = apiService.logout()
            if (response.isSuccessful) {
                Timber.d("[AUTH] Logout successful")
                Result.Success(Unit)
            } else {
                Timber.w("[AUTH] Logout failed: ${response.code()}")
                Result.Error(AppError.Network(
                    message = response.message ?: "خروج ناموفق",
                    statusCode = response.code()
                ))
            }
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Logout error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * ✅ Check if user is logged in
     */
    override fun isLoggedIn(): Flow<Result<Boolean>> = flow {
        try {
            Timber.d("[AUTH] Checking login status")
            emit(Result.Success(true)) // TODO: Check token existence
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Check login error")
            emit(Result.Error(exceptionHandler.handleException(e)))
        }
    }

    /**
     * 👤 Get current user
     */
    override fun getCurrentUser(): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)
            Timber.d("[AUTH] Loading current user")
            
            val response = apiService.getUserProfile()
            if (response.isSuccessful) {
                if (response.data != null) {
                    Timber.d("[AUTH] User loaded: ${response.data.email}")
                    emit(Result.Success(response.data.toUser()))
                } else {
                    Timber.w("[AUTH] User profile response empty")
                    emit(Result.Error(AppError.Network(
                        message = "پروفایل کاربر خالی است",
                        statusCode = 200
                    )))
                }
            } else {
                Timber.w("[AUTH] Get user failed: ${response.code()}")
                emit(Result.Error(AppError.Network(
                    message = response.message ?: "تاب آبی کاربر ناموفق",
                    statusCode = response.code()
                )))
            }
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Get user error")
            emit(Result.Error(exceptionHandler.handleException(e)))
        }
    }

    /**
     * 🕦 Update user profile
     */
    override suspend fun updateProfile(
        firstName: String?,
        lastName: String?,
        profileImage: String?,
        bio: String?,
        birthDate: String?,
    ): Result<User> {
        return try {
            Timber.d("[AUTH] Updating profile")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Profile update error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🔐 Change password
     */
    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            Timber.d("[AUTH] Changing password")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Password change error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🗑️ Request password reset
     */
    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            Timber.d("[AUTH] Requesting password reset: $email")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Password reset request error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🔠 Reset password
     */
    override suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> {
        return try {
            Timber.d("[AUTH] Resetting password: $email")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Password reset error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * ➕ Add shipping address
     */
    override suspend fun addShippingAddress(address: Address): Result<Address> {
        return try {
            Timber.d("[AUTH] Adding address: ${address.title}")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Add address error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * ✍️ Update shipping address
     */
    override suspend fun updateShippingAddress(addressId: String, address: Address): Result<Address> {
        return try {
            Timber.d("[AUTH] Updating address: $addressId")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Update address error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🗑️ Delete shipping address
     */
    override suspend fun deleteShippingAddress(addressId: String): Result<Unit> {
        return try {
            Timber.d("[AUTH] Deleting address: $addressId")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Delete address error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 📂 Get shipping addresses
     */
    override fun getShippingAddresses(): Flow<Result<List<Address>>> = flow {
        try {
            emit(Result.Loading)
            Timber.d("[AUTH] Loading addresses")
            
            val response = apiService.getShippingAddresses()
            if (response.isSuccessful) {
                if (response.data != null) {
                    val addresses = response.data.map { it.toAddress() }
                    Timber.d("[AUTH] Addresses loaded: ${addresses.size}")
                    emit(Result.Success(addresses))
                } else {
                    Timber.w("[AUTH] Addresses response empty")
                    emit(Result.Success(emptyList()))
                }
            } else {
                Timber.w("[AUTH] Get addresses failed: ${response.code()}")
                emit(Result.Error(AppError.Network(
                    message = response.message ?: "تاب آبی آدرس ناموفق",
                    statusCode = response.code()
                )))
            }
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Get addresses error")
            emit(Result.Error(exceptionHandler.handleException(e)))
        }
    }

    /**
     * 🎯 Set default shipping address
     */
    override suspend fun setDefaultShippingAddress(addressId: String): Result<Unit> {
        return try {
            Timber.d("[AUTH] Setting default address: $addressId")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Set default address error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 📄 Update user preferences
     */
    override suspend fun updatePreferences(preferences: UserPreferences): Result<UserPreferences> {
        return try {
            Timber.d("[AUTH] Updating preferences")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Update preferences error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🔐 Enable two-factor authentication
     */
    override suspend fun enableTwoFactor(): Result<TwoFactorSetup> {
        return try {
            Timber.d("[AUTH] Enabling two-factor")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Enable 2FA error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🔒 Disable two-factor authentication
     */
    override suspend fun disableTwoFactor(code: String): Result<Unit> {
        return try {
            Timber.d("[AUTH] Disabling two-factor")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Disable 2FA error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * ✅ Verify two-factor code
     */
    override suspend fun verifyTwoFactorCode(code: String): Result<Boolean> {
        return try {
            Timber.d("[AUTH] Verifying 2FA code")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] 2FA verification error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🗑️ Delete account
     */
    override suspend fun deleteAccount(password: String): Result<Unit> {
        return try {
            Timber.d("[AUTH] Deleting account")
            Result.Error(AppError.Unknown("Not yet implemented"))
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Delete account error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🔐 Get security settings
     */
    override fun getSecuritySettings(): Flow<Result<SecuritySettings>> = flow {
        try {
            emit(Result.Loading)
            Timber.d("[AUTH] Loading security settings")
            
            val response = apiService.getSecuritySettings()
            if (response.isSuccessful) {
                if (response.data != null) {
                    Timber.d("[AUTH] Security settings loaded")
                    emit(Result.Success(response.data.toSecuritySettings()))
                } else {
                    Timber.w("[AUTH] Security settings response empty")
                    emit(Result.Error(AppError.Network(
                        message = "تنظیمات امنیتی خالی است",
                        statusCode = 200
                    )))
                }
            } else {
                Timber.w("[AUTH] Get security settings failed: ${response.code()}")
                emit(Result.Error(AppError.Network(
                    message = response.message ?: "تاب آبی تنظیمات ناموفق",
                    statusCode = response.code()
                )))
            }
        } catch (e: Exception) {
            Timber.e(e, "[AUTH] Get security settings error")
            emit(Result.Error(exceptionHandler.handleException(e)))
        }
    }

    // ============================================
    // 🔄 Mapper Functions
    // ============================================

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