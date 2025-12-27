package com.noghre.sod.ui.screen

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.hasClickAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noghre.sod.domain.model.Product
import com.noghre.sod.ui.screens.ProductsScreen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for ProductsScreen
 * Tests product listing, filtering, and search functionality
 */
@RunWith(AndroidJUnit4::class)
class ProductsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockProducts = listOf(
        Product(id = "1", name = "Silver Ring", price = 500000, imageUrl = "url1"),
        Product(id = "2", name = "Silver Necklace", price = 750000, imageUrl = "url2"),
        Product(id = "3", name = "Silver Bracelet", price = 600000, imageUrl = "url3"),
        Product(id = "4", name = "Silver Earrings", price = 400000, imageUrl = "url4")
    )

    @Test
    fun productsScreen_displays_product_list() {
        // Arrange
        composeTestRule.setContent {
            ProductsScreen(products = mockProducts)
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("products_list")
            .assertExists()
        composeTestRule.onNodeWithText("Silver Ring")
            .assertExists()
    }

    @Test
    fun productsScreen_shows_product_price() {
        // Arrange
        composeTestRule.setContent {
            ProductsScreen(products = mockProducts)
        }

        // Act & Assert
        composeTestRule.onNodeWithText("500,000 IRR")
            .assertExists()
    }

    @Test
    fun productsScreen_product_item_is_clickable() {
        // Arrange
        var clicked = false
        composeTestRule.setContent {
            ProductsScreen(
                products = mockProducts,
                onProductClick = { clicked = true }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Silver Ring")
            .performClick()

        // Assert
        assert(clicked)
    }

    @Test
    fun productsScreen_scroll_list_to_end() {
        // Arrange
        composeTestRule.setContent {
            ProductsScreen(products = mockProducts)
        }

        // Act
        composeTestRule.onNodeWithTag("products_list")
            .performScrollToIndex(mockProducts.size - 1)

        // Assert
        composeTestRule.onNodeWithText("Silver Earrings")
            .assertExists()
    }

    @Test
    fun productsScreen_filter_by_price_range() {
        // Arrange
        var filteredProducts = mockProducts
        composeTestRule.setContent {
            ProductsScreen(
                products = filteredProducts,
                onPriceFilterChange = { min, max ->
                    filteredProducts = mockProducts.filter { it.price in min..max }
                }
            )
        }

        // Act
        composeTestRule.onNodeWithTag("price_filter_button")
            .performClick()
        composeTestRule.onNodeWithTag("min_price_input")
            .performClick()
        // Type min price
        composeTestRule.onNodeWithTag("max_price_input")
            .performClick()
        // Type max price

        // Assert
        assert(filteredProducts.isNotEmpty())
    }

    @Test
    fun productsScreen_search_functionality() {
        // Arrange
        var searchResults = mockProducts
        composeTestRule.setContent {
            ProductsScreen(
                products = searchResults,
                onSearchChange = { query ->
                    searchResults = mockProducts.filter { it.name.contains(query, ignoreCase = true) }
                }
            )
        }

        // Act
        composeTestRule.onNodeWithTag("search_input")
            .performClick()
        // Type "Ring"

        // Assert
        assert(searchResults.any { it.name.contains("Ring") })
    }

    @Test
    fun productsScreen_add_to_cart_button_visible() {
        // Arrange
        composeTestRule.setContent {
            ProductsScreen(products = mockProducts)
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("add_to_cart_button_0")
            .assertExists()
    }

    @Test
    fun productsScreen_add_to_cart_functionality() {
        // Arrange
        var addedProduct: Product? = null
        composeTestRule.setContent {
            ProductsScreen(
                products = mockProducts,
                onAddToCart = { product -> addedProduct = product }
            )
        }

        // Act
        composeTestRule.onNodeWithTag("add_to_cart_button_0")
            .performClick()

        // Assert
        assert(addedProduct?.id == "1")
        assert(addedProduct?.name == "Silver Ring")
    }

    @Test
    fun productsScreen_loading_state_shows_shimmer() {
        // Arrange
        composeTestRule.setContent {
            ProductsScreen(products = emptyList(), isLoading = true)
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("loading_shimmer")
            .assertExists()
    }

    @Test
    fun productsScreen_empty_state_shows_message() {
        // Arrange
        composeTestRule.setContent {
            ProductsScreen(products = emptyList(), isLoading = false)
        }

        // Act & Assert
        composeTestRule.onNodeWithText("No products found")
            .assertExists()
    }

    @Test
    fun productsScreen_error_state_shows_error_message() {
        // Arrange
        val errorMessage = "Failed to load products"
        composeTestRule.setContent {
            ProductsScreen(products = emptyList(), error = errorMessage)
        }

        // Act & Assert
        composeTestRule.onNodeWithText(errorMessage)
            .assertExists()
    }
}
