package com.noghre.sod.data.local

import com.noghre.sod.data.local.database.AppDatabase
import com.noghre.sod.data.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Local Data Source Interface
 * Defines contract for all local database operations
 */
interface LocalDataSource {

    // ==================== Product Operations ====================
    suspend fun getProducts(): List<ProductEntity>
    suspend fun getProducts(limit: Int, offset: Int): List<ProductEntity>
    suspend fun saveProducts(products: List<ProductEntity>)
    suspend fun getProductById(id: String): ProductEntity?
    suspend fun getProductsByCategory(categoryId: String): List<ProductEntity>
    suspend fun searchProducts(query: String): List<ProductEntity>
    suspend fun updateProductFavorite(productId: String, isFavorite: Boolean)
    fun getProductsFlow(): Flow<List<ProductEntity>>

    // ==================== Cart Operations ====================
    suspend fun getCart(): CartEntity?
    suspend fun saveCart(cart: CartEntity)
    suspend fun addCartItem(item: CartItemEntity)
    suspend fun updateCartItem(item: CartItemEntity)
    suspend fun removeCartItem(itemId: Long)
    suspend fun clearCart()
    fun getCartFlow(): Flow<CartEntity?>
    suspend fun getCartItemCount(): Int

    // ==================== Order Operations ====================
    suspend fun getOrders(userId: String): List<OrderEntity>
    suspend fun getOrderById(orderId: String): OrderEntity?
    suspend fun saveOrder(order: OrderEntity)
    suspend fun updateOrderStatus(orderId: String, status: String)
    suspend fun getOrdersFlow(userId: String): Flow<List<OrderEntity>>

    // ==================== User Operations ====================
    suspend fun getUser(userId: String): UserEntity?
    suspend fun saveUser(user: UserEntity)
    suspend fun updateUser(user: UserEntity)
    suspend fun deleteUser(userId: String)
    fun getUserFlow(userId: String): Flow<UserEntity?>
    suspend fun getCurrentUser(): UserEntity?

    // ==================== Cleanup Operations ====================
    suspend fun clearAll()
    suspend fun clearProducts()
    suspend fun clearOrders()
}

/**
 * Local Data Source Implementation
 * Uses Room database DAOs for data persistence
 */
class LocalDataSourceImpl(private val database: AppDatabase) : LocalDataSource {

    private val productDao = database.productDao()
    private val cartDao = database.cartDao()
    private val orderDao = database.orderDao()
    private val userDao = database.userDao()

    // ==================== Product Operations ====================

    override suspend fun getProducts(): List<ProductEntity> {
        return productDao.getAllProducts()
    }

    override suspend fun getProducts(limit: Int, offset: Int): List<ProductEntity> {
        return productDao.getProductsWithLimit(limit, offset)
    }

    override suspend fun saveProducts(products: List<ProductEntity>) {
        productDao.insertProducts(products)
    }

    override suspend fun getProductById(id: String): ProductEntity? {
        return productDao.getProductById(id)
    }

    override suspend fun getProductsByCategory(categoryId: String): List<ProductEntity> {
        return productDao.getProductsByCategory(categoryId)
    }

    override suspend fun searchProducts(query: String): List<ProductEntity> {
        return productDao.searchProducts("%$query%")
    }

    override suspend fun updateProductFavorite(productId: String, isFavorite: Boolean) {
        productDao.updateFavoriteStatus(productId, isFavorite)
    }

    override fun getProductsFlow(): Flow<List<ProductEntity>> {
        return productDao.getAllProductsFlow()
    }

    // ==================== Cart Operations ====================

    override suspend fun getCart(): CartEntity? {
        return cartDao.getCart()
    }

    override suspend fun saveCart(cart: CartEntity) {
        cartDao.insertCart(cart)
    }

    override suspend fun addCartItem(item: CartItemEntity) {
        cartDao.insertCartItem(item)
    }

    override suspend fun updateCartItem(item: CartItemEntity) {
        cartDao.updateCartItem(item)
    }

    override suspend fun removeCartItem(itemId: Long) {
        cartDao.deleteCartItem(itemId)
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }

    override fun getCartFlow(): Flow<CartEntity?> {
        return cartDao.getCartFlow()
    }

    override suspend fun getCartItemCount(): Int {
        return cartDao.getCartItemCount()
    }

    // ==================== Order Operations ====================

    override suspend fun getOrders(userId: String): List<OrderEntity> {
        return orderDao.getUserOrders(userId).getOrNull() ?: emptyList()
    }

    override suspend fun getOrderById(orderId: String): OrderEntity? {
        return orderDao.getOrderById(orderId)
    }

    override suspend fun saveOrder(order: OrderEntity) {
        orderDao.insertOrder(order)
    }

    override suspend fun updateOrderStatus(orderId: String, status: String) {
        orderDao.updateOrderStatus(orderId, status, System.currentTimeMillis())
    }

    override fun getOrdersFlow(userId: String): Flow<List<OrderEntity>> {
        return orderDao.getUserOrders(userId)
    }

    // ==================== User Operations ====================

    override suspend fun getUser(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }

    override suspend fun saveUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    override suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    override suspend fun deleteUser(userId: String) {
        userDao.deleteUserById(userId)
    }

    override fun getUserFlow(userId: String): Flow<UserEntity?> {
        return userDao.getUserByIdFlow(userId)
    }

    override suspend fun getCurrentUser(): UserEntity? {
        return userDao.getActiveUser()
    }

    // ==================== Cleanup Operations ====================

    override suspend fun clearAll() {
        productDao.deleteAllProducts()
        cartDao.clearCart()
        orderDao.deleteAllOrders()
        userDao.deleteAllUsers()
    }

    override suspend fun clearProducts() {
        productDao.deleteAllProducts()
    }

    override suspend fun clearOrders() {
        orderDao.deleteAllOrders()
    }
}
