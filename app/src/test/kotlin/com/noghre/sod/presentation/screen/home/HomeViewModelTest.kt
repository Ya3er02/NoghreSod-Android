package com.noghre.sod.presentation.screen.home

import app.cash.turbine.test
import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.ProductSummary
import com.noghre.sod.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for HomeViewModel.
 * Tests data loading, error handling, and state management.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
    private lateinit var productRepository: ProductRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        productRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadData should emit loading then success state`() = runTest {
        // Given
        val mockProducts = listOf(
            createMockProduct("1", "Product 1"),
            createMockProduct("2", "Product 2")
        )
        coEvery { productRepository.getProducts(0, 10) } returns
            flowOf(Result.Success(mockProducts))

        // When
        viewModel = HomeViewModel(productRepository)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(2, state.featuredProducts.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadData should emit error state on failure`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { productRepository.getProducts(0, 10) } returns
            flowOf(Result.Error(Exception(errorMessage), errorMessage))

        // When
        viewModel = HomeViewModel(productRepository)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Error)
            assertEquals(errorMessage, state.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadData retry should reload data`() = runTest {
        // Given
        val mockProducts = listOf(createMockProduct("1", "Product 1"))
        coEvery { productRepository.getProducts(0, 10) } returns
            flowOf(Result.Success(mockProducts))

        // When
        viewModel = HomeViewModel(productRepository)
        advanceUntilIdle()
        viewModel.loadData()
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial state should be loading`() = runTest {
        // Given
        coEvery { productRepository.getProducts(0, 10) } returns
            flowOf(Result.Loading)

        // When
        viewModel = HomeViewModel(productRepository)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createMockProduct(id: String, name: String): ProductSummary {
        return mockk(relaxed = true) {
            coEvery { this@mockk.id } returns id
            coEvery { this@mockk.name } returns name
        }
    }
}
