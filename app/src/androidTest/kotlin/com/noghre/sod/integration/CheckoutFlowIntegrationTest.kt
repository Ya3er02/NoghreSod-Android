package com.noghre.sod.integration

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.model.Product
import com.noghre.sod.ui.screens.CheckoutFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for complete checkout flow
 * End-to-end testing from cart to order confirmation
 */
@RunWith(AndroidJUnit4::class)
class CheckoutFlowIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockProducts = listOf(
        Product(id = "1", name = "Silver Ring", price = 500000, imageUrl = "url1"),
        Product(id = "2", name = "Silver Necklace", price = 750000, imageUrl = "url2")
    )

    private val mockCartItems = listOf(
        CartItem(id = "1", productId = "prod1", name = "Silver Ring", price = 500000, quantity = 1),
        CartItem(id = "2", productId = "prod2", name = "Silver Necklace", price = 750000, quantity = 1)
    )

    @Test
    fun complete_checkout_flow_success() {
        // Arrange
        var checkoutCompleted = false
        var orderNumber = ""

        composeTestRule.setContent {
            CheckoutFlow(
                cartItems = mockCartItems,
                onCheckoutComplete = { orderId ->
                    checkoutCompleted = true
                    orderNumber = orderId
                }
            )
        }

        // Act - Step 1: Review Cart
        composeTestRule.onNodeWithText("Silver Ring").assertExists()
        composeTestRule.onNodeWithText("Silver Necklace").assertExists()

        // Step 2: Select Shipping
        composeTestRule.onNodeWithText("Standard").performClick()
        composeTestRule.onNodeWithTag("continue_btn").performClick()

        // Step 3: Enter Shipping Address
        composeTestRule.onNodeWithTag("address_input").performTextInput("123 Main St")
        composeTestRule.onNodeWithTag("city_input").performTextInput("Tehran")
        composeTestRule.onNodeWithTag("postal_code_input").performTextInput("12345")
        composeTestRule.onNodeWithTag("continue_btn").performClick()

        // Step 4: Apply Promo Code
        composeTestRule.onNodeWithTag("promo_input").performTextInput("SAVE20")
        composeTestRule.onNodeWithTag("apply_promo_btn").performClick()

        // Step 5: Select Payment Method
        composeTestRule.onNodeWithText("Credit Card").performClick()
        composeTestRule.onNodeWithTag("card_number_input").performTextInput("4111111111111111")
        composeTestRule.onNodeWithTag("expiry_input").performTextInput("12/25")
        composeTestRule.onNodeWithTag("cvv_input").performTextInput("123")

        // Step 6: Place Order
        composeTestRule.onNodeWithTag("place_order_btn").performClick()

        // Assert
        composeTestRule.waitUntil(timeoutMillis = 5000) { checkoutCompleted }
        assert(checkoutCompleted)
        assert(orderNumber.isNotEmpty())
    }

    @Test
    fun checkout_flow_with_address_validation() {
        // Arrange
        composeTestRule.setContent {
            CheckoutFlow(cartItems = mockCartItems)
        }

        // Act & Assert - Invalid address
        composeTestRule.onNodeWithTag("address_input").performTextInput("")
        composeTestRule.onNodeWithTag("continue_btn").performClick()

        composeTestRule.onNodeWithText("Address is required").assertExists()
    }

    @Test
    fun checkout_flow_with_payment_validation() {
        // Arrange
        composeTestRule.setContent {
            CheckoutFlow(cartItems = mockCartItems)
        }

        // Navigate to payment
        composeTestRule.onNodeWithTag("address_input").performTextInput("123 Main St")
        composeTestRule.onNodeWithTag("city_input").performTextInput("Tehran")
        composeTestRule.onNodeWithTag("postal_code_input").performTextInput("12345")
        composeTestRule.onNodeWithTag("continue_btn").performClick()

        // Try invalid card
        composeTestRule.onNodeWithTag("card_number_input").performTextInput("1234567890123456")
        composeTestRule.onNodeWithTag("continue_btn").performClick()

        composeTestRule.onNodeWithText("Invalid card number").assertExists()
    }

    @Test
    fun checkout_flow_promo_code_discount() {
        // Arrange
        composeTestRule.setContent {
            CheckoutFlow(
                cartItems = mockCartItems,
                onPromoApplied = { discount -> assert(discount > 0) }
            )
        }

        // Navigate to promo
        composeTestRule.onNodeWithText("Standard").performClick()
        composeTestRule.onNodeWithTag("continue_btn").performClick()
        composeTestRule.onNodeWithTag("address_input").performTextInput("123 Main St")
        composeTestRule.onNodeWithTag("continue_btn").performClick()

        // Apply promo
        composeTestRule.onNodeWithTag("promo_input").performTextInput("SAVE20")
        composeTestRule.onNodeWithTag("apply_promo_btn").performClick()

        // Verify discount applied
        composeTestRule.onNodeWithTag("discount_amount").assertExists()
    }

    @Test
    fun checkout_flow_order_summary() {
        // Arrange
        composeTestRule.setContent {
            CheckoutFlow(cartItems = mockCartItems)
        }

        // Act & Assert
        composeTestRule.onNodeWithText("Order Summary").assertExists()
        composeTestRule.onNodeWithText("2 Items").assertExists()
        composeTestRule.onNodeWithTag("subtotal").assertExists()
        composeTestRule.onNodeWithTag("tax").assertExists()
        composeTestRule.onNodeWithTag("shipping_cost").assertExists()
        composeTestRule.onNodeWithTag("final_total").assertExists()
    }
}
