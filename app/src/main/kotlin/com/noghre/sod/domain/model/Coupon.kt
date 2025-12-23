package com.noghre.sod.domain.model

data class Coupon(
    val id: String,
    val code: String,
    val discountType: DiscountType,
    val discountValue: Double,
    val minAmount: Double?,
    val usageLimit: Int?,
    val usageCount: Int,
    val expiryDate: Long
)

enum class DiscountType {
    PERCENTAGE, FIXED
}
