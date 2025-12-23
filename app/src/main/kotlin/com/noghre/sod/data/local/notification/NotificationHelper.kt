package com.noghre.sod.data.local.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.noghre.sod.R
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Orders channel
            val ordersChannel = NotificationChannel(
                "orders",
                "Order Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "اطلاعات سفارشات"
            }

            // Promotions channel
            val promotionsChannel = NotificationChannel(
                "promotions",
                "Promotions",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "تبلیغات تخفیفات"
            }

            // Payments channel
            val paymentsChannel = NotificationChannel(
                "payments",
                "Payment Confirmations",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تأیید پرداختها"
            }

            // General channel
            val generalChannel = NotificationChannel(
                "general",
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannels(
                listOf(ordersChannel, promotionsChannel, paymentsChannel, generalChannel)
            )
        }
    }

    fun showNotification(
        title: String,
        body: String,
        channelId: String = "general",
        data: Map<String, String>? = null,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        try {
            val notification = NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .build()

            notificationManager.notify(notificationId, notification)
        } catch (e: Exception) {
            Timber.e(e, "Error showing notification")
        }
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}
