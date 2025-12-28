package com.noghre.sod.domain.usecase.validation

import com.noghre.sod.core.util.AppError
import com.noghre.sod.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Validates email format and non-emptiness.
 * Part of Authentication domain logic.
 * 
 * Business Rules:
 * - Email must not be empty
 * - Email must be in valid format (contains @ and domain)
 * - Email must not exceed 254 characters (RFC 5321)
 */
class ValidateEmailUseCase @Inject constructor(
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : UseCase<String, Unit>(dispatcher) {
    
    override suspend fun execute(params: String): Unit {
        val email = params.trim()
        
        when {
            email.isBlank() -> throw AppError.Validation(
                message = "ایمیل نمی‌تواند خالی باشد",
                fieldName = "email"
            )
            email.length > 254 -> throw AppError.Validation(
                message = "ایمیل نمی‌تواند بیشتر از 254 کاراکتر باشد",
                fieldName = "email"
            )
            !isValidEmailFormat(email) -> throw AppError.Validation(
                message = "فرمت ایمیل معتبر نیست",
                fieldName = "email"
            )
        }
    }
    
    /**
     * Simple email validation using regex.
     * RFC 5322 compliant (simplified version).
     */
    private fun isValidEmailFormat(email: String): Boolean {
        val emailRegex = Regex(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
        )
        return emailRegex.matches(email)
    }
}
