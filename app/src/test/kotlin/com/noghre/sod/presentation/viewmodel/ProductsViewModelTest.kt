package com.noghre.sod.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.noghre.sod.domain.common.AppError
import com.noghre.sod.domain.common.Result
import com.noghre.sod.domain.repository.ProductRepository
import com.noghre.sod.domain.usecase.GetProductsUseCase
import com.noghre.sod.presentation.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Comprehensive ViewModel Tests using Turbine for Flow collection
 * 
 * Test Coverage:
 * - State transitions (Idle → Loading → Success)
 * - Multiple state changes in sequence
 * - Error handling
 * - Event emissions
 * - Loading with data (refresh)
 * 
 * @since 1.0.0
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ProductsViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @Mock
    private lateinit var getProductsUseCase: GetProductsUseCase
    
    @Mock
    private lateinit var repository: ProductRepository
    
    private lateinit var viewModel: ProductsViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProductsViewModel(
            getProductsUseCase = getProductsUseCase,
            ioDispatcher = testDispatcher
        )
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    // ==================== STATE TRANSITION TESTS ====================
    
    @Test
    fun `loadProducts shows loading then success states`() = runTest {
        // Given
        val mockProducts = createMockProducts(3)
        whenever(getProductsUseCase(1, 20, null))
            .thenReturn(Result.Success(mockProducts))
        
        // When & Then
        viewModel.uiState.test {
            // Initial state
            val idle = awaitItem()
            assertTrue(idle is UiState.Idle)
            
            // Trigger load
            viewModel.loadProducts()
            advanceUntilIdle()
            
            // Loading state
            val loading = awaitItem()
            assertTrue(loading is UiState.Loading)
            
            // Success state
            val success = awaitItem()
            assertTrue(success is UiState.Success)
            assertEquals(3, (success as UiState.Success).data.products.size)
            
            expectNoEvents()
        }
    }
    
    @Test
    fun `loadProducts with error shows error state`() = runTest {
        // Given
        val error = AppError.NetworkError(IOException("No connection"))
        whenever(getProductsUseCase(any(), any(), any()))
            .thenReturn(Result.Error(error))
        
        // When & Then
        viewModel.uiState.test {
            skipItems(1) // Skip idle
            
            viewModel.loadProducts()
            advanceUntilIdle()
            
            val loading = awaitItem()
            assertTrue(loading is UiState.Loading)
            
            val errorState = awaitItem()
            assertTrue(errorState is UiState.Error)
            assertEquals(error, (errorState as UiState.Error).error)
            
            expectNoEvents()
        }
    }
    
    @Test
    fun `refresh with existing data shows LoadingWithData state`() = runTest {
        // Given
        val initialProducts = createMockProducts(5)
        val newProducts = createMockProducts(7)
        
        whenever(getProductsUseCase(1, 20, null))
            .thenReturn(Result.Success(initialProducts))
            .thenReturn(Result.Success(newProducts))
        
        // When & Then
        viewModel.uiState.test {
            skipItems(1) // Skip idle
            
            // Initial load
            viewModel.loadProducts()
            advanceUntilIdle()
            
            skipItems(1) // Skip loading
            val initialSuccess = awaitItem()
            assertTrue(initialSuccess is UiState.Success)
            
            // Refresh
            viewModel.refresh()
            advanceUntilIdle()
            
            val loadingWithData = awaitItem()
            assertTrue(loadingWithData is UiState.LoadingWithData)
            assertEquals(5, (loadingWithData as UiState.LoadingWithData).data.products.size)
            
            val refreshedSuccess = awaitItem()
            assertTrue(refreshedSuccess is UiState.Success)
            assertEquals(7, (refreshedSuccess as UiState.Success).data.products.size)
            
            expectNoEvents()
        }
    }
    
    @Test
    fun `error with cached data shows ErrorWithData state`() = runTest {
        // Given
        val cachedProducts = createMockProducts(3)
        val error = AppError.ServerError(
            statusCode = 500,
            serverMessage = "Server error",
            code = "SERVER_ERROR",
            userMessage = "خطای سرور"
        )
        
        whenever(getProductsUseCase(any(), any(), any()))
            .thenReturn(Result.Error(error, data = cachedProducts))
        
        // When & Then
        viewModel.uiState.test {
            skipItems(1) // Skip idle
            
            viewModel.loadProducts()
            advanceUntilIdle()
            
            skipItems(1) // Skip loading
            val errorWithData = awaitItem()
            
            assertTrue(errorWithData is UiState.ErrorWithData)
            val typedError = errorWithData as UiState.ErrorWithData<*>
            assertEquals(3, typedError.data.products.size)
            assertEquals(error, typedError.error)
            
            expectNoEvents()
        }
    }
    
    @Test
    fun `empty result shows Empty state`() = runTest {
        // Given
        whenever(getProductsUseCase(any(), any(), any()))
            .thenReturn(Result.Success(emptyList()))
        
        // When & Then
        viewModel.uiState.test {
            skipItems(1) // Skip idle
            
            viewModel.loadProducts()
            advanceUntilIdle()
            
            skipItems(1) // Skip loading
            val empty = awaitItem()
            
            assertTrue(empty is UiState.Empty)
            
            expectNoEvents()
        }
    }
    
    // ==================== PAGINATION TESTS ====================
    
    @Test
    fun `loadMoreProducts appends to existing products`() = runTest {
        // Given
        val page1 = createMockProducts(20, startId = 1)
        val page2 = createMockProducts(20, startId = 21)
        
        whenever(getProductsUseCase(1, 20, null))
            .thenReturn(Result.Success(page1))
        whenever(getProductsUseCase(2, 20, null))
            .thenReturn(Result.Success(page2))
        
        // When & Then
        viewModel.uiState.test {
            skipItems(1) // Skip idle
            
            viewModel.loadProducts()
            advanceUntilIdle()
            skipItems(1) // Skip loading
            skipItems(1) // Skip first success
            
            viewModel.loadMoreProducts()
            advanceUntilIdle()
            
            val paginatedState = awaitItem()
            assertTrue(paginatedState is UiState.Success)
            assertEquals(40, (paginatedState as UiState.Success).data.products.size)
            
            expectNoEvents()
        }
    }
    
    @Test
    fun `loadMoreProducts does not load when no more pages`() = runTest {
        // Given
        val products = createMockProducts(5) // Less than page size
        whenever(getProductsUseCase(any(), any(), any()))
            .thenReturn(Result.Success(products))
        
        // When & Then
        viewModel.uiState.test {
            skipItems(1) // Skip idle
            
            viewModel.loadProducts()
            advanceUntilIdle()
            skipItems(2) // Skip loading and success
            
            viewModel.loadMoreProducts()
            advanceUntilIdle()
            
            expectNoEvents() // No new state emitted
        }
    }
    
    // ==================== FILTER TESTS ====================
    
    @Test
    fun `applying filter updates products`() = runTest {
        // Given
        val filteredProducts = createMockProducts(10)
        whenever(getProductsUseCase(1, 20, "category-123"))
            .thenReturn(Result.Success(filteredProducts))
        
        // When & Then
        viewModel.uiState.test {
            skipItems(1) // Skip idle
            
            viewModel.applyFilter(categoryId = "category-123")
            advanceUntilIdle()
            
            skipItems(1) // Skip loading
            val success = awaitItem()
            
            assertTrue(success is UiState.Success)
            assertEquals(10, (success as UiState.Success).data.products.size)
            
            expectNoEvents()
        }
    }
    
    // ==================== EVENT TESTS ====================
    
    @Test
    fun `onProductClick sends navigation event`() = runTest {
        // When & Then
        viewModel.events.test {
            viewModel.onProductClick("product-123")
            
            val event = awaitItem()
            assertTrue(event is com.noghre.sod.presentation.common.UiEvent.Navigation.ToProductDetail)
            assertEquals(
                "product-123",
                (event as com.noghre.sod.presentation.common.UiEvent.Navigation.ToProductDetail).productId
            )
            
            expectNoEvents()
        }
    }
    
    // ==================== HELPER FUNCTIONS ====================
    
    private fun createMockProducts(
        count: Int,
        startId: Int = 1
    ): List<com.noghre.sod.domain.model.Product> {
        return List(count) { index ->
            com.noghre.sod.domain.model.Product(
                id = "product-${startId + index}",
                name = "Product ${startId + index}",
                price = (100 + index) * 1000L,
                imageUrl = "https://example.com/image${index}.jpg",
                rating = 4.0f,
                reviewCount = 100,
                stock = 10,
                isFavorite = false
            )
        }
    }
}
