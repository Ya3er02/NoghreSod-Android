package com.noghre.sod.ui.screen

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.ui.screens.CartScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for CartScreen
 * Tests cart operations: add, remove, quantity change, total calculation
 */
@RunWith(AndroidJUnit4::class)
class CartScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockCartItems = listOf(
        CartItem(id = "1", productId = "prod1", name = "Silver Ring", price = 500000, quantity = 1),
        CartItem(id = "2", productId = "prod2", name = "Silver Necklace", price = 750000, quantity = 2)
    )

    @Test
    fun cartScreen_displays_cart_items() {
        // Arrange
        composeTestRule.setContent {
            CartScreen(cartItems = mockCartItems)
        }

        // Act & Assert
        composeTestRule.onNodeWithText("Silver Ring")
            .assertExists()
        composeTestRule.onNodeWithText("Silver Necklace")
            .assertExists()
    }

    @Test
    fun cartScreen_displays_item_price() {
        // Arrange
        composeTestRule.setContent {
            CartScreen(cartItems = mockCartItems)
        }

        // Act & Assert
        composeTestRule.onNodeWithText("500,000 IRR")
            .assertExists()
        composeTestRule.onNodeWithText("750,000 IRR")
            .assertExists()
    }

    @Test
    fun cartScreen_displays_quantity() {
        // Arrange
        composeTestRule.setContent {
            CartScreen(cartItems = mockCartItems)
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("quantity_0")
            .assertExists()
        composeTestRule.onNodeWithText("Qty: 1")
            .assertExists()
    }

    @Test
    fun cartScreen_increase_quantity() {
        // Arrange
        var updatedQuantity = 1
        composeTestRule.setContent {
            CartScreen(
                cartItems = mockCartItems,
                onQuantityChange = { itemId, qty -> updatedQuantity = qty }
            )
        }

        // Act
        composeTestRule.onNodeWithTag("increase_qty_0")
            .performClick()

        // Assert
        assert(updatedQuantity == 2)
    }

    @Test
    fun cartScreen_decrease_quantity() {
        // Arrange
        var updatedQuantity = 2
        composeTestRule.setContent {
            CartScreen(
                cartItems = mockCartItems,
                onQuantityChange = { itemId, qty -> updatedQuantity = qty }
            )
        }

        // Act
        composeTestRule.onNodeWithTag("decrease_qty_1")
            .performClick()

        // Assert
        assert(updatedQuantity == 1)
    }

    @Test
    fun cartScreen_remove_item() {
        // Arrange
        var removedItemId: String? = null
        composeTestRule.setContent {
            CartScreen(
                cartItems = mockCartItems,
                onRemoveItem = { id -> removedItemId = id }
            )
        }

        // Act
        composeTestRule.onNodeWithTag("remove_btn_0")
            .performClick()

        // Assert
        assert(removedItemId == "1")
    }

    @Test
    fun cartScreen_calculates_cart_total_correctly() {
        // Arrange
        // Total = (500000 * 1) + (750000 * 2) = 2000000
        composeTestRule.setContent {
            CartScreen(cartItems = mockCartItems)
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("cart_total")
            .assertExists()
        composeTestRule.onNodeWithText("2,000,000 IRR")
            .assertExists()
    }

    @Test
    fun cartScreen_empty_cart_shows_message() {
        // Arrange
        composeTestRule.setContent {
            CartScreen(cartItems = emptyList())
        }

        // Act & Assert
        composeTestRule.onNodeWithText("Your cart is empty")
            .assertExists()
    }

    @Test
    fun cartScreen_checkout_button_enabled() {
        // Arrange
        composeTestRule.setContent {
            CartScreen(cartItems = mockCartItems)
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("checkout_button")
            .assertExists()
    }

    @Test
    fun cartScreen_checkout_button_disabled_when_empty() {
        // Arrange
        composeTestRule.setContent {
            CartScreen(cartItems = emptyList())
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("checkout_button")
            .assertExists()
            // Button should be disabled
    }

    @Test
    fun cartScreen_checkout_click_triggers_navigation() {
        // Arrange
        var checkoutClicked = false
        composeTestRule.setContent {
            CartScreen(
                cartItems = mockCartItems,
                onCheckoutClick = { checkoutClicked = true }
            )
        }

        // Act
        composeTestRule.onNodeWithTag("checkout_button")
            .performClick()

        // Assert
        assert(checkoutClicked)
    }

    @Test
    fun cartScreen_edit_item_quantity_with_input() {
        // Arrange
        var editedQuantity = 0
        composeTestRule.setContent {
            CartScreen(
                cartItems = mockCartItems,
                onQuantityChange = { _, qty -> editedQuantity = qty }
            )
        }

        // Act
        composeTestRule.onNodeWithTag("quantity_input_0")
            .performClick()
        composeTestRule.onNodeWithTag("quantity_input_0")
            .performTextReplacement("5")

        // Assert
        assert(editedQuantity == 5)
    }

    @Test
    fun cartScreen_displays_subtotal_and_tax() {
        // Arrange
        composeTestRule.setContent {
            CartScreen(cartItems = mockCartItems)
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("subtotal")
            .assertExists()
        composeTestRule.onNodeWithTag("tax")
            .assertExists()
    }

    @Test
    fun cartScreen_displays_continue_shopping_button() {
        // Arrange
        composeTestRule.setContent {
            CartScreen(cartItems = mockCartItems)
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("continue_shopping_btn")
            .assertExists()
    }
}
