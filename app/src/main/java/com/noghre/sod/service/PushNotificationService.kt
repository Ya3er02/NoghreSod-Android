package com.noghre.sod.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.noghre.sod.MainActivity
import com.noghre.sod.R
import android.util.Log

/**
 * Firebase Cloud Messaging Service
 * 
 * Handles incoming push notifications for:
 * - Order status updates
 * - Special offers and promotions
 * - System notifications
 * - General announcements
 * 
 * @author Noghre Sod App
 * @version 1.0.0
 */
class PushNotificationService : FirebaseMessagingService() {
    
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "noghresod_notifications"
        private const val TAG = "PushNotificationService"
    }
    
    /**
     * Called when service is first created.
     * Ensures notification channel is created for Android 8+
     */
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    /**
     * Called when a message is received from Firebase Cloud Messaging
     * 
     * @param remoteMessage The message received from FCM
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Message received from: ${remoteMessage.from}")
        
        // Extract notification data
        val title = remoteMessage.notification?.title ?: getString(R.string.app_name)
        val body = remoteMessage.notification?.body ?: ""
        val orderId = remoteMessage.data["order_id"]
        val notificationType = remoteMessage.data["type"] ?: "general"
        val actionUrl = remoteMessage.data["action_url"]
        
        Log.d(TAG, "Notification Title: $title")
        Log.d(TAG, "Notification Body: $body")
        Log.d(TAG, "Type: $notificationType")
        
        // Show notification
        sendNotification(title, body, notificationType, orderId, actionUrl)
    }
    
    /**
     * Called when a new token is generated for this device.
     * This token should be sent to your backend to enable sending targeted messages.
     * 
     * @param token The new FCM token
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        
        // Send token to backend
        sendTokenToBackend(token)
    }
    
    /**
     * Displays a notification to the user
     * 
     * @param title Notification title
     * @param message Notification message body
     * @param type Type of notification ("order", "promotion", "system", etc.)
     * @param orderId Associated order ID (if any)
     * @param actionUrl URL to open when notification is tapped (if any)
     */
    private fun sendNotification(
        title: String,
        message: String,
        type: String = "general",
        orderId: String? = null,
        actionUrl: String? = null
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create intent for when user taps notification
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (!orderId.isNullOrEmpty()) {
                putExtra("order_id", orderId)
            }
            if (!actionUrl.isNullOrEmpty()) {
                putExtra("action_url", actionUrl)
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notificationId = System.currentTimeMillis().toInt()
        
        // Build notification
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .apply {
                // Set different colors based on notification type
                when (type) {
                    "order" -> setColor(0xFF4CAF50.toInt()) // Green for orders
                    "promotion" -> setColor(0xFFFF9800.toInt()) // Orange for promotions
                    "urgent" -> setColor(0xFFF44336.toInt()) // Red for urgent
                    else -> setColor(0xFF2196F3.toInt()) // Blue for general
                }
            }
            .build()
        
        notificationManager.notify(notificationId, notification)
        Log.d(TAG, "Notification sent with ID: $notificationId")
    }
    
    /**
     * Creates notification channel for Android 8+
     * Required for showing notifications on Android 8 and above
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "اعلانات نوغره سود", // Persian: "Noghre Sod Notifications"
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "اعلانات مربوط به سفارشات و پیشنهادات" // Persian: "Notifications about orders and offers"
                enableVibration(true)
                setShowBadge(true)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }
    
    /**
     * Sends the FCM token to backend server
     * This should be called when:
     * - App first launches
     * - Token is refreshed
     * - User logs in
     * 
     * TODO: Implement actual backend API call
     * 
     * @param token The FCM token to send
     */
    private fun sendTokenToBackend(token: String) {
        // TODO: Implement API call to backend
        // The backend should store this token associated with the user
        // so it can send targeted push notifications
        
        Log.d(TAG, "Token should be sent to backend: $token")
        
        // Example implementation (uncomment when ready):
        // viewModelScope.launch {
        //     try {
        //         apiService.updateFCMToken(FCMTokenRequest(token))
        //         Log.d(TAG, "Token sent to backend successfully")
        //     } catch (e: Exception) {
        //         Log.e(TAG, "Failed to send token to backend", e)
        //     }
        // }
    }
}
