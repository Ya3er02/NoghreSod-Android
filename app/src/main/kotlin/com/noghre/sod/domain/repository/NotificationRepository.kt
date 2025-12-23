package com.noghre.sod.domain.repository

interface NotificationRepository {
    suspend fun registerToken(token: String)
    suspend fun unregisterToken()
    suspend fun updateTokenIfNeeded(newToken: String)
}
