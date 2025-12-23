package com.noghre.sod.domain.usecase.auth

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.AuthResult
import com.noghre.sod.domain.repository.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class VerifyOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<VerifyOtpUseCase.Params, AuthResult>() {

    data class Params(
        val phone: String,
        val code: String
    )

    override suspend fun execute(params: Params): AuthResult {
        if (!isValidCode(params.code)) {
            throw IllegalArgumentException("کد OTP باید 4-6 رقم باشد")
        }
        return authRepository.verifyOtp(params.phone, params.code)
    }

    private fun isValidCode(code: String): Boolean {
        return code.matches(Regex("^\\d{4,6}$"))
    }
}
