package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.ProductSummary
import com.noghre.sod.domain.usecase.GetProductsUseCase
import com.noghre.sod.utils.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @MockK
    private lateinit var getProductsUseCase: GetProductsUseCase

    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        viewModel = HomeViewModel(getProductsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadFeaturedProducts_Success() = runTest {
        // Arrange
        val mockProducts = listOf(
            ProductSummary(
                id = "1",
                name = "Test Product",
                price = 100.0,
                weight = 5.0,
                purity = "925",
                rating = 4.5,
                thumbnailImage = "test.jpg"
            )
        )
        coEvery { getProductsUseCase(any(), any(), any()) } returns Result.Success(mockProducts)

        // Act
        advanceUntilIdle()
        val uiState = viewModel.uiState.first()

        // Assert
        assert(uiState.featuredProducts.isNotEmpty())
        assert(!uiState.isLoading)
        coVerify { getProductsUseCase(any(), any(), any()) }
    }

    @Test
    fun testLoadFeaturedProducts_Error() = runTest {
        // Arrange
        coEvery { getProductsUseCase(any(), any(), any()) } returns Result.Failure(
            Exception("Network error")
        )

        // Act
        advanceUntilIdle()
        val uiState = viewModel.uiState.first()

        // Assert
        assert(uiState.featuredProducts.isEmpty())
        assert(uiState.error != null)
        assert(!uiState.isLoading)
    }
}
