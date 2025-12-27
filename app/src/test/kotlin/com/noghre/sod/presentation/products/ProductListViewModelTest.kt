package com.noghre.sod.presentation.products

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.noghre.sod.core.error.AppError
import com.noghre.sod.core.error.GlobalExceptionHandler
import com.noghre.sod.core.util.Result
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import com.noghre.sod.presentation.common.UiEvent
import com.noghre.sod.presentation.common.UiState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * 🪵 ProductListViewModel Unit Tests
 * 
 * Tests ViewModel state management, events, and error handling.
 */
class ProductListViewModelTest {
    
    private lateinit var repository: ProductRepository
    private lateinit var exceptionHandler: GlobalExceptionHandler
    private lateinit var viewModel: ProductListViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        exceptionHandler = GlobalExceptionHandler()
        viewModel = ProductListViewModel(repository, exceptionHandler)
    }
    
    @After
    fun tearDown() {
        clearAllMocks()
    }
    
    // ============================================
    // LOADING STATE TESTS
    // ============================================
    
    @Test
    fun `initial state is Idle`() = runTest {
        // Initial state should be Idle before any operation
        viewModel.uiState.test {
            val state = awaitItem()
            assertIs<UiState.Idle>(state)
        }
    }
    
    @Test
    fun `loadProducts sets Loading state`() = runTest {
        // Arrange
        val mockProducts = listOf(
            Product(
                id = "1",
                name = "نقره",
                description = "نقره خالص",
                price = 100000.0,
                images = emptyList(),
                rating = 4.5,
                reviewCount = 10,
                categoryId = "cat1",
                stock = 50,
                sku = "sku1",
                createdAt = "",
                updatedAt = ""
            )
        )
        
        coEvery { repository.getProducts() } returns Result.Success(mockProducts)
        
        // Act & Assert
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)
            
            // First emission after load: Loading
            assertEquals(UiState.Loading, awaitItem())
            
            // Second emission: Success
            val successState = awaitItem()
            assertIs<UiState.Success<List<Product>>>(successState)
            assertEquals(1, successState.data.size)
        }
    }
    
    // ============================================
    // SUCCESS CASES
    // ============================================
    
    @Test
    fun `loadProducts emits Success with data`() = runTest {
        // Arrange
        val mockProducts = listOf(
            Product(
                id = "1",
                name = "براسلت",
                description = "براسلت نقره",
                price = 50000.0,
                images = emptyList(),
                rating = 4.2,
                reviewCount = 5,
                categoryId = "cat2",
                stock = 20,
                sku = "sku2",
                createdAt = "",
                updatedAt = ""
            )
        )
        
        coEvery { repository.getProducts() } returns Result.Success(mockProducts)
        
        // Act
        viewModel.loadProducts()
        
        // Assert
        viewModel.uiState.test {
            skipItems(2)  // Skip Idle and Loading
            val state = awaitItem()
            assertIs<UiState.Success<List<Product>>>(state)
            assertEquals(1, state.data.size)
            assertEquals("براسلت", state.data[0].name)
        }
    }
    
    @Test
    fun `searchProducts filters and emits results`() = runTest {
        // Arrange
        val query = "test"
        val mockResults = listOf(
            Product(
                id = "1",
                name = "test product",
                description = "محصول تست",
                price = 75000.0,
                images = emptyList(),
                rating = 3.8,
                reviewCount = 3,
                categoryId = "cat3",
                stock = 15,
                sku = "sku3",
                createdAt = "",
                updatedAt = ""
            )
        )
        
        coEvery { repository.searchProducts(query) } returns Result.Success(mockResults)
        
        // Act
        viewModel.searchProducts(query)
        
        // Assert - Check UI state
        viewModel.uiState.test {
            skipItems(2)  // Skip Idle and Loading
            val state = awaitItem()
            assertIs<UiState.Success<List<Product>>>(state)
            assertEquals(1, state.data.size)
            assertEquals("test product", state.data[0].name)
        }
        
        // Assert - Check search query state
        assertEquals(query, viewModel.searchQuery.value)
    }
    
    // ============================================
    // ERROR CASES
    // ============================================
    
    @Test
    fun `loadProducts emits Error on failure`() = runTest {
        // Arrange
        val error = AppError.Network("Network error", 500)
        coEvery { repository.getProducts() } returns Result.Error(error)
        
        // Act
        viewModel.loadProducts()
        
        // Assert
        viewModel.uiState.test {
            skipItems(2)  // Skip Idle and Loading
            val state = awaitItem()
            assertIs<UiState.Error>(state)
            assertEquals(error, state.error)
        }
    }
    
    @Test
    fun `loadProducts emits Empty when no products`() = runTest {
        // Arrange
        coEvery { repository.getProducts() } returns Result.Success(emptyList())
        
        // Act
        viewModel.loadProducts()
        
        // Assert
        viewModel.uiState.test {
            skipItems(2)  // Skip Idle and Loading
            val state = awaitItem()
            assertIs<UiState.Empty>(state)
        }
    }
    
    @Test
    fun `error event is sent on failure`() = runTest {
        // Arrange
        val error = AppError.Validation("بخش بنام الزامی", "name")
        coEvery { repository.getProducts() } returns Result.Error(error)
        
        // Act
        viewModel.loadProducts()
        
        // Assert - Check event
        viewModel.events.test {
            val event = awaitItem()
            assertIs<UiEvent.ShowError>(event)
            assertEquals(error, event.error)
        }
    }
    
    // ============================================
    // EVENT TESTS
    // ============================================
    
    @Test
    fun `goToProductDetail sends Navigate event`() = runTest {
        // Arrange
        val productId = "123"
        
        // Act
        viewModel.goToProductDetail(productId)
        
        // Assert
        viewModel.events.test {
            val event = awaitItem()
            assertIs<UiEvent.Navigate>(event)
            assertEquals("products/$productId", event.route)
        }
    }
    
    @Test
    fun `toggleFavorite sends Toast event`() = runTest {
        // Arrange
        val product = Product(
            id = "1",
            name = "نقره",
            description = "نقره خالص",
            price = 100000.0,
            images = emptyList(),
            rating = 4.5,
            reviewCount = 10,
            isFavorite = false,
            categoryId = "cat1",
            stock = 50,
            sku = "sku1",
            createdAt = "",
            updatedAt = ""
        )
        
        coEvery { repository.addFavorite("1") } returns Result.Success(Unit)
        
        // Act
        viewModel.toggleFavorite(product)
        
        // Assert
        viewModel.events.test {
            val event = awaitItem()
            assertIs<UiEvent.ShowToast>(event)
            assertEquals("افزوده شد", event.message)
        }
    }
    
    // ============================================
    // FILTER TESTS
    // ============================================
    
    @Test
    fun `filterByCategory updates selected category`() = runTest {
        // Arrange
        val categoryId = "jewelry"
        val mockProducts = emptyList<Product>()
        coEvery { repository.getProductsByCategory(categoryId) } returns Result.Success(mockProducts)
        
        // Act
        viewModel.filterByCategory(categoryId)
        
        // Assert
        assertEquals(categoryId, viewModel.selectedCategory.value)
    }
    
    @Test
    fun `filterByCategory with null resets filter`() = runTest {
        // Arrange
        coEvery { repository.getProducts() } returns Result.Success(emptyList())
        
        // Act
        viewModel.filterByCategory(null)
        
        // Assert
        assertEquals(null, viewModel.selectedCategory.value)
    }
    
    // ============================================
    // RETRY TESTS
    // ============================================
    
    @Test
    fun `retry reloads products`() = runTest {
        // Arrange
        val mockProducts = listOf(
            Product(
                id = "1",
                name = "نقره",
                description = "نقره",
                price = 100000.0,
                images = emptyList(),
                rating = 4.5,
                reviewCount = 10,
                categoryId = "cat1",
                stock = 50,
                sku = "sku1",
                createdAt = "",
                updatedAt = ""
            )
        )
        coEvery { repository.getProducts() } returns Result.Success(mockProducts)
        
        // Act
        viewModel.retry()
        
        // Assert
        viewModel.uiState.test {
            skipItems(2)  // Skip Idle and Loading
            val state = awaitItem()
            assertIs<UiState.Success<List<Product>>>(state)
        }
    }
}
