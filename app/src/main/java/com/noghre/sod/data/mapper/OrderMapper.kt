package com.noghre.sod.data.mapper

import com.google.gson.Gson
import com.noghre.sod.data.dto.OrderDto
import com.noghre.sod.data.dto.OrderItemDto
import com.noghre.sod.data.local.entity.OrderEntity

class OrderMapper {
    fun toEntity(dto: OrderDto): OrderEntity {
        return OrderEntity(
            id = dto.id,
            userId = dto.userId,
            orderNumber = dto.orderNumber,
            items = Gson().toJson(dto.items),
            totalAmount = dto.totalAmount,
            discountAmount = dto.discountAmount,
            shippingCost = dto.shippingCost,
            taxAmount = dto.taxAmount,
            status = dto.status,
            paymentMethod = dto.paymentMethod,
            trackingCode = dto.trackingCode,
            deliveryDate = dto.deliveryDate,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            notes = dto.notes,
            shippingAddress = dto.shippingAddress
        )
    }

    fun toDto(entity: OrderEntity): OrderDto {
        return OrderDto(
            id = entity.id,
            userId = entity.userId,
            orderNumber = entity.orderNumber,
            items = Gson().fromJson(entity.items, Array<OrderItemDto>::class.java).toList(),
            totalAmount = entity.totalAmount,
            discountAmount = entity.discountAmount,
            shippingCost = entity.shippingCost,
            taxAmount = entity.taxAmount,
            status = entity.status,
            paymentMethod = entity.paymentMethod,
            trackingCode = entity.trackingCode,
            deliveryDate = entity.deliveryDate,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            notes = entity.notes,
            shippingAddress = entity.shippingAddress
        )
    }

    fun toEntities(dtos: List<OrderDto>): List<OrderEntity> = dtos.map { toEntity(it) }
    fun toDtos(entities: List<OrderEntity>): List<OrderDto> = entities.map { toDto(it) }
}