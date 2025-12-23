package com.noghre.sod.domain.usecase.notification

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.repository.NotificationRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class RegisterFcmTokenUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) : UseCase<RegisterFcmTokenUseCase.Params, Unit>() {

    data class Params(val token: String)

    override suspend fun execute(params: Params) {
        if (params.token.isEmpty()) {
            throw IllegalArgumentException("FCM token cannot be empty")
        }
        notificationRepository.registerToken(params.token)
    }
}
