package com.noghre.sod.domain.usecase.auth

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.AuthToken
import com.noghre.sod.domain.repository.AuthRepository
import javax.inject.Inject

data class RegisterParams(
    val email: String,
    val phone: String,
    val password: String,
    val firstName: String,
    val lastName: String,
)

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) : UseCase<RegisterParams, AuthToken>() {

    override suspend fun execute(parameters: RegisterParams): Result<AuthToken> {
        return authRepository.register(
            parameters.email,
            parameters.phone,
            parameters.password,
            parameters.firstName,
            parameters.lastName
        )
    }
}
