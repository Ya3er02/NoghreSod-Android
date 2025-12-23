package com.noghre.sod.domain.repository

import com.noghre.sod.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun registerToken(token: String)

    fun getNotifications(): Flow<List<Notification>>

    suspend fun markAsRead(notificationId: String)

    suspend fun deleteNotification(notificationId: String)

    suspend fun markAllAsRead()
}
