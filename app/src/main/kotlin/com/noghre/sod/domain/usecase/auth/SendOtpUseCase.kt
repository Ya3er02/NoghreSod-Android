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
        if (!isValidPhone(params.phone)) {
            throw IllegalArgumentException("شماره تلفن نامعتبر است")
        }
        authRepository.sendOtp(params.phone)
    }

    private fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^09\\d{9}$"))
    }
}
