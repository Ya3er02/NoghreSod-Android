package com.noghre.sod.domain.usecase.auth

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.repository.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<Unit, Unit>() {

    override suspend fun execute(params: Unit) {
        authRepository.logout()
    }
}
