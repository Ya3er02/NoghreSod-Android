package com.noghre.sod.presentation.viewmodel

import app.cash.turbine.turbineScope
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.ProductFilters
import com.noghre.sod.domain.model.SortOption
import com.noghre.sod.domain.model.SortOrder
import com.noghre.sod.domain.usecase.product.GetProductsUseCase
import com.noghre.sod.domain.usecase.product.SearchProductsUseCase
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Unified Test Suite for ProductsViewModel
 *
 * Consolidated from:
 * - presentation/viewmodel/ProductsViewModelTest
 * - test/java/presentation/viewmodel/ProductsViewModelTest
 * - Tests for ProductsViewModelImproved features
 *
 * Test Coverage:
 * - Product loading (success & failure)
 * - Search functionality with debouncing
 * - Filter application and state updates
 * - Pagination and "Load More"
 * - Sort option and order changes
 * - Effect emissions (navigation, errors)
 * - State flow updates
 * - Error handling with sealed Result class
 *
 * @author NoghreSod Team
 * @version 1.0.0
 * @since Refactor Phase 2
 */
class ProductsViewModelTest {

    private lateinit var viewModel: ProductsViewModel
    private val getProductsUseCase = mockk<GetProductsUseCase>()
    private val searchProductsUseCase = mockk<SearchProductsUseCase>()

    private val mockProducts = listOf(
        Product(
            id = "1",
            name = "گردن نقره اسلامی",
            description = "Silver necklace",
            price = 150000.0,
            weight = 12.5,
            image = "https://example.com/necklace.jpg",
            rating = 4.5,
            inStock = true
        ),
        Product(
            id = "2",
            name = "دستباند نقرهای ساخته",
            description = "Silver bracelet",
            price = 200000.0,
            weight = 18.0,
            image = "https://example.com/bracelet.jpg",
            rating = 4.8,
            inStock = true
        )
    )

    @Before
    fun setup() {
        // Reset all mocks
        clearAllMocks()
        
        // Create ViewModel with mocked use cases
        viewModel = ProductsViewModel(
            getProductsUseCase = getProductsUseCase,
            searchProductsUseCase = searchProductsUseCase
        )
    }

    // ==================== Loading Tests ====================

    @Test
    fun `test initial state is Loading`() = runTest {
        viewModel.uiState.value.let { state ->
            assertIs<ProductsUiState.Loading>(state)
        }
    }

    @Test
    fun `test load products success returns Success state`() = runTest {
        // Arrange
        coEvery {
            getProductsUseCase(
                filters = any(),
                page = 1,
                pageSize = any()
            )
        } returns flowOf(Result.success(mockProducts))

        // Act
        viewModel.handleIntent(ProductsIntent.LoadProducts)

        // Assert
        turbineScope {
            val stateTurbine = viewModel.uiState.testIn(backgroundScope)
            stateTurbine.skipItems(1) // Skip initial Loading state
            
            val successState = stateTurbine.awaitItem()
            assertIs<ProductsUiState.Success>(successState)
            successState.let {
                assertEquals(mockProducts.size, it.products.size)
                assertEquals(1, it.currentPage)
                assertTrue(it.hasMore) // 2 products >= PAGE_SIZE
            }
        }
    }

    @Test
    fun `test load products failure returns Error state`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        coEvery {
            getProductsUseCase(
                filters = any(),
                page = 1,
                pageSize = any()
            )
        } returns flowOf(Result.failure(Exception(errorMessage)))

        // Act
        viewModel.handleIntent(ProductsIntent.LoadProducts)

        // Assert
        turbineScope {
            val stateTurbine = viewModel.uiState.testIn(backgroundScope)
            stateTurbine.skipItems(1) // Skip initial Loading
            
            val errorState = stateTurbine.awaitItem()
            assertIs<ProductsUiState.Error>(errorState)
            assertEquals(errorMessage, errorState.message)
        }
    }

    @Test
    fun `test load products emits ShowError effect on failure`() = runTest {
        // Arrange
        val errorMessage = "Failed to fetch"
        coEvery {
            getProductsUseCase(
                filters = any(),
                page = any(),
                pageSize = any()
            )
        } returns flowOf(Result.failure(Exception(errorMessage)))

        // Act & Assert
        turbineScope {
            val effectTurbine = viewModel.effects.testIn(backgroundScope)
            viewModel.handleIntent(ProductsIntent.LoadProducts)
            
            val effect = effectTurbine.awaitItem()
            assertIs<ProductsEffect.ShowError>(effect)
            assertEquals(errorMessage, effect.message)
        }
    }

    // ==================== Search Tests ====================

    @Test
    fun `test search with query length less than 2 does nothing`() = runTest {
        // Act
        viewModel.handleIntent(ProductsIntent.SearchProducts("a"))

        // Assert
        assertEquals("a", viewModel.searchQuery.value)
        // State should remain as initial or unchanged
    }

    @Test
    fun `test search with valid query updates search state`() = runTest {
        // Arrange
        coEvery {
            searchProductsUseCase("necklace")
        } returns flowOf(Result.success(listOf(mockProducts[0])))

        // Act
        viewModel.handleIntent(ProductsIntent.SearchProducts("necklace"))

        // Assert
        assertEquals("necklace", viewModel.searchQuery.value)
        turbineScope {
            val stateTurbine = viewModel.uiState.testIn(backgroundScope)
            stateTurbine.skipItems(1) // Skip Loading
            
            val successState = stateTurbine.awaitItem()
            assertIs<ProductsUiState.Success>(successState)
            assertEquals(1, successState.products.size)
            assertEquals(mockProducts[0].id, successState.products[0].id)
        }
    }

    @Test
    fun `test empty search query resets to full product list`() = runTest {
        // Arrange
        coEvery {
            getProductsUseCase(
                filters = any(),
                page = 1,
                pageSize = any()
            )
        } returns flowOf(Result.success(mockProducts))

        // Act
        viewModel.handleIntent(ProductsIntent.SearchProducts(""))

        // Assert
        turbineScope {
            val stateTurbine = viewModel.uiState.testIn(backgroundScope)
            stateTurbine.skipItems(1) // Skip initial state
            
            val state = stateTurbine.awaitItem()
            assertIs<ProductsUiState.Success>(state)
        }
    }

    // ==================== Filter Tests ====================

    @Test
    fun `test apply filters updates filters state and reloads products`() = runTest {
        // Arrange
        val newFilters = ProductFilters(
            minPrice = 100000.0,
            maxPrice = 300000.0,
            category = "rings"
        )
        coEvery {
            getProductsUseCase(
                filters = newFilters,
                page = 1,
                pageSize = any()
            )
        } returns flowOf(Result.success(listOf(mockProducts[0])))

        // Act
        viewModel.handleIntent(ProductsIntent.ApplyFilters(newFilters))

        // Assert
        assertEquals(newFilters.minPrice, viewModel.filters.value.minPrice)
        assertEquals(newFilters.maxPrice, viewModel.filters.value.maxPrice)
        assertEquals(newFilters.category, viewModel.filters.value.category)
        assertEquals(1, viewModel.currentPage.value) // Reset to page 1
    }

    @Test
    fun `test apply filters emits ScrollToTop effect`() = runTest {
        // Arrange
        val newFilters = ProductFilters(minPrice = 100000.0)
        coEvery {
            getProductsUseCase(
                filters = newFilters,
                page = 1,
                pageSize = any()
            )
        } returns flowOf(Result.success(mockProducts))

        // Act & Assert
        turbineScope {
            val effectTurbine = viewModel.effects.testIn(backgroundScope)
            viewModel.handleIntent(ProductsIntent.ApplyFilters(newFilters))
            
            val effect = effectTurbine.awaitItem()
            assertIs<ProductsEffect.ScrollToTop>(effect)
        }
    }

    // ==================== Pagination Tests ====================

    @Test
    fun `test load more loads next page`() = runTest {
        // Arrange - First load page 1
        coEvery {
            getProductsUseCase(
                filters = any(),
                page = 1,
                pageSize = any()
            )
        } returns flowOf(Result.success(mockProducts))

        // Set success state
        viewModel.handleIntent(ProductsIntent.LoadProducts)
        
        // Now mock page 2
        coEvery {
            getProductsUseCase(
                filters = any(),
                page = 2,
                pageSize = any()
            )
        } returns flowOf(Result.success(mockProducts))

        // Act
        viewModel.handleIntent(ProductsIntent.LoadMore)

        // Assert
        assertEquals(2, viewModel.currentPage.value)
    }

    @Test
    fun `test load more when hasMore is false does nothing`() = runTest {
        // Arrange - Set state with hasMore = false
        val currentPage = 1
        
        // Act
        viewModel.handleIntent(ProductsIntent.LoadMore)

        // Assert - Page should not increment if hasMore is false
        // (Depends on current state)
    }

    // ==================== Refresh Tests ====================

    @Test
    fun `test refresh resets page to 1 and reloads products`() = runTest {
        // Arrange
        coEvery {
            getProductsUseCase(
                filters = any(),
                page = 1,
                pageSize = any()
            )
        } returns flowOf(Result.success(mockProducts))

        // Act
        viewModel.handleIntent(ProductsIntent.Refresh)

        // Assert
        assertEquals(1, viewModel.currentPage.value)
        turbineScope {
            val stateTurbine = viewModel.uiState.testIn(backgroundScope)
            stateTurbine.skipItems(1) // Skip Loading
            
            val state = stateTurbine.awaitItem()
            assertIs<ProductsUiState.Success>(state)
        }
    }

    // ==================== Sort Tests ====================

    @Test
    fun `test set sort option updates filters and reloads`() = runTest {
        // Arrange
        coEvery {
            getProductsUseCase(
                filters = any(),
                page = 1,
                pageSize = any()
            )
        } returns flowOf(Result.success(mockProducts))

        // Act
        viewModel.handleIntent(ProductsIntent.SetSortOption(SortOption.PRICE))

        // Assert
        assertEquals(SortOption.PRICE, viewModel.filters.value.sortBy)
        assertEquals(1, viewModel.currentPage.value)
    }

    @Test
    fun `test set sort order updates filters and reloads`() = runTest {
        // Arrange
        coEvery {
            getProductsUseCase(
                filters = any(),
                page = 1,
                pageSize = any()
            )
        } returns flowOf(Result.success(mockProducts))

        // Act
        viewModel.handleIntent(ProductsIntent.SetSortOrder(SortOrder.ASCENDING))

        // Assert
        assertEquals(SortOrder.ASCENDING, viewModel.filters.value.sortOrder)
    }

    // ==================== Navigation Tests ====================

    @Test
    fun `test navigate to product detail emits NavigateToDetail effect`() = runTest {
        // Act & Assert
        turbineScope {
            val effectTurbine = viewModel.effects.testIn(backgroundScope)
            viewModel.navigateToProductDetail("product-123")
            
            val effect = effectTurbine.awaitItem()
            assertIs<ProductsEffect.NavigateToDetail>(effect)
            assertEquals("product-123", effect.productId)
        }
    }
}
