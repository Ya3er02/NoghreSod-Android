package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

data class CartDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("productId")
    val productId: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Long,
    @SerializedName("addedAt")
    val addedAt: Long = System.currentTimeMillis(),
    @SerializedName("updatedAt")
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun getTotalPrice(): Long = price * quantity
}