package com.noghre.sod.domain.usecase.auth

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.repository.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class VerifyOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<VerifyOtpUseCase.Params, Unit>() {

    data class Params(
        val phone: String,
        val code: String
    )

    override suspend fun execute(params: Params) {
        // Validate OTP code (4-6 digits)
        if (!params.code.matches(Regex("^\\d{4,6}$"))) {
            throw IllegalArgumentException("کد اعتبار سنجی 4-6 رقم باید باشد")
        }
        
        authRepository.verifyOtp(params.phone, params.code)
    }
}
