package com.noghre.sod.domain.usecase.auth

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.AuthToken
import com.noghre.sod.domain.repository.AuthRepository
import javax.inject.Inject

data class LoginParams(
    val email: String,
    val password: String,
)

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) : UseCase<LoginParams, AuthToken>() {

    override suspend fun execute(parameters: LoginParams): Result<AuthToken> {
        return authRepository.login(parameters.email, parameters.password)
    }
}
