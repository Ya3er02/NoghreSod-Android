package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.noghre.sod.data.model.NetworkResult
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.usecase.GetProductsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertIs

/**
 * Unit tests for ProductsViewModel.
 *
 * Tests the following scenarios:
 * - Loading products successfully
 * - Handling network errors
 * - Handling empty product lists
 * - Filtering products
 * - Sorting products
 * - Retry functionality
 *
 * @author Test Suite
 */
@ExperimentalCoroutinesApi
class ProductsViewModelTest {
    
    private lateinit var viewModel: ProductsViewModel
    private lateinit var getProductsUseCase: GetProductsUseCase
    private val testDispatcher = StandardTestDispatcher()
    
    // Sample test data
    private val sampleProducts = listOf(
        Product(
            id = "1",
            name = "Silver Ring",
            price = 150000f,
            image = "https://example.com/ring.jpg",
            category = "Rings",
            description = "Beautiful silver ring"
        ),
        Product(
            id = "2",
            name = "Silver Necklace",
            price = 250000f,
            image = "https://example.com/necklace.jpg",
            category = "Necklaces",
            description = "Elegant silver necklace"
        ),
        Product(
            id = "3",
            name = "Silver Bracelet",
            price = 180000f,
            image = "https://example.com/bracelet.jpg",
            category = "Bracelets",
            description = "Stylish silver bracelet"
        )
    )
    
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getProductsUseCase = mockk()
        
        viewModel = ProductsViewModel(
            getProductsUseCase = getProductsUseCase,
            savedStateHandle = SavedStateHandle()
        )
    }
    
    @Test
    fun `loadProducts should update state with success`() = runTest {
        // Arrange
        coEvery { getProductsUseCase() } returns flowOf(
            NetworkResult.Loading(),
            NetworkResult.Success(sampleProducts)
        )
        
        // Act
        viewModel.loadProducts()
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(null, uiState.error)
        assertEquals(sampleProducts, uiState.products)
        coVerify { getProductsUseCase() }
    }
    
    @Test
    fun `loadProducts should handle error state`() = runTest {
        // Arrange
        val errorMessage = "خطای شبکه. لطفاً اتصال اینترنت خود را بررسی کنید"
        coEvery { getProductsUseCase() } returns flowOf(
            NetworkResult.Loading(),
            NetworkResult.Error(
                code = 500,
                message = errorMessage
            )
        )
        
        // Act
        viewModel.loadProducts()
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(errorMessage, uiState.error)
        assertEquals(emptyList<Product>(), uiState.products)
    }
    
    @Test
    fun `loadProducts should show loading state initially`() = runTest {
        // Arrange
        coEvery { getProductsUseCase() } returns flowOf(
            NetworkResult.Loading()
        )
        
        // Act
        viewModel.loadProducts()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(true, uiState.isLoading)
        assertEquals(null, uiState.error)
    }
    
    @Test
    fun `loadProducts should handle empty product list`() = runTest {
        // Arrange
        coEvery { getProductsUseCase() } returns flowOf(
            NetworkResult.Loading(),
            NetworkResult.Success(emptyList())
        )
        
        // Act
        viewModel.loadProducts()
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(null, uiState.error)
        assertEquals(true, uiState.products.isEmpty())
    }
    
    @Test
    fun `filterProducts should filter by category`() = runTest {
        // Arrange
        coEvery { getProductsUseCase() } returns flowOf(
            NetworkResult.Success(sampleProducts)
        )
        viewModel.loadProducts()
        advanceUntilIdle()
        
        // Act
        viewModel.filterProducts(category = "Rings")
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(1, uiState.products.size)
        assertEquals("Silver Ring", uiState.products[0].name)
    }
    
    @Test
    fun `sortProducts should sort by price ascending`() = runTest {
        // Arrange
        coEvery { getProductsUseCase() } returns flowOf(
            NetworkResult.Success(sampleProducts)
        )
        viewModel.loadProducts()
        advanceUntilIdle()
        
        // Act
        viewModel.sortProducts(sortBy = "price_asc")
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(150000f, uiState.products[0].price)
        assertEquals(250000f, uiState.products[2].price)
    }
    
    @Test
    fun `searchProducts should filter by name`() = runTest {
        // Arrange
        coEvery { getProductsUseCase() } returns flowOf(
            NetworkResult.Success(sampleProducts)
        )
        viewModel.loadProducts()
        advanceUntilIdle()
        
        // Act
        viewModel.searchProducts(query = "Ring")
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(1, uiState.products.size)
        assertEquals(true, uiState.products[0].name.contains("Ring"))
    }
    
    @Test
    fun `retryLoadProducts should call useCase again`() = runTest {
        // Arrange
        coEvery { getProductsUseCase() } returns flowOf(
            NetworkResult.Error(code = 500, message = "Server Error")
        )
        viewModel.loadProducts()
        advanceUntilIdle()
        
        // Act
        coEvery { getProductsUseCase() } returns flowOf(
            NetworkResult.Success(sampleProducts)
        )
        viewModel.retryLoadProducts()
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(null, uiState.error)
        assertEquals(sampleProducts, uiState.products)
        coVerify(exactly = 2) { getProductsUseCase() }
    }
}
