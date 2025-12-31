package com.noghre.sod.data.mapper

import com.noghre.sod.data.database.entity.OrderEntity
import com.noghre.sod.data.network.dto.OrderDto
import com.noghre.sod.domain.model.Order

/**
 * Mapper for Order DTOs to Domain models.
 *
 * @author NoghreSod Team
 */
object OrderMapper {

    fun OrderDto.toDomain(): Order = Order(
        id = id ?: "",
        userId = userId ?: "",
        items = items ?: emptyList(),
        totalPrice = totalPrice ?: 0.0,
        status = status ?: "PENDING",
        shippingAddress = shippingAddress ?: "",
        paymentMethod = paymentMethod ?: "",
        trackingNumber = trackingNumber,
        createdAt = createdAt ?: System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        lastUpdated = System.currentTimeMillis()
    )

    fun OrderEntity.toDomain(): Order = Order(
        id = id,
        userId = userId,
        items = items,
        totalPrice = totalPrice,
        status = status,
        shippingAddress = shippingAddress,
        paymentMethod = paymentMethod,
        trackingNumber = trackingNumber,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastUpdated = lastUpdated
    )

    fun Order.toEntity(): OrderEntity = OrderEntity(
        id = id,
        userId = userId,
        items = items,
        totalPrice = totalPrice,
        status = status,
        shippingAddress = shippingAddress,
        paymentMethod = paymentMethod,
        trackingNumber = trackingNumber,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastUpdated = lastUpdated
    )

    fun List<OrderDto>.toDomainList(): List<Order> =
        this.map { it.toDomain() }

    fun List<OrderEntity>.toDomainListFromEntity(): List<Order> =
        this.map { it.toDomain() }
}
