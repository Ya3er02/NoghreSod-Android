package com.noghre.sod.core.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized string provider for localized messages
 * 
 * Responsibilities:
 * - Provide strings from Android resources
 * - Support multi-language (Persian, English)
 * - Abstract away Context dependency
 * - Enable easy message updates
 * 
 * Usage:
 * ```kotlin
 * @Inject lateinit var stringProvider: StringProvider
 * 
 * fun handleError(error: AppError) {
 *     val message = when (error) {
 *         is AppError.Validation -> stringProvider.validationError(error.field)
 *         is AppError.Payment -> stringProvider.paymentError()
 *         // ...
 *     }
 * }
 * ```
 */
@Singleton
class StringProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * Get string by resource ID
     */
    fun getString(@StringRes resId: Int): String = context.getString(resId)
    
    /**
     * Get string with format arguments
     */
    fun getString(@StringRes resId: Int, vararg args: Any): String =
        context.getString(resId, *args)
    
    // ========== PAYMENT STRINGS ==========
    
    fun paymentAmountInvalid(): String =
        getString(R.string.payment_validation_amount_error)
    
    fun paymentAuthorityInvalid(): String =
        getString(R.string.payment_validation_authority_error)
    
    fun paymentMobileInvalid(): String =
        getString(R.string.payment_validation_mobile_error)
    
    fun paymentRequestFailed(): String =
        getString(R.string.payment_request_failed)
    
    fun paymentVerificationFailed(): String =
        getString(R.string.payment_verification_failed)
    
    fun paymentGatewayUnavailable(): String =
        getString(R.string.payment_gateway_unavailable)
    
    fun paymentNotFound(): String =
        getString(R.string.payment_not_found)
    
    fun paymentDescription(orderId: String): String =
        getString(R.string.payment_description, orderId)
    
    fun paymentCashOnDelivery(): String =
        getString(R.string.payment_cod_message)
    
    // ========== CART STRINGS ==========
    
    fun cartLoadFailed(): String =
        getString(R.string.cart_load_failed)
    
    fun cartAddFailed(): String =
        getString(R.string.cart_add_failed)
    
    fun cartRemoveFailed(): String =
        getString(R.string.cart_remove_failed)
    
    fun cartClearFailed(): String =
        getString(R.string.cart_clear_failed)
    
    fun cartProductIdInvalid(): String =
        getString(R.string.cart_validation_product_id_error)
    
    fun cartQuantityInvalid(): String =
        getString(R.string.cart_validation_quantity_error)
    
    // ========== GENERIC ERROR STRINGS ==========
    
    fun errorGeneric(): String =
        getString(R.string.error_generic)
    
    fun errorNetwork(): String =
        getString(R.string.error_network)
    
    fun errorDatabase(): String =
        getString(R.string.error_database)
    
    fun errorValidation(field: String = ""): String =
        if (field.isNotEmpty()) {
            getString(R.string.error_validation_field, field)
        } else {
            getString(R.string.error_validation)
        }
    
    fun errorTimeout(): String =
        getString(R.string.error_timeout)
    
    fun errorNotFound(): String =
        getString(R.string.error_not_found)
}

// ========== STRING RESOURCE IDS ==========
// Note: These resource IDs should be defined in res/values/strings.xml
// Adding here for reference

object R {
    object string {
        // Payment validation
        const val payment_validation_amount_error = 0x7F1001
        const val payment_validation_authority_error = 0x7F1002
        const val payment_validation_mobile_error = 0x7F1003
        const val payment_request_failed = 0x7F1004
        const val payment_verification_failed = 0x7F1005
        const val payment_gateway_unavailable = 0x7F1006
        const val payment_not_found = 0x7F1007
        const val payment_description = 0x7F1008
        const val payment_cod_message = 0x7F1009
        
        // Cart validation
        const val cart_load_failed = 0x7F1010
        const val cart_add_failed = 0x7F1011
        const val cart_remove_failed = 0x7F1012
        const val cart_clear_failed = 0x7F1013
        const val cart_validation_product_id_error = 0x7F1014
        const val cart_validation_quantity_error = 0x7F1015
        
        // Generic errors
        const val error_generic = 0x7F1020
        const val error_network = 0x7F1021
        const val error_database = 0x7F1022
        const val error_validation = 0x7F1023
        const val error_validation_field = 0x7F1024
        const val error_timeout = 0x7F1025
        const val error_not_found = 0x7F1026
    }
}
