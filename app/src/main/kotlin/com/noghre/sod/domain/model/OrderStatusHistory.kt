package com.noghre.sod.domain.model

import java.util.Date

data class OrderStatusHistory(
    val status: OrderStatus,
    val timestamp: Date,
    val description: String? = null
)
