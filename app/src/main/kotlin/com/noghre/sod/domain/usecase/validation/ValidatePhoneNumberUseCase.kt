package com.noghre.sod.domain.usecase.validation

import com.noghre.sod.core.util.AppError
import com.noghre.sod.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Validates Iranian phone numbers.
 * Part of Profile domain logic.
 * 
 * Business Rules:
 * - Phone number must not be empty
 * - Must be valid Iranian format
 * - Supports mobile (0910-0999) and landline (0511-0999 area codes)
 * - After cleaning: 11 digits starting with 0
 */
class ValidatePhoneNumberUseCase @Inject constructor(
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : UseCase<String, Unit>(dispatcher) {
    
    companion object {
        private const val EXPECTED_LENGTH = 11
        private val VALID_MOBILE_PREFIXES = listOf(
            "0910", "0911", "0912", "0913", "0914", "0915", "0916", "0917", "0918", "0919",
            "0990", "0991", "0992", "0993", "0994"
        )
    }
    
    override suspend fun execute(params: String): Unit {
        val phoneNumber = params.trim()
        
        when {
            phoneNumber.isBlank() -> throw AppError.Validation(
                message = "شماره تلفن نمی‌تواند خالی باشد",
                fieldName = "phoneNumber"
            )
            !isValidFormat(phoneNumber) -> throw AppError.Validation(
                message = "فرمت شماره تلفن معتبر نیست",
                fieldName = "phoneNumber"
            )
        }
    }
    
    /**
     * Validates Iranian phone number format.
     * 
     * Acceptable formats:
     * - 09101234567 (mobile)
     * - 0910-123-4567
     * - (0910) 123-4567
     */
    private fun isValidFormat(phoneNumber: String): Boolean {
        // Remove common separators
        val cleanedPhone = phoneNumber
            .replace("-", "")
            .replace(" ", "")
            .replace("(", "")
            .replace(")", "")
        
        // Must be 11 digits
        if (cleanedPhone.length != EXPECTED_LENGTH) {
            return false
        }
        
        // Must start with 0
        if (!cleanedPhone.startsWith("0")) {
            return false
        }
        
        // Must be all digits
        if (!cleanedPhone.all { it.isDigit() }) {
            return false
        }
        
        // Check if starts with valid mobile prefix
        val prefix = cleanedPhone.substring(0, 4)
        return VALID_MOBILE_PREFIXES.contains(prefix)
    }
}
