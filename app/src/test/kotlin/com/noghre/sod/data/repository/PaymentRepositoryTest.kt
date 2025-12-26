package com.noghre.sod.data.repository

import com.noghre.sod.data.local.dao.PaymentDao
import com.noghre.sod.data.model.NetworkResult
import com.noghre.sod.data.model.ErrorType
import com.noghre.sod.data.remote.PaymentApi
import com.noghre.sod.domain.model.Payment
import com.noghre.sod.domain.model.CardInfo
import io.mockk.coEvery
import io.mockk.coVerify
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
 * Unit tests for Payment Repository
 * Tests payment processing including:
 * - Payment processing and authorization
 * - Card validation
 * - Payment history retrieval
 * - Error handling for various payment scenarios
 */
class PaymentRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val paymentApi = mockk<PaymentApi>()
    private val paymentDao = mockk<PaymentDao>()

    private lateinit var repository: PaymentRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = PaymentRepository(paymentApi, paymentDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `process payment - successful transaction` = runTest {
        // Arrange
        val payment = Payment(cardToken = "valid_token", amount = 500000, currency = "IRR")
        val transactionId = "TXN123"
        coEvery { paymentApi.process(payment) } returns NetworkResult.Success(transactionId)

        // Act
        val result = repository.processPayment(payment)

        // Assert
        assert(result is NetworkResult.Success)
        val successResult = result as NetworkResult.Success
        assert(successResult.data == transactionId)
    }

    @Test
    fun `process payment - declined card returns error` = runTest {
        // Arrange
        val payment = Payment(cardToken = "declined_card", amount = 500000, currency = "IRR")
        coEvery { paymentApi.process(payment) } returns NetworkResult.Error(
            exception = Exception("Card declined"),
            errorType = ErrorType.PAYMENT_FAILED
        )

        // Act
        val result = repository.processPayment(payment)

        // Assert
        assert(result is NetworkResult.Error)
        val errorResult = result as NetworkResult.Error
        assert(errorResult.errorType == ErrorType.PAYMENT_FAILED)
    }

    @Test
    fun `process payment - insufficient funds error` = runTest {
        // Arrange
        val payment = Payment(cardToken = "low_balance", amount = 999999999, currency = "IRR")
        coEvery { paymentApi.process(payment) } returns NetworkResult.Error(
            exception = Exception("Insufficient funds"),
            errorType = ErrorType.PAYMENT_FAILED
        )

        // Act
        val result = repository.processPayment(payment)

        // Assert
        assert(result is NetworkResult.Error)
    }

    @Test
    fun `refund payment - successful refund processed` = runTest {
        // Arrange
        val transactionId = "TXN123"
        coEvery { paymentApi.refund(transactionId) } returns NetworkResult.Success(Unit)

        // Act
        val result = repository.refundPayment(transactionId)

        // Assert
        assert(result is NetworkResult.Success)
    }

    @Test
    fun `get payment history - returns list of payments` = runTest {
        // Arrange
        val userId = "user1"
        val mockHistory = listOf(
            Payment(id = "1", amount = 100000),
            Payment(id = "2", amount = 50000)
        )
        coEvery { paymentDao.getHistory(userId) } returns mockHistory

        // Act
        val result = repository.getPaymentHistory(userId)

        // Assert
        assert(result.size == 2)
        assert(result[0].id == "1")
    }

    @Test
    fun `validate card info - valid card returns true` = runTest {
        // Arrange
        val card = CardInfo(
            number = "4111111111111111",
            cvv = "123",
            expiryMonth = 12,
            expiryYear = 2025
        )

        // Act
        val isValid = repository.validateCard(card)

        // Assert
        assert(isValid)
    }

    @Test
    fun `validate card info - invalid card returns false` = runTest {
        // Arrange
        val card = CardInfo(
            number = "1234567890123456",
            cvv = "999",
            expiryMonth = 12,
            expiryYear = 2025
        )

        // Act
        val isValid = repository.validateCard(card)

        // Assert
        assert(!isValid)
    }

    @Test
    fun `save payment method - persisted to database` = runTest {
        // Arrange
        val payment = Payment(id = "pay1", amount = 500000)
        coEvery { paymentDao.insert(any()) } returns Unit

        // Act
        repository.savePaymentMethod(payment)

        // Assert
        coVerify { paymentDao.insert(any()) }
    }
}
