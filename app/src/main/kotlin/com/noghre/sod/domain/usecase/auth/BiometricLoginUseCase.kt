package com.noghre.sod.domain.usecase.auth

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.AuthResult
import com.noghre.sod.domain.repository.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class BiometricLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<Unit, AuthResult>() {

    override suspend fun execute(params: Unit): AuthResult {
        val credentials = authRepository.getBiometricCredentials()
            ?: throw IllegalStateException("هیچ معیار زبی نا شد")
        
        return authRepository.login(credentials.phone, credentials.password)
    }
}
