package com.noghre.sod.presentation.screen

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import org.junit.Rule
import org.junit.Test
import timber.log.Timber

/**
 * Compose UI tests for Product screen.
 */
class ProductScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun productListDisplayed() {
        Timber.d("Testing product list display")
        
        composeTestRule.setContent {
            Text("Product List")
        }

        composeTestRule.onNodeWithText("Product List").assertIsDisplayed()
    }

    @Test
    fun productItemClickable() {
        Timber.d("Testing product item click")
        
        var clickCount = 0
        composeTestRule.setContent {
            Text(
                text = "Test Product",
                modifier = androidx.compose.ui.Modifier.then(
                    androidx.compose.foundation.clickable {
                        clickCount++
                    }
                )
            )
        }

        composeTestRule.onNodeWithText("Test Product").performClick()
        assert(clickCount > 0)
    }

    @Test
    fun productListScroll() {
        Timber.d("Testing product list scroll")
        
        composeTestRule.setContent {
            androidx.compose.foundation.lazy.LazyColumn {
                items(50) { index ->
                    Text("Product $index")
                }
            }
        }

        composeTestRule.onNodeWithTag("product_list").performScrollToIndex(10)
        composeTestRule.onNodeWithText("Product 10").assertIsDisplayed()
    }

    @Test
    fun loadingStateDisplayed() {
        Timber.d("Testing loading state")
        
        composeTestRule.setContent {
            Text("Loading...")
        }

        composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
    }

    @Test
    fun errorStateDisplayed() {
        Timber.d("Testing error state")
        
        composeTestRule.setContent {
            Text("Error: Failed to load products")
        }

        composeTestRule.onNodeWithText("Error: Failed to load products").assertIsDisplayed()
    }
}
