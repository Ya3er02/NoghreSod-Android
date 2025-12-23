package com.noghre.sod.domain.usecase.auth

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.AuthResult
import com.noghre.sod.domain.repository.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<LoginUseCase.Params, AuthResult>() {

    data class Params(
        val phone: String,
        val password: String
    )

    override suspend fun execute(params: Params): AuthResult {
        // Validate phone format (Iran: 09XXXXXXXXX)
        if (!isValidPhone(params.phone)) {
            throw IllegalArgumentException("شماره تلفن نامعتبر است")
        }

        // Validate password
        if (!isValidPassword(params.password)) {
            throw IllegalArgumentException("رمز عبور حداقل 8 کاراکتر باید باشد")
        }

        // Call repository
        return authRepository.login(params.phone, params.password)
    }

    private fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^09\\d{9}$"))
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }
}
