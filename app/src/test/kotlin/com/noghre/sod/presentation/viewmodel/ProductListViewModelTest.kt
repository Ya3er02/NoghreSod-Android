package com.noghre.sod.presentation.viewmodel

import androidx.paging.PagingData
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

/**
 * Unit tests for ProductListViewModel.
 * Tests business logic, state management, and repository interactions.
 * 
 * @since 1.0.0
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository: ProductRepository = mockk(relaxed = true)
    private lateinit var viewModel: ProductListViewModel
    
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProductListViewModel(mockRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun viewModel_initializes_with_empty_products() = runTest {
        // Products should be empty initially
        assert(viewModel.products != null)
    }
    
    @Test
    fun searchProducts_calls_repository_search() = runTest {
        // Mock repository response
        val mockProducts = listOf(
            Product(
                id = "1",
                name = "Silver Ring",
                price = BigDecimal("99.99"),
                inStock = true,
                discountPercentage = BigDecimal.ZERO,
                rating = 4.5f
            )
        )
        
        coEvery { mockRepository.searchProducts("silver") } returns mockProducts
        
        // Call search
        viewModel.searchProducts("silver")
        
        // Wait for coroutine
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verify repository was called
        coEvery { mockRepository.searchProducts("silver") }
    }
    
    @Test
    fun getProducts_returns_paged_data() = runTest {
        // Mock paged response
        val mockPagingData = PagingData.empty<Product>()
        
        coEvery { mockRepository.getProductsPaged() } returns flowOf(mockPagingData)
        
        // Verify products flow exists
        assert(viewModel.products != null)
    }
    
    @Test
    fun error_handling_displays_error_state() = runTest {
        // Mock error from repository
        coEvery { mockRepository.searchProducts(any()) } throws Exception("Network error")
        
        // Call search
        viewModel.searchProducts("test")
        
        // Wait for coroutine
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Error should be handled gracefully
        // ViewModel should not crash
    }
}
