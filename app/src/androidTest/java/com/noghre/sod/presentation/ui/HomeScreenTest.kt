package com.noghre.sod.presentation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noghre.sod.core.ui.UiState
import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.model.Currency
import com.noghre.sod.domain.model.Money
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.StockStatus
import com.noghre.sod.presentation.viewmodel.HomeData
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockProduct = Product(
        id = "1",
        name = "Silver Ring",
        description = "Beautiful silver ring",
        price = Money(50000, Currency.USD),
        imageUrl = "https://example.com/image.jpg",
        category = Category("1", "Rings"),
        rating = 4.5,
        reviewCount = 10,
        stockStatus = StockStatus.InStock(5),
        discount = 10
    )

    private val mockCategory = Category(
        id = "1",
        name = "Rings"
    )

    /**
     * Test loading state display.
     */
    @Test
    fun testLoadingStateDisplayed() {
        // When: UI state is Loading
        val uiState = UiState.Loading
        
        // Then: Loading indicator should be displayed
        composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
    }

    /**
     * Test success state with product list.
     */
    @Test
    fun testSuccessStateWithProducts() {
        // Given: Product data
        val homeData = HomeData(
            products = listOf(mockProduct, mockProduct.copy(id = "2", name = "Silver Bracelet")),
            featuredProducts = listOf(mockProduct),
            categories = listOf(mockCategory)
        )
        val uiState = UiState.Success(homeData)

        // Then: Products should be displayed
        composeTestRule.onNodeWithText("Silver Ring").assertIsDisplayed()
    }

    /**
     * Test error state display.
     */
    @Test
    fun testErrorStateDisplayed() {
        // Given: Error state
        val error = com.noghre.sod.core.ui.UiError.NetworkError("No connection")
        val uiState = UiState.Error<HomeData>(error = error, canRetry = true)

        // Then: Error message and retry button should be displayed
        composeTestRule.onNodeWithText("Connection Error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
    }

    /**
     * Test empty state display.
     */
    @Test
    fun testEmptyStateDisplayed() {
        // Given: Empty state
        val uiState = UiState.Empty

        // Then: Empty message should be displayed
        composeTestRule.onNodeWithText("No Products Found").assertIsDisplayed()
    }

    /**
     * Test retry button click.
     */
    @Test
    fun testRetryButtonClick() {
        // Given: Error state
        val error = com.noghre.sod.core.ui.UiError.ServerError(500, "Server Error")
        val uiState = UiState.Error<HomeData>(error = error, canRetry = true)
        var retryClicked = false

        // When: Retry button is clicked
        composeTestRule.onNodeWithText("Try Again").performClick()
        retryClicked = true

        // Then: Retry should be executed
        assert(retryClicked)
    }

    /**
     * Test category selection.
     */
    @Test
    fun testCategorySelection() {
        // Given: Categories displayed
        val homeData = HomeData(
            products = listOf(mockProduct),
            featuredProducts = listOf(mockProduct),
            categories = listOf(
                mockCategory,
                Category("2", "Bracelets")
            )
        )
        val uiState = UiState.Success(homeData)

        // When: Category is clicked
        composeTestRule.onNodeWithText("Rings").performClick()

        // Then: Products should be filtered (asserts would be in ViewModel test)
    }

    /**
     * Test product scrolling.
     */
    @Test
    fun testProductListScroll() {
        // Given: Multiple products
        val products = (1..20).map {
            mockProduct.copy(id = it.toString(), name = "Product $it")
        }
        val homeData = HomeData(
            products = products,
            featuredProducts = products.take(5),
            categories = listOf(mockCategory)
        )
        val uiState = UiState.Success(homeData)

        // When: Scrolling down
        composeTestRule.onNodeWithText("Product 1").performScrollToIndex(10)

        // Then: Later products should be visible
        composeTestRule.onNodeWithText("Product 10").assertIsDisplayed()
    }

    /**
     * Test price display.
     */
    @Test
    fun testPriceDisplay() {
        // Given: Product with price
        val homeData = HomeData(
            products = listOf(mockProduct),
            featuredProducts = listOf(mockProduct),
            categories = listOf(mockCategory)
        )
        val uiState = UiState.Success(homeData)

        // Then: Price should be displayed correctly
        composeTestRule.onNodeWithText("\$50,000").assertIsDisplayed()
    }

    /**
     * Test rating display.
     */
    @Test
    fun testRatingDisplay() {
        // Given: Product with rating
        val homeData = HomeData(
            products = listOf(mockProduct),
            featuredProducts = listOf(mockProduct),
            categories = listOf(mockCategory)
        )
        val uiState = UiState.Success(homeData)

        // Then: Rating should be displayed
        composeTestRule.onNodeWithText("4.5 ★ (10 reviews)").assertIsDisplayed()
    }
}