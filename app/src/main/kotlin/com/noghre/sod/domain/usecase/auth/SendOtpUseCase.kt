package com.noghre.sod.domain.usecase.auth

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.repository.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<SendOtpUseCase.Params, Unit>() {

    data class Params(val phone: String)

    override suspend fun execute(params: Params) {
        // Validate phone
        if (!params.phone.matches(Regex("^09\\d{9}$"))) {
            throw IllegalArgumentException("شماره موبایل نامعتبر")
        }
        
        authRepository.sendOtp(params.phone)
    }
}
