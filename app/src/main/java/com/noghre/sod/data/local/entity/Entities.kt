package com.noghre.sod.data.local.entity

import androidx.room.*
import com.noghre.sod.data.local.database.DatabaseConverters

// ============== PRODUCT ENTITY ==============

/**
 * Product entity for local database storage.
 * Contains all product information including images, colors, sizes.
 */
@Entity(tableName = "products")
@TypeConverters(DatabaseConverters::class)
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double?,
    val category: String,
    val imageUrl: String,
    val images: List<String>,
    val rating: Float,
    val reviewCount: Int,
    val quantity: Int,
    val weight: Double,
    val material: String,
    val colors: List<String>,
    val sizes: List<String>,
    val inStock: Boolean,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis(),
    val createdAt: String
)

// ============== USER ENTITY ==============

/**
 * User entity for local database storage.
 * Contains user profile information.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val phoneNumber: String?,
    val firstName: String?,
    val lastName: String?,
    val profileImageUrl: String?,
    val isVerified: Boolean,
    val createdAt: String,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

// ============== ADDRESS ENTITY ==============

/**
 * Address entity for local database storage.
 * Related to User entity via foreign key.
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
    indices = [Index("userId")]
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
    val recipientName: String?,
    val recipientPhone: String?,
    val isDefault: Boolean = false
)

// ============== CART ENTITY ==============

/**
 * Cart entity for local database storage.
 * Related to User entity via foreign key.
 */
@Entity(
    tableName = "carts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class CartEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val totalPrice: Double,
    val itemCount: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Cart item entity for local database storage.
 * Related to Cart and Product entities via foreign keys.
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
    indices = [Index("cartId"), Index("productId")]
)
data class CartItemEntity(
    @PrimaryKey
    val id: String,
    val cartId: String,
    val productId: String,
    val quantity: Int,
    val selectedColor: String?,
    val selectedSize: String?,
    val subtotal: Double
)

// ============== ORDER ENTITY ==============

/**
 * Order entity for local database storage.
 * Related to User entity via foreign key.
 */
@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val orderNumber: String,
    val subtotal: Double,
    val shippingCost: Double,
    val discountAmount: Double,
    val totalAmount: Double,
    val status: String, // "pending", "confirmed", "shipped", "delivered", "cancelled"
    val shippingAddressId: String,
    @Embedded(prefix = "tracking_")
    val tracking: OrderTrackingEntity?,
    val createdAt: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Order item entity for local database storage.
 * Related to Order and Product entities via foreign keys.
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
            onDelete = ForeignKey.RESTRICT // Prevent product deletion if used in orders
        )
    ],
    indices = [Index("orderId"), Index("productId")]
)
data class OrderItemEntity(
    @PrimaryKey
    val id: String,
    val orderId: String,
    val productId: String,
    val quantity: Int,
    val unitPrice: Double,
    val selectedColor: String?,
    val selectedSize: String?
)

/**
 * Order tracking information.
 * Embedded within OrderEntity.
 */
data class OrderTrackingEntity(
    val trackingNumber: String,
    val carrier: String,
    val estimatedDelivery: String,
    val currentLocation: String
)

// ============== CATEGORY ENTITY ==============

/**
 * Category entity for local database storage.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val productCount: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)

// ============== PAYMENT ENTITY ==============

/**
 * Payment entity for local database storage.
 * Related to Order entity via foreign key.
 */
@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId")]
)
data class PaymentEntity(
    @PrimaryKey
    val id: String,
    val orderId: String,
    val amount: Double,
    val method: String, // "card", "bank_transfer", "wallet"
    val status: String, // "pending", "completed", "failed"
    val transactionId: String?,
    val createdAt: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

// ============== REVIEW ENTITY ==============

/**
 * Review entity for local database storage.
 * Related to User and Product entities.
 */
@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("productId")]
)
@TypeConverters(DatabaseConverters::class)
data class ReviewEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val productId: String,
    val rating: Float,
    val title: String,
    val comment: String,
    val images: List<String>?,
    val helpfulCount: Int,
    val isVerifiedPurchase: Boolean,
    val createdAt: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

// ============== NOTIFICATION ENTITY ==============

/**
 * Notification entity for local database storage.
 * Related to User entity via foreign key.
 */
@Entity(
    tableName = "notifications",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // "order", "promotion", "system"
    val imageUrl: String?,
    val actionUrl: String?,
    val isRead: Boolean,
    val createdAt: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
