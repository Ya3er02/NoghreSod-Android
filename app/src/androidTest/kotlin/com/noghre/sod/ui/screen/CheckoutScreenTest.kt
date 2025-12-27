package com.noghre.sod.ui.screen

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noghre.sod.domain.model.ShippingMethod
import com.noghre.sod.ui.screens.CheckoutScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for CheckoutScreen
 * Tests complete checkout flow including payment and order confirmation
 */
@RunWith(AndroidJUnit4::class)
class CheckoutScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun checkoutScreen_displays_order_summary() {
        // Arrange
        composeTestRule.setContent {
            CheckoutScreen(
                subtotal = 2000000,
                tax = 200000,
                shipping = 50000
            )
        }

        // Act & Assert
        composeTestRule.onNodeWithText("Order Summary")
            .assertExists()
        composeTestRule.onNodeWithText("2,000,000 IRR")
            .assertExists()
    }

    @Test
    fun checkoutScreen_displays_shipping_methods() {
        // Arrange
        composeTestRule.setContent {
            CheckoutScreen(
                subtotal = 2000000,
                shippingMethods = listOf(
                    ShippingMethod(name = "Standard", price = 50000),
                    ShippingMethod(name = "Express", price = 100000)
                )
            )
        }

        // Act & Assert
        composeTestRule.onNodeWithText("Standard")
            .assertExists()
        composeTestRule.onNodeWithText("Express")
            .assertExists()
    }

    @Test
    fun checkoutScreen_select_shipping_method() {
        // Arrange
        var selectedMethod = ""
        composeTestRule.setContent {
            CheckoutScreen(
                subtotal = 2000000,
                onShippingMethodSelect = { method -> selectedMethod = method.name }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Express")
            .performClick()

        // Assert
        assert(selectedMethod == "Express")
    }

    @Test
    fun checkoutScreen_displays_promo_code_input() {
        // Arrange
        composeTestRule.setContent {
            CheckoutScreen(subtotal = 2000000)
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("promo_code_input")
            .assertExists()
    }

    @Test
    fun checkoutScreen_apply_promo_code() {
        // Arrange
        var appliedCode = ""
        composeTestRule.setContent {
            CheckoutScreen(
                subtotal = 2000000,
                onApplyPromo = { code -> appliedCode = code }
            )
        }

        // Act
        composeTestRule.onNodeWithTag("promo_code_input")
            .performTextInput("SAVE20")
        composeTestRule.onNodeWithTag("apply_promo_btn")
            .performClick()

        // Assert
        assert(appliedCode == "SAVE20")
    }

    @Test
    fun checkoutScreen_promo_code_shows_discount() {
        // Arrange
        composeTestRule.setContent {
            CheckoutScreen(
                subtotal = 2000000,
                discount = 400000  // 20% off
            )
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("discount_amount")
            .assertExists()
        composeTestRule.onNodeWithText("-400,000 IRR")
            .assertExists()
    }

    @Test
    fun checkoutScreen_displays_final_total() {
        // Arrange
        // Subtotal: 2000000, Tax: 200000, Shipping: 50000
        // Total: 2250000
        composeTestRule.setContent {
            CheckoutScreen(
                subtotal = 2000000,
                tax = 200000,
                shipping = 50000
            )
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("final_total")
            .assertExists()
        composeTestRule.onNodeWithText("2,250,000 IRR")
            .assertExists()
    }

    @Test
    fun checkoutScreen_displays_payment_method_section() {
        // Arrange
        composeTestRule.setContent {
            CheckoutScreen(subtotal = 2000000)
        }

        // Act & Assert
        composeTestRule.onNodeWithText("Payment Method")
            .assertExists()
    }

    @Test
    fun checkoutScreen_select_payment_method() {
        // Arrange
        var selectedPayment = ""
        composeTestRule.setContent {
            CheckoutScreen(
                subtotal = 2000000,
                onPaymentMethodSelect = { method -> selectedPayment = method }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Credit Card")
            .performClick()

        // Assert
        assert(selectedPayment == "Credit Card")
    }

    @Test
    fun checkoutScreen_place_order_button_visible() {
        // Arrange
        composeTestRule.setContent {
            CheckoutScreen(subtotal = 2000000)
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("place_order_btn")
            .assertExists()
    }

    @Test
    fun checkoutScreen_place_order_click() {
        // Arrange
        var orderPlaced = false
        composeTestRule.setContent {
            CheckoutScreen(
                subtotal = 2000000,
                onPlaceOrder = { orderPlaced = true }
            )
        }

        // Act
        composeTestRule.onNodeWithTag("place_order_btn")
            .performClick()

        // Assert
        assert(orderPlaced)
    }

    @Test
    fun checkoutScreen_order_confirmation_shows() {
        // Arrange
        composeTestRule.setContent {
            CheckoutScreen(
                subtotal = 2000000,
                orderNumber = "ORD123456"
            )
        }

        // Act & Assert
        composeTestRule.onNodeWithText("Order Confirmed")
            .assertExists()
        composeTestRule.onNodeWithText("ORD123456")
            .assertExists()
    }

    @Test
    fun checkoutScreen_displays_estimated_delivery() {
        // Arrange
        composeTestRule.setContent {
            CheckoutScreen(
                subtotal = 2000000,
                estimatedDelivery = "December 30, 2025"
            )
        }

        // Act & Assert
        composeTestRule.onNodeWithText("Estimated Delivery")
            .assertExists()
        composeTestRule.onNodeWithText("December 30, 2025")
            .assertExists()
    }

    @Test
    fun checkoutScreen_loading_state_during_payment() {
        // Arrange
        composeTestRule.setContent {
            CheckoutScreen(
                subtotal = 2000000,
                isProcessing = true
            )
        }

        // Act & Assert
        composeTestRule.onNodeWithTag("payment_loading")
            .assertExists()
    }

    @Test
    fun checkoutScreen_error_message_shows_on_failure() {
        // Arrange
        val errorMessage = "Payment failed. Please try again."
        composeTestRule.setContent {
            CheckoutScreen(
                subtotal = 2000000,
                error = errorMessage
            )
        }

        // Act & Assert
        composeTestRule.onNodeWithText(errorMessage)
            .assertExists()
    }
}
