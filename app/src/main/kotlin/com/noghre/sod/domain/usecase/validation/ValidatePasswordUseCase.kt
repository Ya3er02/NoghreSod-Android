package com.noghre.sod.domain.usecase.validation

import com.noghre.sod.core.util.AppError
import com.noghre.sod.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Validates password according to security requirements.
 * Part of Authentication domain logic.
 * 
 * Business Rules:
 * - Password must not be empty
 * - Minimum length: 6 characters
 * - Maximum length: 128 characters
 * - Must contain at least one letter and one number (recommended)
 */
class ValidatePasswordUseCase @Inject constructor(
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : UseCase<String, Unit>(dispatcher) {
    
    companion object {
        private const val MIN_LENGTH = 6
        private const val MAX_LENGTH = 128
    }
    
    override suspend fun execute(params: String): Unit {
        val password = params
        
        when {
            password.isBlank() -> throw AppError.Validation(
                message = "رمز عبور نمی‌تواند خالی باشد",
                fieldName = "password"
            )
            password.length < MIN_LENGTH -> throw AppError.Validation(
                message = "رمز عبور باید حداقل $MIN_LENGTH کاراکتر باشد",
                fieldName = "password"
            )
            password.length > MAX_LENGTH -> throw AppError.Validation(
                message = "رمز عبور نمی‌تواند بیشتر از $MAX_LENGTH کاراکتر باشد",
                fieldName = "password"
            )
        }
    }
}

/**
 * Validates password confirmation matches original password.
 * 
 * Business Rules:
 * - Both passwords must match exactly
 * - Case-sensitive comparison
 */
class ValidatePasswordConfirmationUseCase @Inject constructor(
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : UseCase<ValidatePasswordConfirmationUseCase.Params, Unit>(dispatcher) {
    
    data class Params(
        val password: String,
        val confirmPassword: String
    )
    
    override suspend fun execute(params: Params): Unit {
        when {
            params.password != params.confirmPassword -> throw AppError.Validation(
                message = "رمز عبور و تأیید آن یکسان نیست",
                fieldName = "confirmPassword"
            )
        }
    }
}
