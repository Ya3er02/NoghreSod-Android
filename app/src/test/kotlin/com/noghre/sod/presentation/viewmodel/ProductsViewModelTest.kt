package com.noghre.sod.presentation.viewmodel

import app.cash.turbine.test
import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.usecase.product.GetCategoriesUseCase
import com.noghre.sod.domain.usecase.product.GetProductsUseCase
import com.noghre.sod.presentation.products.ProductsUiState
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class ProductsViewModelTest {

    @MockK
    private lateinit var getProductsUseCase: GetProductsUseCase

    @MockK
    private lateinit var getCategoriesUseCase: GetCategoriesUseCase

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ProductsViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProducts success updates state`() = runTest {
        val sampleProducts = listOf(
            Product(id = "1", name = "کمر نقرهای", price = 500000.0, images = listOf(), rating = 4.5f, reviewCount = 10, stock = 5),
            Product(id = "2", name = "رابط نقرهای", price = 300000.0, images = listOf(), rating = 4.0f, reviewCount = 5, stock = 10)
        )

        coEvery { getProductsUseCase(any()) } returns sampleProducts

        viewModel = ProductsViewModel(
            getProductsUseCase = { getProductsUseCase },
            getCategoriesUseCase = { getCategoriesUseCase }
        )

        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            val state = awaitItem()
            assert(state.products == sampleProducts)
            assert(!state.isLoading)
        }
    }

    @Test
    fun `loadProducts error shows error message`() = runTest {
        val exception = Exception("Network Error")
        coEvery { getProductsUseCase(any()) } throws exception

        viewModel = ProductsViewModel(
            getProductsUseCase = { getProductsUseCase },
            getCategoriesUseCase = { getCategoriesUseCase }
        )

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            assert(state.error != null)
            assert(!state.isLoading)
        }
    }
}
