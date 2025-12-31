package com.noghre.sod.data.mapper

import com.noghre.sod.data.database.entity.OrderEntity
import com.noghre.sod.data.dto.remote.OrderDto
import com.noghre.sod.data.dto.remote.OrderItemDto
import com.noghre.sod.data.dto.remote.OrderTrackingDto
import com.noghre.sod.data.dto.remote.TrackingEventDto
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.model.OrderItem
import com.noghre.sod.domain.model.OrderStatus
import com.noghre.sod.domain.model.OrderTracking
import com.noghre.sod.domain.model.PaymentMethod
import com.noghre.sod.domain.model.TrackingEvent
import kotlinx.serialization.json.Json

/**
 * Mapper for Order-related data transformations.
 *
 * Converts between:
 * - DTO ↔ Domain
 * - Entity ↔ Domain
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
object OrderMapper {

    // ============ DTO TO DOMAIN ============

    /**
     * Convert OrderDto to Order domain model.
     * Safe conversion with fallbacks.
     */
    fun OrderDto.toDomain(): Order = Order(
        id = this.id,
        userId = this.userId,
        orderNumber = this.orderNumber,
        items = this.items.map { it.toDomain() },
        shippingAddress = this.shippingAddress.toDomain(),
        paymentMethod = runCatching {
            PaymentMethod.valueOf(this.paymentMethod.uppercase())
        }.getOrDefault(PaymentMethod.CREDIT_CARD),
        subtotal = this.subtotal,
        shippingCost = this.shippingCost,
        discountAmount = this.discountAmount,
        totalAmount = this.totalAmount,
        status = runCatching {
            OrderStatus.valueOf(this.status.uppercase())
        }.getOrDefault(OrderStatus.PENDING),
        tracking = this.tracking?.toDomain(),
        createdAt = this.createdAt
    )

    /**
     * Convert OrderItemDto to OrderItem domain model.
     */
    fun OrderItemDto.toDomain(): OrderItem = OrderItem(
        id = this.id,
        product = this.product.toDomain(),
        quantity = this.quantity,
        unitPrice = this.unitPrice,
        selectedColor = this.selectedColor,
        selectedSize = this.selectedSize
    )

    /**
     * Convert OrderTrackingDto to OrderTracking domain model.
     */
    fun OrderTrackingDto.toDomain(): OrderTracking = OrderTracking(
        trackingNumber = this.trackingNumber,
        carrier = this.carrier,
        estimatedDelivery = this.estimatedDelivery,
        currentLocation = this.currentLocation,
        events = this.events?.map { it.toDomain() } ?: emptyList()
    )

    /**
     * Convert TrackingEventDto to TrackingEvent domain model.
     */
    fun TrackingEventDto.toDomain(): TrackingEvent = TrackingEvent(
        status = this.status,
        timestamp = this.timestamp,
        location = this.location,
        description = this.description
    )

    // ============ ENTITY TO DOMAIN ============

    /**
     * Convert OrderEntity to Order domain model.
     * Deserializes JSON fields.
     */
    fun OrderEntity.toDomain(): Order = try {
        Order(
            id = this.id,
            userId = this.userId,
            orderNumber = this.orderNumber,
            items = try {
                Json.decodeFromString(this.itemsJson)
            } catch (e: Exception) {
                emptyList()
            },
            shippingAddress = try {
                Json.decodeFromString(this.shippingAddressJson)
            } catch (e: Exception) {
                throw IllegalStateException("Invalid shipping address")
            },
            paymentMethod = runCatching {
                PaymentMethod.valueOf(this.paymentMethod.uppercase())
            }.getOrDefault(PaymentMethod.CREDIT_CARD),
            subtotal = this.subtotal,
            shippingCost = this.shippingCost,
            discountAmount = this.discountAmount,
            totalAmount = this.totalAmount,
            status = runCatching {
                OrderStatus.valueOf(this.status.uppercase())
            }.getOrDefault(OrderStatus.PENDING),
            tracking = try {
                Json.decodeFromString(this.trackingJson ?: "null")
            } catch (e: Exception) {
                null
            },
            createdAt = this.createdAt
        )
    } catch (e: Exception) {
        throw IllegalStateException("Failed to convert OrderEntity to Order", e)
    }

    // ============ DOMAIN TO ENTITY ============

    /**
     * Convert Order domain model to OrderEntity for caching.
     */
    fun Order.toEntity(): OrderEntity = OrderEntity(
        id = this.id,
        userId = this.userId,
        orderNumber = this.orderNumber,
        itemsJson = Json.encodeToString(this.items),
        shippingAddressJson = Json.encodeToString(this.shippingAddress),
        paymentMethod = this.paymentMethod.name.lowercase(),
        subtotal = this.subtotal,
        shippingCost = this.shippingCost,
        discountAmount = this.discountAmount,
        totalAmount = this.totalAmount,
        status = this.status.name.lowercase(),
        trackingJson = this.tracking?.let { Json.encodeToString(it) },
        createdAt = this.createdAt,
        lastUpdated = System.currentTimeMillis()
    )

    // ============ BATCH CONVERSIONS ============

    /**
     * Convert list of OrderDtos to domain models.
     */
    fun List<OrderDto>.toDomainList(): List<Order> = map { it.toDomain() }

    /**
     * Convert list of OrderEntities to domain models.
     */
    fun List<OrderEntity>.toDomainList(): List<Order> = map { it.toDomain() }
}
