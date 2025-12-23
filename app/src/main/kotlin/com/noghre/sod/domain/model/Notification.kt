package com.noghre.sod.domain.model

data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val imageUrl: String? = null,
    val type: NotificationType,
    val relatedId: String? = null,
    val timestamp: Long,
    val isRead: Boolean = false,
    val data: Map<String, String>? = null
)

enum class NotificationType {
    ORDER_STATUS, PROMOTION, PAYMENT_CONFIRMATION, GENERAL
}
