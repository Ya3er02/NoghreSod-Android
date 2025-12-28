package com.noghre.sod.data.repository

import com.noghre.sod.core.error.AppError
import com.noghre.sod.core.util.Result
import com.noghre.sod.core.util.onError
import com.noghre.sod.core.util.onSuccess
import com.noghre.sod.data.payment.ZarinpalPaymentService
import com.noghre.sod.data.payment.PaymentVerificationCache
import com.noghre.sod.domain.model.*
import com.noghre.sod.domain.repository.PaymentRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Payment Repository Implementation
 * Handles payment operations through various Iranian gateways
 * 
 * Key features:
 * - Gateway abstraction layer for switching between payment providers
 * - Idempotent verification to prevent duplicate order fulfillment
 * - Comprehensive validation and error handling
 * - Configurable callback URLs for deep linking
 */
@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val zarinpalService: ZarinpalPaymentService,
    private val verificationCache: PaymentVerificationCache,
    private val paymentConfig: PaymentConfiguration
    // TODO: Inject additional gateway services when implemented
    // private val idpayService: IDPayPaymentService,
    // private val nextpayService: NextPayPaymentService,
) : PaymentRepository {
    
    /**
     * Request payment from specified gateway
     * 
     * Validates amount before submission and routes to appropriate gateway service.
     * 
     * @param orderId Unique order identifier
     * @param amount Payment amount in Tomans (must be > 0)
     * @param gateway Payment gateway to use
     * @param mobile Optional mobile number for payment
     * @return PaymentResponse with authority and payment URL for redirect
     */
    override suspend fun requestPayment(
        orderId: String,
        amount: Long,
        gateway: PaymentGateway,
        mobile: String?
    ): Result<PaymentResponse> {
        return try {
            // VALIDATION: Ensure amount is positive
            if (amount <= 0) {
                Timber.w("Invalid payment amount: $amount for order $orderId")
                return Result.Error(
                    AppError.Validation(
                        message = "مبلغ باید بیشتر از صفر باشد",
                        field = "amount"
                    )
                )
            }
            
            // Improve logging context
            Timber.d(
                "Requesting payment for order $orderId: amount=$amount, gateway=$gateway, " +
                "hasMobile=${mobile != null}"
            )
            
            // Create payment request with configurable callback URL
            val request = PaymentRequest(
                orderId = orderId,
                amount = amount,
                gateway = gateway,
                description = paymentConfig.getPaymentDescription(orderId),
                callbackUrl = paymentConfig.getCallbackUrl(),
                mobile = mobile,
                email = null
            )
            
            // Route to appropriate gateway service (exhaustive when for enum safety)
            val result = when (gateway) {
                PaymentGateway.ZARINPAL -> zarinpalService.requestPayment(request)
                
                PaymentGateway.IDPAY -> {
                    Timber.d("IDPay gateway not yet implemented")
                    Result.Error(AppError.Payment("درگاه پرداخت هنوز فعال نیست"))
                }
                
                PaymentGateway.NEXTPAY -> {
                    Timber.d("NextPay gateway not yet implemented")
                    Result.Error(AppError.Payment("درگاه پرداخت هنوز فعال نیست"))
                }
                
                PaymentGateway.ZIBAL -> {
                    Timber.d("Zibal gateway not yet implemented")
                    Result.Error(AppError.Payment("درگاه پرداخت هنوز فعال نیست"))
                }
                
                PaymentGateway.PAYPINGSUM -> {
                    Timber.d("PayPingSum gateway not yet implemented")
                    Result.Error(AppError.Payment("درگاه پرداخت هنوز فعال نیست"))
                }
                
                PaymentGateway.CASH_ON_DELIVERY -> {
                    // Cash on delivery - no actual payment needed
                    Timber.d("Using cash on delivery payment for order $orderId")
                    Result.Success(
                        PaymentResponse(
                            authority = "COD_${System.currentTimeMillis()}_$orderId",
                            paymentUrl = "",
                            status = PaymentStatus.PENDING,
                            message = "سفارش ثبت شد. پرداخت در محل تحویل"
                        )
                    )
                }
            }
            
            // Log results appropriately
            result.onSuccess { response ->
                Timber.i("Payment requested successfully. Authority: ${response.authority}")
            }.onError { error ->
                Timber.e("Payment request failed: $error")
            }
            
            result
        } catch (e: Exception) {
            Timber.e(e, "Exception in payment request for order $orderId")
            // Map to AppError instead of leaking raw exception message
            Result.Error(AppError.Unknown(
                message = "خطا در ارسال درخواست پرداخت",
                throwable = e
            ))
        }
    }
    
    /**
     * Verify payment after user returns from gateway
     * 
     * Implements idempotent verification by caching results.
     * Multiple calls with same authority return cached result.
     * 
     * @param authority Payment gateway authority code
     * @param amount Payment amount for verification
     * @param gateway Payment gateway used
     * @return PaymentVerification with transaction details
     */
    override suspend fun verifyPayment(
        authority: String,
        amount: Long,
        gateway: PaymentGateway
    ): Result<PaymentVerification> {
        return try {
            Timber.d("Verifying payment: authority=$authority, gateway=$gateway")
            
            // CHECK CACHE: Idempotent verification
            // If this authority was already verified, return cached result
            verificationCache.getVerification(authority)?.let { cachedVerification ->
                Timber.i("Returning cached verification for authority: $authority")
                return Result.Success(cachedVerification)
            }
            
            // Route to appropriate gateway service (exhaustive when)
            val result = when (gateway) {
                PaymentGateway.ZARINPAL -> zarinpalService.verifyPayment(authority, amount)
                
                PaymentGateway.IDPAY -> {
                    Timber.d("IDPay gateway not yet implemented")
                    Result.Error(AppError.Payment("درگاه پرداخت هنوز فعال نیست"))
                }
                
                PaymentGateway.NEXTPAY -> {
                    Timber.d("NextPay gateway not yet implemented")
                    Result.Error(AppError.Payment("درگاه پرداخت هنوز فعال نیست"))
                }
                
                PaymentGateway.ZIBAL -> {
                    Timber.d("Zibal gateway not yet implemented")
                    Result.Error(AppError.Payment("درگاه پرداخت هنوز فعال نیست"))
                }
                
                PaymentGateway.PAYPINGSUM -> {
                    Timber.d("PayPingSum gateway not yet implemented")
                    Result.Error(AppError.Payment("درگاه پرداخت هنوز فعال نیست"))
                }
                
                PaymentGateway.CASH_ON_DELIVERY -> {
                    // Cash on delivery - verification is immediate
                    Timber.d("Verifying cash on delivery payment: $authority")
                    Result.Success(
                        PaymentVerification(
                            orderId = authority.substringAfterLast("_"), // Extract orderId from COD authority
                            authority = authority,
                            refId = null,
                            cardPan = null,
                            cardHash = null,
                            feeType = null,
                            fee = null,
                            status = PaymentStatus.SUCCESS,
                            verifiedAt = System.currentTimeMillis()
                        )
                    )
                }
            }
            
            // CACHE SUCCESSFUL VERIFICATIONS: Prevent duplicate fulfillment
            if (result is Result.Success) {
                verificationCache.cacheVerification(authority, result.data)
                Timber.i("Cached verification for authority: $authority")
            }
            
            // Log results
            result.onSuccess { verification ->
                Timber.i("Payment verified successfully. Authority: $authority, RefId: ${verification.refId}")
            }.onError { error ->
                Timber.e("Payment verification failed for authority $authority: $error")
            }
            
            result
        } catch (e: Exception) {
            Timber.e(e, "Exception in payment verification for authority: $authority")
            Result.Error(AppError.Unknown(
                message = "خطا در تالید پرداخت",
                throwable = e
            ))
        }
    }
    
    /**
     * Get payment details from database
     * 
     * Requires Room DAO implementation to retrieve payments from local database.
     * 
     * @param paymentId Payment identifier
     * @return Payment data if found
     */
    override suspend fun getPayment(paymentId: String): Result<Payment> {
        return try {
            Timber.d("Getting payment: $paymentId")
            
            // TODO: Implement Room DAO integration
            // val payment = paymentDao.getPaymentById(paymentId)
            // return if (payment != null) Result.Success(payment) else Result.Error(...)
            
            Result.Error(AppError.NotFound("پرداخت برای مورد منتظر یافت نشد"))
        } catch (e: Exception) {
            Timber.e(e, "Exception in getPayment")
            Result.Error(AppError.Unknown(
                message = "خطا در بازیابی اطلاعات پرداخت",
                throwable = e
            ))
        }
    }
    
    /**
     * Get all payments for a specific order
     * 
     * Requires Room DAO implementation to retrieve payment history.
     * 
     * @param orderId Order identifier
     * @return List of payments for order (empty if none found)
     */
    override suspend fun getOrderPayments(orderId: String): Result<List<Payment>> {
        return try {
            Timber.d("Getting payments for order: $orderId")
            
            // TODO: Implement Room DAO integration
            // val payments = paymentDao.getPaymentsByOrderId(orderId)
            // return Result.Success(payments)
            
            Result.Success(emptyList())
        } catch (e: Exception) {
            Timber.e(e, "Exception in getOrderPayments")
            Result.Error(AppError.Unknown(
                message = "خطا در بازیابی اطلاعات پرداخت",
                throwable = e
            ))
        }
    }
}
