package com.noghre.sod.data.mapper

import com.noghre.sod.data.network.dto.PaymentDto
import com.noghre.sod.domain.model.Payment

/**
 * Mapper for Payment DTOs to Domain models.
 *
 * @author NoghreSod Team
 */
object PaymentMapper {

    fun PaymentDto.toDomain(): Payment = Payment(
        id = id ?: "",
        orderId = orderId ?: "",
        amount = amount ?: 0.0,
        method = method ?: "",
        status = status ?: "PENDING",
        transactionId = transactionId,
        gatewayResponse = gatewayResponse,
        createdAt = createdAt ?: System.currentTimeMillis(),
        updatedAt = updatedAt ?: System.currentTimeMillis()
    )
}
