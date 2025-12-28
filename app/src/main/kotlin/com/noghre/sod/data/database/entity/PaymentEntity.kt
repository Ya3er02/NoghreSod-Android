package com.noghre.sod.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.noghre.sod.domain.model.Payment
import com.noghre.sod.domain.model.PaymentGateway
import com.noghre.sod.domain.model.PaymentStatus

/**
 * Room database entity for Payment
 * 
 * Maps Payment domain model to SQLite table
 * Includes indexes for efficient querying
 * 
 * Schema:
 * - id: Unique payment identifier (primary key)
 * - orderId: Order this payment belongs to
 * - amount: Payment amount in Tomans
 * - gateway: Which payment gateway processed it
 * - authority: Gateway-specific transaction code
 * - refId: Bank reference number (if available)
 * - status: Current payment status
 * - createdAt: When payment was initiated
 * - paidAt: When payment was verified (null if failed)
 * - description: Transaction description
 */
@Entity(
    tableName = "payments",
    indices = [
        Index(name = "idx_payment_id", value = ["id"], unique = true),
        Index(name = "idx_payment_orderId", value = ["orderId"]),
        Index(name = "idx_payment_authority", value = ["authority"], unique = true),
        Index(name = "idx_payment_status", value = ["status"]),
        Index(name = "idx_payment_createdAt", value = ["createdAt"])
    ]
)
data class PaymentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "orderId")
    val orderId: String,
    
    @ColumnInfo(name = "amount")
    val amount: Long,  // به تومان
    
    @ColumnInfo(name = "gateway")
    val gateway: String,  // PaymentGateway enum name
    
    @ColumnInfo(name = "authority")
    val authority: String?,  // کد رهگیری درگاه
    
    @ColumnInfo(name = "refId")
    val refId: String?,  // شماره مرجع بانک
    
    @ColumnInfo(name = "status")
    val status: String,  // PaymentStatus enum name
    
    @ColumnInfo(name = "createdAt")
    val createdAt: Long,  // Timestamp milliseconds
    
    @ColumnInfo(name = "paidAt")
    val paidAt: Long?,  // Timestamp when verified (null if not paid)
    
    @ColumnInfo(name = "description")
    val description: String?  // تفصیل تراکنش
) {
    
    /**
     * Convert database entity to domain model
     */
    fun toDomainModel(): Payment = Payment(
        id = id,
        orderId = orderId,
        amount = amount,
        gateway = PaymentGateway.valueOf(gateway),
        authority = authority,
        refId = refId,
        status = PaymentStatus.valueOf(status),
        createdAt = createdAt,
        paidAt = paidAt,
        description = description
    )
    
    companion object {
        /**
         * Create entity from domain model
         */
        fun fromDomainModel(payment: Payment): PaymentEntity = PaymentEntity(
            id = payment.id,
            orderId = payment.orderId,
            amount = payment.amount,
            gateway = payment.gateway.name,
            authority = payment.authority,
            refId = payment.refId,
            status = payment.status.name,
            createdAt = payment.createdAt,
            paidAt = payment.paidAt,
            description = payment.description
        )
    }
}
