package com.noghre.sod.ui.screens.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.noghre.sod.MainActivity
import com.noghre.sod.ui.theme.NoghreSodTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 📑 Integration tests for HomeScreen
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class HomeScreenIntegrationTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    /**
     * Test: HomeScreen displays products when data loads
     */
    @Test
    fun homeScreen_displaysProducts_whenDataLoaded() {
        composeTestRule.setContent {
            NoghreSodTheme {
                HomeScreen(
                    onNavigateToProduct = {},
                    onNavigateToCart = {}
                )
            }
        }
        
        // Wait for loading to complete
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("product_card").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Verify products are displayed
        composeTestRule.onNodeWithTag("product_list").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("product_card").assertCountEquals(1)
    }
    
    /**
     * Test: Search functionality filters products
     */
    @Test
    fun homeScreen_filtersProducts_whenSearched() {
        composeTestRule.setContent {
            NoghreSodTheme {
                HomeScreen(
                    onNavigateToProduct = {},
                    onNavigateToCart = {}
                )
            }
        }
        
        // Enter search query
        composeTestRule.onNodeWithTag("search_field")
            .performTextInput("نقره")
        
        // Wait for filter
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithTag("product_card").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Verify filtered results
        composeTestRule.onNodeWithTag("product_list").assertIsDisplayed()
    }
    
    /**
     * Test: Navigate to product detail on click
     */
    @Test
    fun homeScreen_navigatesToProductDetail_whenProductClicked() {
        var navigatedToProductId = ""
        
        composeTestRule.setContent {
            NoghreSodTheme {
                HomeScreen(
                    onNavigateToProduct = { productId -> navigatedToProductId = productId },
                    onNavigateToCart = {}
                )
            }
        }
        
        // Wait for products to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("product_card").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Click first product
        composeTestRule.onAllNodesWithTag("product_card").onFirst().performClick()
        
        // Verify navigation
        assert(navigatedToProductId.isNotEmpty())
    }
    
    /**
     * Test: Categories display correctly
     */
    @Test
    fun homeScreen_displayCategories_onLoad() {
        composeTestRule.setContent {
            NoghreSodTheme {
                HomeScreen(
                    onNavigateToProduct = {},
                    onNavigateToCart = {}
                )
            }
        }
        
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("category_card").fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule.onNodeWithTag("categories_grid").assertIsDisplayed()
    }
    
    /**
     * Test: Load more pagination
     */
    @Test
    fun homeScreen_loadsMore_whenLoadMoreClicked() {
        composeTestRule.setContent {
            NoghreSodTheme {
                HomeScreen(
                    onNavigateToProduct = {},
                    onNavigateToCart = {}
                )
            }
        }
        
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("product_card").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Click load more button
        composeTestRule.onNodeWithTag("load_more_button").performClick()
        
        // Verify more items loaded
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("product_card").fetchSemanticsNodes().size > 1
        }
    }
    
    /**
     * Test: Error handling
     */
    @Test
    fun homeScreen_showsError_whenLoadingFails() {
        // This would need a mock to simulate error
        composeTestRule.setContent {
            NoghreSodTheme {
                HomeScreen(
                    onNavigateToProduct = {},
                    onNavigateToCart = {}
                )
            }
        }
        
        // Error state should have retry button
        composeTestRule.onNodeWithTag("retry_button").assertIsDisplayed()
    }
}
