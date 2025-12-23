package com.noghre.sod.data.repository

import com.noghre.sod.domain.repository.NotificationRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {
    override suspend fun registerToken(token: String) {
        // TODO: Implement
    }

    override suspend fun unregisterToken() {
        // TODO: Implement
    }

    override suspend fun updateTokenIfNeeded(newToken: String) {
        // TODO: Implement
    }
}
