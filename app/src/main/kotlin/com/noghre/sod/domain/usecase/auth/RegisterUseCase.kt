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
        // Validate phone
        if (!params.phone.matches(Regex("^09\\d{9}$"))) {
            throw IllegalArgumentException("شماره موبایل نامعتبر")
        }
        
        // Validate full name
        if (params.fullName.isEmpty() || params.fullName.length < 3) {
            throw IllegalArgumentException("نام حداقل 3 کاراکتر باید باشد")
        }
        
        // Validate password
        if (params.password.length < 8) {
            throw IllegalArgumentException("رمز عبور حداقل 8 کاراکتر باید باشد")
        }
        
        return authRepository.register(
            phone = params.phone,
            fullName = params.fullName,
            password = params.password
        )
    }
}
