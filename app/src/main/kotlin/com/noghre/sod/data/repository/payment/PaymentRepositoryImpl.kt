package com.noghre.sod.data.repository.payment

import com.noghre.sod.core.result.Result
import com.noghre.sod.data.error.ExceptionHandler
import com.noghre.sod.data.mapper.PaymentMapper.toDomain
import com.noghre.sod.data.network.NoghreSodApi
import com.noghre.sod.domain.model.Payment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject

/**
 * Payment Repository Implementation.
 *
 * Handles payment processing with:
 * - Secure payment gateway integration
 * - Transaction verification
 * - Payment status tracking
 * - Error handling and retry logic
 *
 * @param api Retrofit API client
 * @param ioDispatcher Dispatcher for I/O operations
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
class PaymentRepositoryImpl @Inject constructor(
    private val api: NoghreSodApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IPaymentRepository {

    override suspend fun processPayment(
        orderId: String,
        amount: Double,
        method: String
    ): Result<Payment> = try {
        // Validate amount
        if (amount <= 0) {
            return Result.failure(Exception("Invalid amount: $amount"))
        }

        val requestBody = mapOf(
            "orderId" to orderId,
            "amount" to amount,
            "method" to method
        )

        Timber.d("Processing payment: orderId=$orderId, amount=$amount, method=$method")

        val response = api.processPayment(requestBody)

        if (response.isSuccessful && response.body()?.success == true) {
            val paymentDto = response.body()?.data
            if (paymentDto != null) {
                val payment = paymentDto.toDomain()
                Timber.d("Payment processed successfully: ${payment.id}")
                Result.success(payment)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            val errorMsg = "Payment processing failed: ${response.code()} - ${response.message()}"
            Timber.e(errorMsg)
            Result.failure(Exception(errorMsg))
        }
    } catch (e: Exception) {
        val errorState = ExceptionHandler.handle(e, "processPayment")
        Timber.e(e, "Error processing payment: ${ExceptionHandler.getUserMessage(errorState)}")
        Result.failure(e)
    }

    override suspend fun getPaymentStatus(paymentId: String): Result<Payment> = try {
        if (paymentId.isBlank()) {
            return Result.failure(Exception("Invalid payment ID"))
        }

        Timber.d("Fetching payment status: $paymentId")

        val response = api.getPaymentStatus(paymentId)

        if (response.isSuccessful && response.body()?.success == true) {
            val paymentDto = response.body()?.data
            if (paymentDto != null) {
                val payment = paymentDto.toDomain()
                Timber.d("Payment status retrieved: $paymentId - ${payment.status}")
                Result.success(payment)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            when (response.code()) {
                404 -> Result.failure(Exception("Payment not found"))
                else -> Result.failure(Exception("Failed to get payment status: ${response.code()}"))
            }
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "getPaymentStatus")
        Timber.e(e, "Error getting payment status")
        Result.failure(e)
    }

    override suspend fun verifyTransaction(transactionId: String): Result<Boolean> = try {
        if (transactionId.isBlank()) {
            return Result.failure(Exception("Invalid transaction ID"))
        }

        Timber.d("Verifying transaction: $transactionId")

        val requestBody = mapOf("transactionId" to transactionId)
        val response = api.verifyPayment(requestBody)

        if (response.isSuccessful && response.body()?.success == true) {
            val isValid = response.body()?.data?.valid ?: false
            Timber.d("Transaction verification: $transactionId - valid=$isValid")
            Result.success(isValid)
        } else {
            Result.failure(Exception("Verification failed: ${response.code()}"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "verifyTransaction")
        Timber.e(e, "Error verifying transaction")
        Result.failure(e)
    }
}
