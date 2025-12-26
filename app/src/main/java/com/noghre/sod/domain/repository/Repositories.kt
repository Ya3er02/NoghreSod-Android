package com.noghre.sod.domain.repository

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.*
import kotlinx.coroutines.flow.Flow

// ============== CART REPOSITORY ==============

/**
 * Repository interface for shopping cart operations.
 * Handles all cart-related business logic.
 */
interface CartRepository {

    /**
     * Get user's cart with all items.
     */
    fun getCart(): Flow<Result<Cart>>

    /**
     * Add item to cart.
     *
     * @param productId ID of product to add
     * @param quantity Quantity to add
     * @param variantId Optional variant selection
     * @return Updated cart
     */
    suspend fun addItem(
        productId: String,
        quantity: Int,
        variantId: String? = null
    ): Result<Cart>

    /**
     * Update cart item quantity.
     *
     * @param itemId ID of cart item to update
     * @param quantity New quantity
     * @return Updated cart
     */
    suspend fun updateItemQuantity(
        itemId: String,
        quantity: Int
    ): Result<Cart>

    /**
     * Remove item from cart.
     *
     * @param itemId ID of cart item to remove
     * @return Updated cart
     */
    suspend fun removeItem(itemId: String): Result<Cart>

    /**
     * Clear entire cart.
     *
     * @return Result of clearing
     */
    suspend fun clearCart(): Result<Unit>

    /**
     * Apply coupon code to cart.
     *
     * @param code Coupon code to apply
     * @return Updated cart with discount
     */
    suspend fun applyCoupon(code: String): Result<Cart>

    /**
     * Remove applied coupon from cart.
     *
     * @return Updated cart without discount
     */
    suspend fun removeCoupon(): Result<Cart>

    /**
     * Validate coupon code without applying.
     *
     * @param code Coupon code to validate
     * @return Coupon details if valid
     */
    suspend fun validateCoupon(code: String): Result<Coupon>

    /**
     * Get cart item count (for badge).
     */
    fun getCartItemCount(): Flow<Int>

    /**
     * Sync local cart with server.
     *
     * @return Synchronized cart
     */
    suspend fun syncCart(): Result<Cart>
}

// ============== ORDER REPOSITORY ==============

/**
 * Repository interface for order operations.
 * Handles all order-related business logic.
 */
interface OrderRepository {

    /**
     * Get paginated list of user's orders.
     *
     * @param page Page number (0-based)
     * @param pageSize Number of items per page
     * @return List of orders
     */
    fun getOrders(page: Int = 0, pageSize: Int = 20): Flow<Result<List<Order>>>

    /**
     * Get specific order by ID.
     *
     * @param orderId ID of order to retrieve
     * @return Order details
     */
    fun getOrderById(orderId: String): Flow<Result<Order>>

    /**
     * Create new order from cart.
     *
     * @param addressId ID of shipping address
     * @param paymentMethod Payment method to use
     * @param note Optional customer note
     * @return Created order
     */
    suspend fun createOrder(
        addressId: String,
        paymentMethod: PaymentMethod,
        note: String? = null
    ): Result<Order>

    /**
     * Cancel an order.
     *
     * @param orderId ID of order to cancel
     * @return Updated order with cancelled status
     */
    suspend fun cancelOrder(orderId: String): Result<Order>

    /**
     * Get orders by status.
     *
     * @param status Order status to filter by
     * @return Orders with specified status
     */
    fun getOrdersByStatus(status: OrderStatus): Flow<Result<List<Order>>>

    /**
     * Request refund for an order.
     *
     * @param orderId ID of order to refund
     * @param reason Reason for refund
     * @return Result of refund request
     */
    suspend fun requestRefund(orderId: String, reason: String): Result<Unit>
}

// ============== AUTHENTICATION REPOSITORY ==============

/**
 * Repository interface for authentication operations.
 * Handles user authentication and session management.
 */
interface AuthRepository {

    /**
     * Login with email and password.
     *
     * @param email User email
     * @param password User password
     * @return Authenticated user
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Register new user account.
     *
     * @param email User email
     * @param password User password
     * @param firstName First name
     * @param lastName Last name
     * @param phoneNumber Phone number
     * @return Newly created user
     */
    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Result<User>

    /**
     * Logout current user.
     *
     * @return Result of logout operation
     */
    suspend fun logout(): Result<Unit>

    /**
     * Get currently logged in user.
     *
     * @return Current user or null if not logged in
     */
    fun getCurrentUser(): Flow<Result<User?>>

    /**
     * Check if user is logged in.
     *
     * @return True if user is authenticated
     */
    fun isLoggedIn(): Flow<Boolean>

    /**
     * Refresh authentication token.
     *
     * @return Result of token refresh
     */
    suspend fun refreshToken(): Result<Unit>

    /**
     * Send password reset email.
     *
     * @param email Email to send reset link to
     * @return Result of sending email
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    /**
     * Verify email with confirmation code.
     *
     * @param code Email verification code
     * @return Result of email verification
     */
    suspend fun verifyEmail(code: String): Result<Unit>

    /**
     * Send email verification code.
     *
     * @return Result of sending code
     */
    suspend fun sendVerificationCode(): Result<Unit>
}

// ============== PRODUCT REPOSITORY ==============

/**
 * Repository interface for product operations.
 * Handles product catalog and search functionality.
 */
interface ProductRepository {

    /**
     * Get paginated list of all products.
     *
     * @param page Page number (0-based)
     * @param pageSize Number of items per page
     * @return List of product summaries
     */
    fun getProducts(page: Int = 0, pageSize: Int = 20): Flow<Result<List<ProductSummary>>>

    /**
     * Get product details by ID.
     *
     * @param productId ID of product to retrieve
     * @return Detailed product information
     */
    fun getProductById(productId: String): Flow<Result<ProductDetail>>

    /**
     * Search products by query.
     *
     * @param query Search query
     * @param page Page number (0-based)
     * @param pageSize Number of items per page
     * @return Search results
     */
    fun searchProducts(
        query: String,
        page: Int = 0,
        pageSize: Int = 20
    ): Flow<Result<List<ProductSummary>>>

    /**
     * Get products by category.
     *
     * @param category Product category
     * @param page Page number (0-based)
     * @param pageSize Number of items per page
     * @return Products in category
     */
    fun getProductsByCategory(
        category: ProductCategory,
        page: Int = 0,
        pageSize: Int = 20
    ): Flow<Result<List<ProductSummary>>>

    /**
     * Get user's favorite products.
     *
     * @return List of favorite products
     */
    fun getFavorites(): Flow<Result<List<ProductSummary>>>

    /**
     * Add product to favorites.
     *
     * @param productId ID of product to add
     * @return Result of adding to favorites
     */
    suspend fun addToFavorites(productId: String): Result<Unit>

    /**
     * Remove product from favorites.
     *
     * @param productId ID of product to remove
     * @return Result of removing from favorites
     */
    suspend fun removeFromFavorites(productId: String): Result<Unit>
}

// ============== USER REPOSITORY ==============

/**
 * Repository interface for user profile operations.
 * Handles user information and address management.
 */
interface UserRepository {

    /**
     * Get current user's profile.
     *
     * @return Current user information
     */
    fun getUserProfile(): Flow<Result<User>>

    /**
     * Update user profile.
     *
     * @param firstName New first name
     * @param lastName New last name
     * @param phoneNumber New phone number
     * @return Updated user
     */
    suspend fun updateProfile(
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Result<User>

    /**
     * Get user's addresses.
     *
     * @return List of user addresses
     */
    fun getAddresses(): Flow<Result<List<Address>>>

    /**
     * Add new address to user's list.
     *
     * @param address Address to add
     * @return Updated user
     */
    suspend fun addAddress(address: Address): Result<User>

    /**
     * Update existing address.
     *
     * @param address Address to update
     * @return Updated user
     */
    suspend fun updateAddress(address: Address): Result<User>

    /**
     * Delete address from user's list.
     *
     * @param addressId ID of address to delete
     * @return Result of deletion
     */
    suspend fun deleteAddress(addressId: String): Result<Unit>

    /**
     * Set default address for shipment.
     *
     * @param addressId ID of address to set as default
     * @return Updated user
     */
    suspend fun setDefaultAddress(addressId: String): Result<User>
}
