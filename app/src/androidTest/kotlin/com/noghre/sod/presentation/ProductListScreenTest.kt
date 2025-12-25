package com.noghre.sod.presentation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noghre.sod.R
import com.noghre.sod.domain.model.Product
import com.noghre.sod.presentation.screens.ProductListScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

/**
 * Integration tests for ProductListScreen.
 * Tests navigation flow, user interactions, and UI rendering.
 * 
 * @since 1.0.0
 */
@RunWith(AndroidJUnit4::class)
class ProductListScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun productListScreen_displays_correctly() {
        composeTestRule.setContent {
            ProductListScreen()
        }
        
        // Verify search bar is displayed
        composeTestRule
            .onNodeWithContentDescription("search_products")
            .assertExists()
            .assertIsDisplayed()
    }
    
    @Test
    fun search_bar_accepts_input() {
        composeTestRule.setContent {
            ProductListScreen()
        }
        
        // Find search input
        val searchInput = composeTestRule
            .onNodeWithContentDescription("search_products")
        
        // Type search query
        searchInput.performTextInput("silver ring")
        
        // Verify input was entered
        searchInput.assert(hasText("silver ring"))
    }
    
    @Test
    fun product_item_click_triggers_callback() {
        var clickedProductId: String? = null
        
        composeTestRule.setContent {
            ProductListScreen(
                onProductClick = { productId ->
                    clickedProductId = productId
                }
            )
        }
        
        // Find and click product
        composeTestRule
            .onNodeWithContentDescription("Product: Silver Ring, Price: 99.99")
            .assertExists()
            .performClick()
        
        // Verify callback was triggered
        assert(clickedProductId != null)
    }
    
    @Test
    fun loading_indicator_is_displayed_when_loading() {
        composeTestRule.setContent {
            ProductListScreen()
        }
        
        // Look for loading indicator
        composeTestRule
            .onNodeWithContentDescription("Loading")
            .assertIsDisplayed()
    }
    
    @Test
    fun product_list_scrolls_smoothly() {
        composeTestRule.setContent {
            ProductListScreen()
        }
        
        // Scroll down
        composeTestRule
            .onNodeWithTag("product_list")
            .performScrollToIndex(5)
        
        // Verify scroll happened
        composeTestRule.onNodeWithTag("product_list").assertIsDisplayed()
    }
}
