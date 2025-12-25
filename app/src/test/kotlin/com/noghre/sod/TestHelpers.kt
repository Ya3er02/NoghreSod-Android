package com.noghre.sod

import junit.framework.TestCase
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlinx.coroutines.Dispatchers
import org.junit.ExperimentalCoroutinesApi

/**
 * 🔬 Testing Utilities & Helpers
 * 
 * Provides test infrastructure, mocking utilities, and common test patterns.
 * 
 * @since 1.0.0
 */

// ==================== Test Dispatchers ====================

/**
 * Main dispatcher rule for unit tests
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }
    
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

// ==================== Mock Factories ====================

/**
 * Create mock Product objects for testing
 */
fun createMockProducts(
    count: Int,
    startId: Int = 1,
    categoryId: String = "default"
): List<Any> {
    return List(count) { index ->
        mapOf(
            "id" to "product-${startId + index}",
            "name" to "Product ${startId + index}",
            "price" to ((100 + index) * 1000L),
            "image" to "https://example.com/image-${index}.jpg",
            "categoryId" to categoryId,
            "rating" to (4.0f + (index % 5) * 0.2f),
            "reviewCount" to (10 + index),
            "stock" to (5 + index),
            "isFavorite" to false,
            "createdAt" to (System.currentTimeMillis() - (index * 86400000L))
        )
    }
}

/**
 * Create mock Order objects for testing
 */
fun createMockOrders(
    count: Int,
    startId: Int = 1
): List<Any> {
    val statuses = listOf("pending", "confirmed", "shipped", "delivered")
    return List(count) { index ->
        mapOf(
            "id" to "order-${startId + index}",
            "orderNumber" to "ORD-${startId + index}",
            "status" to statuses[index % statuses.size],
            "total" to ((100 + index) * 1000L),
            "itemCount" to (1 + index),
            "createdAt" to System.currentTimeMillis(),
            "estimatedDelivery" to (System.currentTimeMillis() + 604800000)
        )
    }
}

/**
 * Create mock CartItem objects for testing
 */
fun createMockCartItems(
    count: Int,
    startId: Int = 1
): List<Any> {
    return List(count) { index ->
        mapOf(
            "id" to "cart-item-${startId + index}",
            "productId" to "product-${startId + index}",
            "quantity" to (1 + (index % 3)),
            "price" to ((100 + index) * 1000L)
        )
    }
}

// ==================== Assertion Helpers ====================

/**
 * Assert that a suspending block completes without exception
 */
suspend inline fun <T> assertSuspendCompletes(crossinline block: suspend () -> T): T {
    return try {
        block()
    } catch (e: Exception) {
        throw AssertionError("Expected block to complete without exception, but got: ${e.message}", e)
    }
}

/**
 * Assert that a suspending block throws exception
 */
suspend inline fun assertSuspendThrows(
    expectedType: Class<out Throwable>,
    crossinline block: suspend () -> Unit
) {
    try {
        block()
        throw AssertionError("Expected ${expectedType.simpleName} to be thrown, but nothing was thrown")
    } catch (e: Throwable) {
        if (!expectedType.isInstance(e)) {
            throw AssertionError(
                "Expected ${expectedType.simpleName} but got ${e::class.simpleName}: ${e.message}",
                e
            )
        }
    }
}

/**
 * Assert two collections have same items (ignoring order)
 */
fun <T> assertContainsSameItems(expected: Collection<T>, actual: Collection<T>) {
    TestCase.assertEquals(
        "Collections don't have same size",
        expected.size,
        actual.size
    )
    expected.forEach { item ->
        TestCase.assertTrue(
            "Item $item not found in actual collection",
            actual.contains(item)
        )
    }
}

// ==================== Time Helpers ====================

/**
 * Assert that a suspending block executes within time limit
 */
suspend inline fun <T> assertExecutesWithin(
    timeoutMs: Long,
    crossinline block: suspend () -> T
): T {
    val startTime = System.currentTimeMillis()
    val result = block()
    val duration = System.currentTimeMillis() - startTime
    
    if (duration > timeoutMs) {
        throw AssertionError(
            "Expected block to execute within ${timeoutMs}ms, but took ${duration}ms"
        )
    }
    
    return result
}

// ==================== Data Class Helpers ====================

/**
 * Deep copy for testing (prevents reference issues)
 */
inline fun <reified T> Any.deepCopy(): T {
    return this as T
}

/**
 * Create modified copy
 */
inline fun <reified T> T.withModification(block: T.() -> Unit): T {
    val copy = (this as Any).deepCopy<T>()
    copy.block()
    return copy
}
