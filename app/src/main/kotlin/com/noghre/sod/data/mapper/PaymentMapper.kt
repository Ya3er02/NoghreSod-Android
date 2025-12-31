package com.noghre.sod.data.mapper

import com.noghre.sod.data.dto.remote.PaymentDto
import com.noghre.sod.domain.model.Payment
import com.noghre.sod.domain.model.PaymentMethod
import com.noghre.sod.domain.model.PaymentStatus

/**
 * Mapper for Payment-related data transformations.
 *
 * Converts between:
 * - DTO ↔ Domain
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
object PaymentMapper {

    // ============ DTO TO DOMAIN ============

    /**
     * Convert PaymentDto to Payment domain model.
     * Safe conversion with fallbacks for enums.
     */
    fun PaymentDto.toDomain(): Payment = Payment(
        id = this.id,
        orderId = this.orderId,
        userId = this.userId ?: "",
        method = runCatching {
            PaymentMethod.valueOf(this.method.uppercase())
        }.getOrDefault(PaymentMethod.CREDIT_CARD),
        amount = this.amount,
        status = runCatching {
            PaymentStatus.valueOf(this.status.uppercase())
        }.getOrDefault(PaymentStatus.PENDING),
        transactionId = this.transactionId,
        createdAt = this.createdAt
    )

    // ============ BATCH CONVERSIONS ============

    /**
     * Convert list of PaymentDtos to domain models.
     */
    fun List<PaymentDto>.toDomainList(): List<Payment> = map { it.toDomain() }
}
