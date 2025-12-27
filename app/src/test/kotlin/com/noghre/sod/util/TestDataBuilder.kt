package com.noghre.sod.util

import java.util.UUID

/**
 * Test data builder for creating test instances
 */
object TestDataBuilder {
    
    // ============================================
    // Product Test Data
    // ============================================
    
    fun buildProductId(): String = UUID.randomUUID().toString()
    
    data class ProductBuilder(
        var id: String = buildProductId(),
        var name: String = "Test Product",
        var description: String = "Test Description",
        var price: Double = 100.0,
        var discountPrice: Double? = null,
        var imageUrl: String = "https://example.com/image.jpg",
        var categoryId: String = UUID.randomUUID().toString(),
        var rating: Double = 4.5,
        var reviewCount: Int = 100,
        var inStock: Boolean = true,
        var stockQuantity: Int = 50
    ) {
        fun build() = mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "price" to price,
            "discountPrice" to discountPrice,
            "imageUrl" to imageUrl,
            "categoryId" to categoryId,
            "rating" to rating,
            "reviewCount" to reviewCount,
            "inStock" to inStock,
            "stockQuantity" to stockQuantity
        )
    }
    
    fun product(block: ProductBuilder.() -> Unit = {}): Map<*, *> {
        return ProductBuilder().apply(block).build()
    }
    
    // ============================================
    // User Test Data
    // ============================================
    
    fun buildUserId(): String = UUID.randomUUID().toString()
    
    data class UserBuilder(
        var id: String = buildUserId(),
        var email: String = "test@example.com",
        var username: String = "testuser",
        var firstName: String = "Test",
        var lastName: String = "User",
        var phoneNumber: String = "+989123456789",
        var profileImageUrl: String? = null,
        var isVerified: Boolean = true,
        var createdAt: Long = System.currentTimeMillis()
    ) {
        fun build() = mapOf(
            "id" to id,
            "email" to email,
            "username" to username,
            "firstName" to firstName,
            "lastName" to lastName,
            "phoneNumber" to phoneNumber,
            "profileImageUrl" to profileImageUrl,
            "isVerified" to isVerified,
            "createdAt" to createdAt
        )
    }
    
    fun user(block: UserBuilder.() -> Unit = {}): Map<*, *> {
        return UserBuilder().apply(block).build()
    }
    
    // ============================================
    // Order Test Data
    // ============================================
    
    fun buildOrderId(): String = UUID.randomUUID().toString()
    
    data class OrderBuilder(
        var id: String = buildOrderId(),
        var userId: String = buildUserId(),
        var items: List<Map<*, *>> = listOf(product()),
        var totalPrice: Double = 100.0,
        var status: String = "pending",
        var shippingAddress: String = "123 Test St, Test City",
        var createdAt: Long = System.currentTimeMillis(),
        var updatedAt: Long = System.currentTimeMillis()
    ) {
        fun build() = mapOf(
            "id" to id,
            "userId" to userId,
            "items" to items,
            "totalPrice" to totalPrice,
            "status" to status,
            "shippingAddress" to shippingAddress,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
    
    fun order(block: OrderBuilder.() -> Unit = {}): Map<*, *> {
        return OrderBuilder().apply(block).build()
    }
    
    // ============================================
    // Cart Test Data
    // ============================================
    
    fun buildCartId(): String = UUID.randomUUID().toString()
    
    data class CartBuilder(
        var id: String = buildCartId(),
        var userId: String = buildUserId(),
        var items: List<Map<*, *>> = listOf(
            mapOf(
                "productId" to buildProductId(),
                "quantity" to 1,
                "price" to 100.0
            )
        ),
        var totalItems: Int = 1,
        var totalPrice: Double = 100.0,
        var createdAt: Long = System.currentTimeMillis()
    ) {
        fun build() = mapOf(
            "id" to id,
            "userId" to userId,
            "items" to items,
            "totalItems" to totalItems,
            "totalPrice" to totalPrice,
            "createdAt" to createdAt
        )
    }
    
    fun cart(block: CartBuilder.() -> Unit = {}): Map<*, *> {
        return CartBuilder().apply(block).build()
    }
    
    // ============================================
    // Review Test Data
    // ============================================
    
    fun buildReviewId(): String = UUID.randomUUID().toString()
    
    data class ReviewBuilder(
        var id: String = buildReviewId(),
        var productId: String = buildProductId(),
        var userId: String = buildUserId(),
        var rating: Int = 5,
        var title: String = "Great Product!",
        var comment: String = "This product is excellent!",
        var images: List<String> = emptyList(),
        var createdAt: Long = System.currentTimeMillis()
    ) {
        fun build() = mapOf(
            "id" to id,
            "productId" to productId,
            "userId" to userId,
            "rating" to rating,
            "title" to title,
            "comment" to comment,
            "images" to images,
            "createdAt" to createdAt
        )
    }
    
    fun review(block: ReviewBuilder.() -> Unit = {}): Map<*, *> {
        return ReviewBuilder().apply(block).build()
    }
}

/**
 * Test utilities
 */
object TestUtils {
    
    /**
     * Create multiple test instances
     */
    fun <T> createList(size: Int, block: (index: Int) -> T): List<T> {
        return (0 until size).map { block(it) }
    }
    
    /**
     * Wait for async operations
     */
    suspend fun delay(ms: Long = 100) {
        kotlinx.coroutines.delay(ms)
    }
    
    /**
     * Assert that a list contains all items
     */
    infix fun <T> List<T>.containsAll(items: List<T>): Boolean {
        return items.all { it in this }
    }
    
    /**
     * Assert that a map contains all keys
     */
    infix fun Map<*, *>.containsKeys(keys: List<String>): Boolean {
        return keys.all { it in this.keys }
    }
}
