package com.noghre.sod.presentation.viewmodel

import com.noghre.sod.core.error.AppException
import com.noghre.sod.core.util.Result
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import com.noghre.sod.presentation.common.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ProductListViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantExecutorRule()
    
    private lateinit var viewModel: ProductListViewModel
    private val productRepository = mockk<ProductRepository>()
    
    @Before
    fun setUp() {
        viewModel = ProductListViewModel(productRepository)
    }
    
    @Test
    fun loadProducts_success() = runTest {
        // Arrange
        val products = listOf(
            Product(
                id = "1",
                name = "Test Product 1",
                price = 100.0,
                description = "Test",
                image = "image1.jpg",
                isFavorite = false
            ),
            Product(
                id = "2",
                name = "Test Product 2",
                price = 200.0,
                description = "Test",
                image = "image2.jpg",
                isFavorite = false
            )
        )
        
        coEvery { productRepository.getProducts(1) } returns Result.Success(products)
        
        // Act
        viewModel.loadProducts()
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.uiState.value
        assert(state is UiState.Success)
        assert((state as UiState.Success).data.size == 2)
    }
    
    @Test
    fun loadProducts_error() = runTest {
        // Arrange
        val error = AppException.NetworkException("Network error")
        coEvery { productRepository.getProducts(1) } returns Result.Error(error)
        
        // Act
        viewModel.loadProducts()
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.uiState.value
        assert(state is UiState.Error)
    }
    
    @Test
    fun loadProducts_empty() = runTest {
        // Arrange
        coEvery { productRepository.getProducts(1) } returns Result.Success(emptyList())
        
        // Act
        viewModel.loadProducts()
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.uiState.value
        assert(state is UiState.Empty)
    }
    
    @Test
    fun searchProducts_success() = runTest {
        // Arrange
        val products = listOf(
            Product(
                id = "1",
                name = "Silver Ring",
                price = 150.0,
                description = "Test",
                image = "ring.jpg",
                isFavorite = false
            )
        )
        
        coEvery { productRepository.searchProducts("silver") } returns Result.Success(products)
        
        // Act
        viewModel.searchProducts("silver")
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.uiState.value
        assert(state is UiState.Success)
        assert((state as UiState.Success).data[0].name.contains("Silver"))
    }
}
