package com.noghre.sod.domain.model

data class PaymentVerifyResponse(
    val success: Boolean,
    val refId: String?,
    val cardPan: String?
)
