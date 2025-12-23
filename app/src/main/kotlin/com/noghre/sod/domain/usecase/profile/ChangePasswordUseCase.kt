package com.noghre.sod.domain.usecase.profile

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.repository.UserRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ChangePasswordUseCase @Inject constructor(
    private val userRepository: UserRepository
) : UseCase<ChangePasswordUseCase.Params, Unit>() {

    data class Params(
        val currentPassword: String,
        val newPassword: String
    )

    override suspend fun execute(params: Params) {
        if (params.newPassword.length < 8) {
            throw IllegalArgumentException("رمز عبور جدید حداقل 8 کاراکتر باید باشد")
        }
        if (params.currentPassword == params.newPassword) {
            throw IllegalArgumentException("رمز عبور جدید نباید با رمز قدیم یکسان باشد")
        }
        userRepository.changePassword(params.currentPassword, params.newPassword)
    }
}
