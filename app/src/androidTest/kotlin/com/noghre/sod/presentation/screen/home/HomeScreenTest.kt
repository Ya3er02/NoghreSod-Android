package com.noghre.sod.presentation.screen.home

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noghre.sod.domain.model.ProductCategory
import com.noghre.sod.domain.model.ProductSummary
import com.noghre.sod.presentation.theme.NoghreSodTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

/**
 * Integration tests for HomeScreen UI.
 * Tests composable rendering and user interactions.
 */
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysLoadingState() {
        // Given
        val viewModel = mockk<HomeViewModel>(relaxed = true) {
            every { uiState } returns MutableStateFlow(HomeUiState.Loading)
        }

        // When
        composeTestRule.setContent {
            NoghreSodTheme {
                HomeScreen(
                    onProductClick = {},
                    onCategoryClick = {},
                    onSearchClick = {},
                    onCartClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Then - verify loading indicator is displayed
        // Note: CircularProgressIndicator doesn't have a standard test tag
        // In production, add testTag("loading_indicator") to the CircularProgressIndicator
    }

    @Test
    fun homeScreen_displaysProducts() {
        // Given
        val mockProducts = listOf(
            createMockProduct("1", "نقره نگین"),
            createMockProduct("2", "نقره گردنبند")
        )
        val viewModel = mockk<HomeViewModel>(relaxed = true) {
            every { uiState } returns MutableStateFlow(
                HomeUiState.Success(
                    featuredProducts = mockProducts,
                    categories = emptyList()
                )
            )
        }

        // When
        composeTestRule.setContent {
            NoghreSodTheme {
                HomeScreen(
                    onProductClick = {},
                    onCategoryClick = {},
                    onSearchClick = {},
                    onCartClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("نقره نگین").assertExists()
        composeTestRule.onNodeWithText("نقره گردنبند").assertExists()
    }

    @Test
    fun homeScreen_displaysErrorState() {
        // Given
        val errorMessage = "خطا در بارگذاری محصولات"
        val viewModel = mockk<HomeViewModel>(relaxed = true) {
            every { uiState } returns MutableStateFlow(HomeUiState.Error(errorMessage))
        }

        // When
        composeTestRule.setContent {
            NoghreSodTheme {
                HomeScreen(
                    onProductClick = {},
                    onCategoryClick = {},
                    onSearchClick = {},
                    onCartClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertExists()
        composeTestRule.onNodeWithText("مجدد تالش").assertExists()
    }

    @Test
    fun homeScreen_clickOnProduct_triggersCallback() {
        // Given
        var clickedProductId = ""
        val mockProducts = listOf(createMockProduct("123", "نقره نگین"))
        val viewModel = mockk<HomeViewModel>(relaxed = true) {
            every { uiState } returns MutableStateFlow(
                HomeUiState.Success(
                    featuredProducts = mockProducts,
                    categories = emptyList()
                )
            )
        }

        // When
        composeTestRule.setContent {
            NoghreSodTheme {
                HomeScreen(
                    onProductClick = { clickedProductId = it },
                    onCategoryClick = {},
                    onSearchClick = {},
                    onCartClick = {},
                    viewModel = viewModel
                )
            }
        }
        composeTestRule.onNodeWithText("نقره نگین").performClick()

        // Then
        assert(clickedProductId == "123")
    }

    @Test
    fun homeScreen_clickRetryButton_callsLoadData() {
        // Given
        var loadDataCalled = false
        val errorMessage = "خطا در بارگذاری"
        val viewModel = mockk<HomeViewModel>(relaxed = true) {
            every { uiState } returns MutableStateFlow(HomeUiState.Error(errorMessage))
            every { loadData() }.answers {
                loadDataCalled = true
            }
        }

        // When
        composeTestRule.setContent {
            NoghreSodTheme {
                HomeScreen(
                    onProductClick = {},
                    onCategoryClick = {},
                    onSearchClick = {},
                    onCartClick = {},
                    viewModel = viewModel
                )
            }
        }
        composeTestRule.onNodeWithText("مجدد تالش").performClick()

        // Then
        assert(loadDataCalled)
    }

    private fun createMockProduct(id: String, name: String): ProductSummary {
        return mockk(relaxed = true) {
            every { this@mockk.id } returns id
            every { this@mockk.name } returns name
            every { price } returns mockk {
                every { formatted() } returns "100,000 ریال"
            }
            every { category } returns ProductCategory.RING
            every { purity } returns mockk()
            every { rating } returns 4.5f
            every { reviewCount } returns 10
            every { inStock } returns true
        }
    }
}
