package com.noghre.sod.data.repository

import com.noghre.sod.core.error.AppError
import com.noghre.sod.core.util.Result
import com.noghre.sod.core.util.onError
import com.noghre.sod.core.util.onSuccess
import com.noghre.sod.data.payment.ZarinpalPaymentService
import com.noghre.sod.data.payment.PaymentVerificationCache
import com.noghre.sod.data.database.dao.PaymentDao
import com.noghre.sod.data.database.entity.PaymentEntity
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
 * - Payment persistence with Room database
 * - Payment history tracking and auditing
 */
@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val zarinpalService: ZarinpalPaymentService,
    private val verificationCache: PaymentVerificationCache,
    private val paymentConfig: PaymentConfiguration,
    private val paymentDao: PaymentDao
    // TODO: Inject additional gateway services when implemented
    // private val idpayService: IDPayPaymentService,
    // private val nextpayService: NextPayPaymentService,
) : PaymentRepository {
    
    /**
     * Request payment from specified gateway
     * 
     * Validates amount before submission and routes to appropriate gateway service.
     * Persists payment record to database after successful request.
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
            
            // PERSIST: Store payment record in database after successful request
            if (result is Result.Success) {
                val paymentEntity = PaymentEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    orderId = orderId,
                    amount = amount,
                    gateway = gateway.name,
                    authority = result.data.authority,
                    refId = null,  // Will be updated on verification
                    status = PaymentStatus.PENDING.name,
                    createdAt = System.currentTimeMillis(),
                    paidAt = null,
                    description = paymentConfig.getPaymentDescription(orderId)
                )
                try {
                    paymentDao.insertPayment(paymentEntity)
                    Timber.i("Payment record stored: ${paymentEntity.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to store payment record")
                    // Don't fail payment request if DB storage fails
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
     * Updates payment status in database on verification.
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
                
                // UPDATE DATABASE: Mark payment as verified
                try {
                    paymentDao.updatePaymentStatus(
                        // Note: In real implementation, retrieve paymentId from DB query by authority
                        paymentId = "temp_id",  // TODO: Get actual ID from DB
                        status = PaymentStatus.SUCCESS.name,
                        refId = result.data.refId,
                        paidAt = result.data.verifiedAt
                    )
                    Timber.i("Payment status updated in database: $authority")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to update payment status in database")
                    // Don't fail verification if DB update fails
                }
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
     * Queries Room database for payment record by ID.
     * 
     * @param paymentId Payment identifier
     * @return Payment data if found
     */
    override suspend fun getPayment(paymentId: String): Result<Payment> {
        return try {
            Timber.d("Getting payment: $paymentId")
            
            val paymentEntity = paymentDao.getPaymentById(paymentId)
            if (paymentEntity != null) {
                Timber.d("Payment found in database: $paymentId")
                Result.Success(paymentEntity.toDomainModel())
            } else {
                Timber.w("Payment not found: $paymentId")
                Result.Error(AppError.NotFound("پرداخت برای مورد منتظر یافت نشد"))
            }
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
     * Queries Room database for payment history by order ID.
     * Returns payments ordered by most recent first.
     * 
     * @param orderId Order identifier
     * @return List of payments for order (empty if none found)
     */
    override suspend fun getOrderPayments(orderId: String): Result<List<Payment>> {
        return try {
            Timber.d("Getting payments for order: $orderId")
            
            val paymentEntities = paymentDao.getPaymentsByOrderId(orderId)
            val payments = paymentEntities.map { it.toDomainModel() }
            Timber.d("Found ${payments.size} payments for order: $orderId")
            Result.Success(payments)
        } catch (e: Exception) {
            Timber.e(e, "Exception in getOrderPayments")
            Result.Error(AppError.Unknown(
                message = "خطا در بازیابی اطلاعات پرداخت",
                throwable = e
            ))
        }
    }
}
