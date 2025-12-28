package com.noghre.sod.domain.usecase.profile

import com.noghre.sod.core.util.AppError
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.repository.UserRepository
import com.noghre.sod.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Retrieves user profile information.
 * Part of Profile Management domain logic.
 */
class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Unit, User>(dispatcher) {
    
    override suspend fun execute(params: Unit): User {
        return userRepository.getUserProfile()
            .getOrThrow()
    }
}

/**
 * Updates user profile information.
 * Part of Profile Management domain logic.
 * 
 * Business Rules:
 * - Name must not be empty
 * - Phone number must be valid Iranian format
 * - Email format must be valid
 */
class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<UpdateUserProfileUseCase.Params, User>(dispatcher) {
    
    data class Params(
        val name: String?,
        val email: String?,
        val phoneNumber: String?,
        val address: String?,
        val city: String?,
        val postalCode: String?
    )
    
    override suspend fun execute(params: Params): User {
        when {
            params.name?.isBlank() == true -> throw AppError.Validation(
                message = "نام نمی‌تواند خالی باشد",
                fieldName = "name"
            )
        }
        
        return userRepository.updateUserProfile(
            name = params.name,
            email = params.email,
            phoneNumber = params.phoneNumber,
            address = params.address,
            city = params.city,
            postalCode = params.postalCode
        ).getOrThrow()
    }
}

/**
 * Updates user profile image.
 * Part of Profile Management domain logic.
 */
class UpdateProfileImageUseCase @Inject constructor(
    private val userRepository: UserRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<String, User>(dispatcher) {
    
    /**
     * @param imagePath Local file path or remote URL of image
     */
    override suspend fun execute(params: String): User {
        return userRepository.updateProfileImage(params)
            .getOrThrow()
    }
}

/**
 * Deletes user account permanently.
 * Part of Profile Management domain logic.
 * 
 * Business Rules:
 * - Action is irreversible
 * - Password confirmation required
 * - All user data will be deleted
 */
class DeleteAccountUseCase @Inject constructor(
    private val userRepository: UserRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<String, Unit>(dispatcher) {
    
    /**
     * @param password Password confirmation for security
     */
    override suspend fun execute(params: String): Unit {
        return userRepository.deleteAccount(params)
            .getOrThrow()
    }
}
