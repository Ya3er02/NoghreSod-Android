package com.noghre.sod.domain.usecase.profile

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.repository.UserRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UpdateProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) : UseCase<UpdateProfileUseCase.Params, User>() {

    data class Params(
        val fullName: String,
        val email: String? = null,
        val avatarUrl: String? = null
    )

    override suspend fun execute(params: Params): User {
        if (params.fullName.isEmpty() || params.fullName.length < 3) {
            throw IllegalArgumentException("نام حداقل 3 کاراکتر باید باشد")
        }
        return userRepository.updateProfile(params.fullName, params.email, params.avatarUrl)
    }
}
