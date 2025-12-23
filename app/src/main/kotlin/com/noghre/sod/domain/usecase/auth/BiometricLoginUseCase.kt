package com.noghre.sod.domain.usecase.auth

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.repository.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class BiometricLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<Unit, com.noghre.sod.domain.model.AuthResult>() {

    override suspend fun execute(params: Unit): com.noghre.sod.domain.model.AuthResult {
        val biometricData = authRepository.getBiometricCredentials()
            ?: throw IllegalStateException("بیومتری فعال نیست")

        return authRepository.biometricLogin(biometricData.phone, biometricData.password)
    }
}
