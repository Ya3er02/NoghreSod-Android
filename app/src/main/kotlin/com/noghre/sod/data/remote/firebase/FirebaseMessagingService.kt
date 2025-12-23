package com.noghre.sod.data.remote.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.noghre.sod.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NoghreSodMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Timber.d("Message received from: ${remoteMessage.from}")

        // Handle data message
        if (remoteMessage.data.isNotEmpty()) {
            handleDataMessage(remoteMessage.data)
        }

        // Handle notification message
        remoteMessage.notification?.let {
            handleNotificationMessage(
                title = it.title ?: "",
                body = it.body ?: "",
                data = remoteMessage.data
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("New FCM Token: $token")
        // Send token to server
        sendTokenToServer(token)
    }

    private fun handleDataMessage(data: Map<String, String>) {
        Timber.d("Data message: $data")
        // Handle data-only messages
    }

    private fun handleNotificationMessage(
        title: String,
        body: String,
        data: Map<String, String>
    ) {
        val notificationType = data["type"] ?: "general"
        val channelId = when (notificationType) {
            "order" -> "orders"
            "promotion" -> "promotions"
            "payment" -> "payments"
            else -> "general"
        }

        notificationHelper.showNotification(
            title = title,
            body = body,
            channelId = channelId,
            data = data
        )
    }

    private fun sendTokenToServer(token: String) {
        // Implementation in repository
        Timber.d("Token sent to server: $token")
    }
}
