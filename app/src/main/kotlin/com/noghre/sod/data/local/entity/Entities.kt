package com.noghre.sod.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Product database entity.
 * Represents a product in the local database.
 */
@Entity(
    tableName = "products",
    indices = [
        Index(value = ["category"]),
        Index(value = ["inStock"])
    ]
)
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val category: String,
    val imageUrl: String,
    val images: String? = null, // JSON array as string
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val quantity: Int = 0,
    val weight: Double = 0.0,
    val material: String = "",
    val colors: String? = null, // JSON array as string
    val sizes: String? = null, // JSON array as string
    val inStock: Boolean = true,
    val isFavorite: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = "",
    val syncedAt: Long = System.currentTimeMillis()
)

/**
 * Cart database entity.
 */
@Entity(
    tableName = "cart",
    indices = [Index(value = ["userId"])]
)
data class CartEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val totalPrice: Double = 0.0,
    val itemCount: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = "",
    val syncedAt: Long = System.currentTimeMillis()
)

/**
 * Cart item database entity.
 */
@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = CartEntity::class,
            parentColumns = ["id"],
            childColumns = ["cartId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["cartId"]),
        Index(value = ["productId"])
    ]
)
data class CartItemEntity(
    @PrimaryKey
    val id: String,
    val cartId: String,
    val productId: String,
    val quantity: Int,
    val selectedColor: String? = null,
    val selectedSize: String? = null,
    val subtotal: Double = 0.0,
    val addedAt: String = ""
)

/**
 * Order database entity.
 */
@Entity(
    tableName = "orders",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["status"])
    ]
)
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val orderNumber: String,
    val shippingAddressId: String,
    val paymentMethod: String,
    val subtotal: Double,
    val shippingCost: Double = 0.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double,
    val status: String, // PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    val tracking: String? = null, // JSON object as string
    val notes: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val syncedAt: Long = System.currentTimeMillis()
)

/**
 * Order item database entity.
 */
@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["orderId"]),
        Index(value = ["productId"])
    ]
)
data class OrderItemEntity(
    @PrimaryKey
    val id: String,
    val orderId: String,
    val productId: String?,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val selectedColor: String? = null,
    val selectedSize: String? = null
)

/**
 * User database entity.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val phoneNumber: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val profileImageUrl: String? = null,
    val isVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val createdAt: String = "",
    val lastLogin: String? = null,
    val syncedAt: Long = System.currentTimeMillis()
)

/**
 * Address database entity.
 */
@Entity(
    tableName = "addresses",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class AddressEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val fullAddress: String,
    val province: String,
    val city: String,
    val postalCode: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isDefault: Boolean = false
)
