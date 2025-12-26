package com.noghre.sod.presentation.viewmodel

import app.cash.turbine.turbineScope
import com.noghre.sod.data.model.NetworkResult
import com.noghre.sod.data.model.ErrorType
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.model.ShippingMethod
import com.noghre.sod.domain.model.PromoCode
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import org.junit.After

/**
 * Unit tests for CheckoutViewModel
 * Tests checkout flow including:
 * - Order total calculation
 * - Discount application
 * - Shipping method selection
 * - Promo code validation
 * - Payment processing
 */
class CheckoutViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val cartRepository = mockk<CartRepository>()
    private val paymentService = mockk<PaymentService>()
    private val promoService = mockk<PromoService>()

    private lateinit var viewModel: CheckoutViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CheckoutViewModel(cartRepository, paymentService, promoService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialize checkout - calculate valid order total from items` = runTest {
        // Arrange
        val item1 = CartItem(id = "1", price = 100000, quantity = 1)
        val item2 = CartItem(id = "2", price = 50000, quantity = 2)
        val expectedTotal = 200000 // 100k + (50k * 2)

        coEvery { cartRepository.getItems() } returns listOf(item1, item2)

        // Act
        val total = viewModel.calculateOrderTotal(listOf(item1, item2))

        // Assert
        assert(total == expectedTotal)
    }

    @Test
    fun `apply discount - 10% reduction correctly calculated` = runTest {
        // Arrange
        val originalPrice = 1000000
        val discountPercent = 10
        val expectedPrice = 900000

        // Act
        val discountedPrice = viewModel.applyDiscount(originalPrice, discountPercent)

        // Assert
        assert(discountedPrice == expectedPrice)
    }

    @Test
    fun `apply discount - maximum 50% limit enforced` = runTest {
        // Arrange
        val originalPrice = 1000000
        val requestedDiscount = 75 // Request 75%, but max is 50%
        val expectedPrice = 500000 // 50% of 1M

        // Act
        val discountedPrice = viewModel.applyDiscount(originalPrice, requestedDiscount)

        // Assert
        assert(discountedPrice == expectedPrice) // Only 50% applied
    }

    @Test
    fun `select shipping method - updates selected method` = runTest {
        // Arrange
        val method = ShippingMethod.EXPRESS

        // Act
        viewModel.selectShippingMethod(method)

        // Assert
        assert(viewModel.selectedShippingMethod.value == method)
    }

    @Test
    fun `apply promo code - valid code succeeds` = runTest {
        // Arrange
        val code = "SAVE20"
        val promoCode = PromoCode(code = code, discount = 20, isValid = true)
        coEvery { promoService.validateCode(code) } returns NetworkResult.Success(promoCode)

        // Act
        val result = viewModel.applyPromoCode(code)

        // Assert
        assert(result is NetworkResult.Success)
    }

    @Test
    fun `apply promo code - invalid code returns error` = runTest {
        // Arrange
        val code = "INVALID"
        coEvery { promoService.validateCode(code) } returns NetworkResult.Error(
            exception = Exception("Invalid code"),
            errorType = ErrorType.VALIDATION_ERROR
        )

        // Act
        val result = viewModel.applyPromoCode(code)

        // Assert
        assert(result is NetworkResult.Error)
        val errorResult = result as NetworkResult.Error
        assert(errorResult.errorType == ErrorType.VALIDATION_ERROR)
    }

    @Test
    fun `process checkout - payment successful creates order` = runTest {
        // Arrange
        val mockOrder = Order(id = "ORD123", total = 500000, status = "COMPLETED")
        coEvery { paymentService.processPayment(any()) } returns NetworkResult.Success(mockOrder)

        // Act
        val result = viewModel.processCheckout()

        // Assert
        assert(result is NetworkResult.Success)
        val successResult = result as NetworkResult.Success
        assert(successResult.data.id == "ORD123")
    }

    @Test
    fun `process checkout - payment failure returns error with type` = runTest {
        // Arrange
        coEvery { paymentService.processPayment(any()) } returns NetworkResult.Error(
            exception = Exception("Payment declined"),
            errorType = ErrorType.PAYMENT_FAILED
        )

        // Act
        val result = viewModel.processCheckout()

        // Assert
        assert(result is NetworkResult.Error)
        val errorResult = result as NetworkResult.Error
        assert(errorResult.errorType == ErrorType.PAYMENT_FAILED)
    }
}
