package com.noghre.sod.domain.usecase.payment

import com.noghre.sod.core.error.AppError
import com.noghre.sod.core.util.Result
import com.noghre.sod.domain.model.PaymentGateway
import com.noghre.sod.domain.model.PaymentResponse
import com.noghre.sod.domain.repository.PaymentRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for initiating a payment request
 * 
 * Responsibility: Orchestrate payment request logic
 * - Validate input parameters
 * - Call PaymentRepository
 * - Transform results for presentation layer
 * 
 * Benefits:
 * - Testable: Can mock PaymentRepository
 * - Reusable: Used by different features
 * - Encapsulated: Payment logic in one place
 */
class RequestPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    
    /**
     * Execute payment request
     * 
     * @param orderId Order identifier (must not be blank)
     * @param amount Payment amount in Tomans (must be > 0)
     * @param gateway Payment gateway to use
     * @param mobile Optional customer mobile number
     * @return Result<PaymentResponse> with authority and payment URL
     */
    suspend operator fun invoke(
        orderId: String,
        amount: Long,
        gateway: PaymentGateway,
        mobile: String? = null
    ): Result<PaymentResponse> {
        // Input validation
        if (orderId.isBlank()) {
            Timber.w("RequestPaymentUseCase: Invalid orderId")
            return Result.Error(
                AppError.Validation(
                    message = "شناسه سفارش معتبر نیست",
                    field = "orderId"
                )
            )
        }
        
        if (amount <= 0) {
            Timber.w("RequestPaymentUseCase: Invalid amount: $amount")
            return Result.Error(
                AppError.Validation(
                    message = "مبلغ باید بیشتر از صفر باشد",
                    field = "amount"
                )
            )
        }
        
        // Validate mobile if provided
        if (mobile != null && mobile.isBlank()) {
            Timber.w("RequestPaymentUseCase: Blank mobile provided")
            return Result.Error(
                AppError.Validation(
                    message = "نامر موبایل باید خالی نباشد",
                    field = "mobile"
                )
            )
        }
        
        // Log use case execution
        Timber.d(
            "RequestPaymentUseCase: Initiating payment - orderId=$orderId, " +
            "amount=$amount, gateway=$gateway, hasMobile=${mobile != null}"
        )
        
        // Delegate to repository
        return try {
            paymentRepository.requestPayment(
                orderId = orderId,
                amount = amount,
                gateway = gateway,
                mobile = mobile
            )
        } catch (e: Exception) {
            Timber.e(e, "RequestPaymentUseCase: Exception occurred")
            Result.Error(AppError.Unknown(
                message = "خطا در ارسال درخواست پرداخت",
                throwable = e
            ))
        }
    }
}
