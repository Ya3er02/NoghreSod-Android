package com.noghre.sod.domain.usecase.auth

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.AuthResult
import com.noghre.sod.domain.repository.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<RegisterUseCase.Params, AuthResult>() {

    data class Params(
        val phone: String,
        val fullName: String,
        val password: String
    )

    override suspend fun execute(params: Params): AuthResult {
        // Validate inputs
        if (!isValidPhone(params.phone)) {
            throw IllegalArgumentException("شماره تلفن نامعتبر است")
        }
        if (params.fullName.length < 3) {
            throw IllegalArgumentException("نام حداقل 3 کاراکتر باید باشد")
        }
        if (!isValidPassword(params.password)) {
            throw IllegalArgumentException("رمز عبور حداقل 8 کاراکتر و شامل حروف و اعداد باید باشد")
        }

        // Call repository
        return authRepository.register(params.phone, params.fullName, params.password)
    }

    private fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^09\\d{9}$"))
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isLetter() } &&
                password.any { it.isDigit() }
    }
}
