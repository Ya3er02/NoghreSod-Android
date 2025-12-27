package com.noghre.sod.integration

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noghre.sod.domain.model.PaymentMethod
import com.noghre.sod.ui.screens.PaymentScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for payment processing
 * Tests payment methods, validation, and processing flow
 */
@RunWith(AndroidJUnit4::class)
class PaymentIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun payment_credit_card_valid() {
        // Arrange
        var paymentProcessed = false
        composeTestRule.setContent {
            PaymentScreen(
                amount = 2500000,
                onPaymentSuccess = { transactionId -> paymentProcessed = true }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Credit Card").performClick()
        composeTestRule.onNodeWithTag("card_number_input")
            .performTextInput("4111111111111111")
        composeTestRule.onNodeWithTag("cardholder_input")
            .performTextInput("Ali Mohammadi")
        composeTestRule.onNodeWithTag("expiry_input")
            .performTextInput("12/25")
        composeTestRule.onNodeWithTag("cvv_input")
            .performTextInput("123")
        composeTestRule.onNodeWithTag("pay_button").performClick()

        // Assert
        composeTestRule.waitUntil(timeoutMillis = 5000) { paymentProcessed }
        assert(paymentProcessed)
    }

    @Test
    fun payment_invalid_card_number() {
        // Arrange
        composeTestRule.setContent {
            PaymentScreen(amount = 2500000)
        }

        // Act
        composeTestRule.onNodeWithText("Credit Card").performClick()
        composeTestRule.onNodeWithTag("card_number_input")
            .performTextInput("1234567890123456") // Invalid
        composeTestRule.onNodeWithTag("pay_button").performClick()

        // Assert
        composeTestRule.onNodeWithText("Invalid card number").assertExists()
    }

    @Test
    fun payment_expired_card() {
        // Arrange
        composeTestRule.setContent {
            PaymentScreen(amount = 2500000)
        }

        // Act
        composeTestRule.onNodeWithText("Credit Card").performClick()
        composeTestRule.onNodeWithTag("card_number_input")
            .performTextInput("4111111111111111")
        composeTestRule.onNodeWithTag("expiry_input")
            .performTextInput("01/20") // Expired
        composeTestRule.onNodeWithTag("pay_button").performClick()

        // Assert
        composeTestRule.onNodeWithText("Card expired").assertExists()
    }

    @Test
    fun payment_bank_transfer() {
        // Arrange
        var paymentMethodSelected = ""
        composeTestRule.setContent {
            PaymentScreen(
                amount = 2500000,
                onPaymentMethodChange = { method -> paymentMethodSelected = method }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Bank Transfer").performClick()
        composeTestRule.onNodeWithTag("bank_selector").assertExists()
        composeTestRule.onNodeWithTag("account_number_input")
            .performTextInput("123456789")
        composeTestRule.onNodeWithTag("confirm_button").performClick()

        // Assert
        assert(paymentMethodSelected == "Bank Transfer")
    }

    @Test
    fun payment_wallet_integration() {
        // Arrange
        composeTestRule.setContent {
            PaymentScreen(amount = 2500000)
        }

        // Act
        composeTestRule.onNodeWithText("Digital Wallet").performClick()
        composeTestRule.onNodeWithTag("wallet_selector").assertExists()
        composeTestRule.onNodeWithTag("wallet_confirm_btn").performClick()

        // Assert - Redirect to wallet app simulation
        composeTestRule.onNodeWithText("Redirecting to wallet...").assertExists()
    }

    @Test
    fun payment_receipt_display() {
        // Arrange
        composeTestRule.setContent {
            PaymentScreen(
                amount = 2500000,
                isPaymentComplete = true,
                transactionId = "TXN123456789"
            )
        }

        // Act & Assert
        composeTestRule.onNodeWithText("Payment Successful").assertExists()
        composeTestRule.onNodeWithTag("receipt_container").assertExists()
        composeTestRule.onNodeWithText("TXN123456789").assertExists()
        composeTestRule.onNodeWithText("2,500,000 IRR").assertExists()
    }

    @Test
    fun payment_retry_on_failure() {
        // Arrange
        var retryAttempts = 0
        composeTestRule.setContent {
            PaymentScreen(
                amount = 2500000,
                onRetry = { retryAttempts++ }
            )
        }

        // Act - First attempt fails
        composeTestRule.onNodeWithTag("card_number_input")
            .performTextInput("4111111111111111")
        composeTestRule.onNodeWithTag("expiry_input")
            .performTextInput("12/25")
        composeTestRule.onNodeWithTag("cvv_input")
            .performTextInput("123")
        composeTestRule.onNodeWithTag("pay_button").performClick()
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("retry", ignoreCase = true).fetchSemanticsNodes().isNotEmpty()
        }

        // Retry
        composeTestRule.onNodeWithTag("retry_button").performClick()

        // Assert
        assert(retryAttempts > 0)
    }
}
