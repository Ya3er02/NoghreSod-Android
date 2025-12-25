package com.noghre.sod.domain.usecase

import com.noghre.sod.domain.common.AppError
import com.noghre.sod.domain.common.Result
import com.noghre.sod.domain.repository.ProductRepository
import com.noghre.sod.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Comprehensive Unit Tests for GetProductsUseCase
 * 
 * Test Coverage:
 * - Happy path (success)
 * - Edge cases (empty list, large lists)
 * - Error scenarios (network, timeout, server)
 * - Parameter validation
 * - Concurrency
 * 
 * @since 1.0.0
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class GetProductsUseCaseTest {
    
    @Mock
    private lateinit var repository: ProductRepository
    
    private lateinit var useCase: GetProductsUseCase
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = GetProductsUseCase(
            repository = repository,
            ioDispatcher = testDispatcher
        )
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    // ==================== HAPPY PATH TESTS ====================
    
    @Test
    fun `invoke with default params returns success with products`() = runTest {
        // Given
        val mockProducts = createMockProducts(count = 5)
        whenever(repository.getProducts(page = 1, pageSize = 20, categoryId = null))
            .thenReturn(Result.Success(mockProducts))
        
        // When
        val result = useCase(page = 1, pageSize = 20)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(mockProducts, (result as Result.Success).data)
        verify(repository).getProducts(page = 1, pageSize = 20, categoryId = null)
    }
    
    @Test
    fun `invoke with category filter returns filtered products`() = runTest {
        // Given
        val categoryId = "cat-123"
        val mockProducts = createMockProducts(count = 3)
        whenever(repository.getProducts(page = 1, pageSize = 20, categoryId = categoryId))
            .thenReturn(Result.Success(mockProducts))
        
        // When
        val result = useCase(categoryId = categoryId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(3, (result as Result.Success).data.size)
    }
    
    @Test
    fun `invoke with pagination returns correct page`() = runTest {
        // Given
        val page = 3
        val mockProducts = createMockProducts(count = 20, startId = 41)
        whenever(repository.getProducts(page = page, pageSize = 20, categoryId = null))
            .thenReturn(Result.Success(mockProducts))
        
        // When
        val result = useCase(page = page)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(20, (result as Result.Success).data.size)
        verify(repository).getProducts(page = 3, pageSize = 20, categoryId = null)
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    fun `invoke returns empty list when no products found`() = runTest {
        // Given
        whenever(repository.getProducts(any(), any(), any()))
            .thenReturn(Result.Success(emptyList()))
        
        // When
        val result = useCase()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }
    
    @Test
    fun `invoke with page equals 0 throws IllegalArgumentException`() = runTest {
        // When & Then
        try {
            useCase(page = 0)
            assertTrue(false, "Should have thrown IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            verifyNoInteractions(repository)
        }
    }
    
    @Test
    fun `invoke with negative pageSize throws IllegalArgumentException`() = runTest {
        // When & Then
        try {
            useCase(pageSize = -1)
            assertTrue(false, "Should have thrown IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            verifyNoInteractions(repository)
        }
    }
    
    @Test
    fun `invoke with pageSize over 100 uses maximum 100`() = runTest {
        // Given
        val mockProducts = createMockProducts(count = 100)
        whenever(repository.getProducts(page = 1, pageSize = 100, categoryId = null))
            .thenReturn(Result.Success(mockProducts))
        
        // When
        val result = useCase(pageSize = 500)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(result is Result.Success)
        verify(repository).getProducts(page = 1, pageSize = 100, categoryId = null)
    }
    
    @Test
    fun `invoke with large dataset returns all products`() = runTest {
        // Given
        val largeList = createMockProducts(count = 1000)
        whenever(repository.getProducts(page = 1, pageSize = 100, categoryId = null))
            .thenReturn(Result.Success(largeList))
        
        // When
        val result = useCase(pageSize = 100)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(1000, (result as Result.Success).data.size)
    }
    
    // ==================== ERROR HANDLING TESTS ====================
    
    @Test
    fun `invoke returns error when network fails`() = runTest {
        // Given
        val networkError = AppError.NetworkError(
            exception = IOException("Connection failed")
        )
        whenever(repository.getProducts(any(), any(), any()))
            .thenReturn(Result.Error(networkError))
        
        // When
        val result = useCase()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(result is Result.Error)
        assertEquals(networkError, (result as Result.Error).error)
    }
    
    @Test
    fun `invoke returns error when server returns 500`() = runTest {
        // Given
        val serverError = AppError.ServerError(
            statusCode = 500,
            serverMessage = "Internal Server Error",
            code = "INTERNAL_SERVER_ERROR",
            userMessage = "خطا سرور"
        )
        whenever(repository.getProducts(any(), any(), any()))
            .thenReturn(Result.Error(serverError))
        
        // When
        val result = useCase()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(result is Result.Error)
        assertEquals(500, ((result as Result.Error).error as AppError.ServerError).statusCode)
    }
    
    @Test
    fun `invoke handles timeout error correctly`() = runTest {
        // Given
        val timeoutError = AppError.TimeoutError(
            exception = SocketTimeoutException("Request timeout")
        )
        whenever(repository.getProducts(any(), any(), any()))
            .thenReturn(Result.Error(timeoutError))
        
        // When
        val result = useCase()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).error is AppError.TimeoutError)
    }
    
    @Test
    fun `invoke returns error with recovery strategy`() = runTest {
        // Given
        val networkError = AppError.NetworkError(
            exception = IOException("No internet")
        )
        val recovery = com.noghre.sod.domain.common.ErrorRecovery.ManualRetry(
            message = "Please check your connection",
            retryAction = { useCase() }
        )
        whenever(repository.getProducts(any(), any(), any()))
            .thenReturn(Result.Error(networkError, recovery = recovery))
        
        // When
        val result = useCase()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).recovery is com.noghre.sod.domain.common.ErrorRecovery.ManualRetry)
    }
    
    // ==================== CONCURRENCY TESTS ====================
    
    @Test
    fun `multiple concurrent calls execute independently`() = runTest {
        // Given
        val page1Products = createMockProducts(count = 5, startId = 1)
        val page2Products = createMockProducts(count = 5, startId = 6)
        
        whenever(repository.getProducts(page = 1, pageSize = 20, categoryId = null))
            .thenReturn(Result.Success(page1Products))
        whenever(repository.getProducts(page = 2, pageSize = 20, categoryId = null))
            .thenReturn(Result.Success(page2Products))
        
        // When
        val result1 = useCase(page = 1)
        val result2 = useCase(page = 2)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(result1 is Result.Success)
        assertTrue(result2 is Result.Success)
        assertFalse((result1 as Result.Success).data == (result2 as Result.Success).data)
    }
    
    // ==================== HELPER FUNCTIONS ====================
    
    private fun createMockProducts(
        count: Int,
        startId: Int = 1
    ): List<Product> {
        return List(count) { index ->
            Product(
                id = "product-${startId + index}",
                name = "Product ${startId + index}",
                price = (100 + index) * 1000L,
                imageUrl = "https://example.com/image${index}.jpg",
                rating = 4.0f + (index % 5) * 0.2f,
                reviewCount = 10 + index,
                stock = 5 + index,
                isFavorite = false
            )
        }
    }
}
