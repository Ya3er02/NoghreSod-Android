package com.noghre.sod.domain.model

data class CartItem(
    val id: String,
    val product: Product,
    val quantity: Int,
    val addedAt: java.util.Date
)
