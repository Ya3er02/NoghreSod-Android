package com.noghre.sod.data.repository

import com.noghre.sod.core.error.*
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 👤 User Repository Implementation
 * 
 * Manages user profile operations with comprehensive error handling.
 * All operations return Result<T> with proper error classification.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val exceptionHandler: GlobalExceptionHandler
) : UserRepository {

    /**
     * 🕦 Update user profile
     */
    override suspend fun updateProfile(
        fullName: String,
        email: String?,
        avatarUrl: String?,
    ): Result<User> {
        return try {
            Timber.d("[USER] Updating profile: $fullName")
            
            // Validate inputs
            if (fullName.isBlank()) {
                Timber.w("[USER] Invalid full name")
                return Result.Error(AppError.Validation(
                    message = "نام و نام خانوادگی الزامی است",
                    field = "fullName"
                ))
            }
            
            // TODO: Implement when API is ready
            Result.Error(AppError.Unknown(
                message = "امکان‌سازی این بخش برنامه انجام نشده"
            ))
        } catch (e: Exception) {
            Timber.e(e, "[USER] Update profile error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🔐 Change user password
     */
    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
    ): Result<Unit> {
        return try {
            Timber.d("[USER] Changing password")
            
            // Validate inputs
            if (currentPassword.isBlank()) {
                Timber.w("[USER] Current password is empty")
                return Result.Error(AppError.Validation(
                    message = "رمز عبور فعلی الزامی است",
                    field = "currentPassword"
                ))
            }
            
            if (newPassword.isBlank()) {
                Timber.w("[USER] New password is empty")
                return Result.Error(AppError.Validation(
                    message = "رمز عبور جدید الزامی است",
                    field = "newPassword"
                ))
            }
            
            if (newPassword.length < 6) {
                Timber.w("[USER] New password too short")
                return Result.Error(AppError.Validation(
                    message = "رمز عبور جدید حداقل 6 کاراکتر باید باشد",
                    field = "newPassword"
                ))
            }
            
            if (currentPassword == newPassword) {
                Timber.w("[USER] Passwords are the same")
                return Result.Error(AppError.Validation(
                    message = "رمز عبور جدید باید متفاوت باشد",
                    field = "newPassword"
                ))
            }
            
            // TODO: Implement when API is ready
            Result.Error(AppError.Unknown(
                message = "امکان‌سازی این بخش برنامه انجام نشده"
            ))
        } catch (e: Exception) {
            Timber.e(e, "[USER] Change password error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 👤 Get current user profile
     */
    override fun getCurrentUser(): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)
            Timber.d("[USER] Getting current user")
            
            val response = apiService.getUserProfile()
            
            if (response.isSuccessful) {
                if (response.data != null) {
                    Timber.d("[USER] User loaded: ${response.data.email}")
                    emit(Result.Success(response.data.toUser()))
                } else {
                    Timber.w("[USER] User profile response is empty")
                    emit(Result.Error(AppError.Network(
                        message = "پروفایل کاربر خالی است",
                        statusCode = 200
                    )))
                }
            } else {
                Timber.w("[USER] Get user failed: ${response.code()}")
                emit(Result.Error(when (response.code()) {
                    401 -> AppError.Authentication(
                        message = "لطفا دوباره وارد شوید",
                        reason = AuthFailureReason.TOKEN_EXPIRED
                    )
                    404 -> AppError.Network(
                        message = "نمایه کاربری نافت نشد",
                        statusCode = 404
                    )
                    else -> AppError.Network(
                        message = response.message ?: "تاب آبی پروفایل ناموفق",
                        statusCode = response.code()
                    )
                }))
            }
        } catch (e: Exception) {
            Timber.e(e, "[USER] Get user error")
            emit(Result.Error(exceptionHandler.handleException(e)))
        }
    }

    /**
     * 🗑️ Delete user account
     */
    override suspend fun deleteAccount(password: String): Result<Unit> {
        return try {
            Timber.d("[USER] Deleting account")
            
            // Validate input
            if (password.isBlank()) {
                Timber.w("[USER] Password not provided for deletion")
                return Result.Error(AppError.Validation(
                    message = "رمز عبور برای تأیید تاب آبی الزامی است",
                    field = "password"
                ))
            }
            
            // TODO: Implement when API is ready
            Result.Error(AppError.Unknown(
                message = "امکان‌سازی این بخش برنامه انجام نشده"
            ))
        } catch (e: Exception) {
            Timber.e(e, "[USER] Delete account error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 📂 Get user addresses
     */
    override fun getAddresses(): Flow<Result<List<com.noghre.sod.domain.model.Address>>> = flow {
        try {
            emit(Result.Loading)
            Timber.d("[USER] Getting addresses")
            
            val response = apiService.getShippingAddresses()
            
            if (response.isSuccessful) {
                if (response.data != null) {
                    val addresses = response.data.map { it.toAddress() }
                    Timber.d("[USER] Addresses loaded: ${addresses.size}")
                    emit(Result.Success(addresses))
                } else {
                    Timber.w("[USER] Addresses response is empty")
                    emit(Result.Success(emptyList()))
                }
            } else {
                Timber.w("[USER] Get addresses failed: ${response.code()}")
                emit(Result.Error(AppError.Network(
                    message = response.message ?: "تاب آبی آدرس‌ها ناموفق",
                    statusCode = response.code()
                )))
            }
        } catch (e: Exception) {
            Timber.e(e, "[USER] Get addresses error")
            emit(Result.Error(exceptionHandler.handleException(e)))
        }
    }

    /**
     * ➕ Add new address
     */
    override suspend fun addAddress(
        address: com.noghre.sod.domain.model.Address,
    ): Result<com.noghre.sod.domain.model.Address> {
        return try {
            Timber.d("[USER] Adding address: ${address.title}")
            
            // Validate inputs
            if (address.title.isBlank()) {
                Timber.w("[USER] Invalid address title")
                return Result.Error(AppError.Validation(
                    message = "عنوان آدرس الزامی است",
                    field = "title"
                ))
            }
            
            if (address.street.isBlank()) {
                Timber.w("[USER] Invalid street")
                return Result.Error(AppError.Validation(
                    message = "نام خيابان الزامی است",
                    field = "street"
                ))
            }
            
            if (address.city.isBlank()) {
                Timber.w("[USER] Invalid city")
                return Result.Error(AppError.Validation(
                    message = "نام شهر الزامی است",
                    field = "city"
                ))
            }
            
            // TODO: Implement when API is ready
            Result.Error(AppError.Unknown(
                message = "امکان‌سازی این بخش برنامه انجام نشده"
            ))
        } catch (e: Exception) {
            Timber.e(e, "[USER] Add address error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    // ============================================
    // 🔄 Mapper Functions
    // ============================================

    private fun com.noghre.sod.data.remote.dto.UserDto.toUser(): com.noghre.sod.domain.model.User {
        return com.noghre.sod.domain.model.User(
            id = id,
            email = email,
            phone = phone,
            firstName = firstName,
            lastName = lastName,
            profileImage = profileImage,
            membershipTier = membershipTier,
        )
    }

    private fun com.noghre.sod.data.remote.dto.AddressDto.toAddress(): com.noghre.sod.domain.model.Address {
        return com.noghre.sod.domain.model.Address(
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
}