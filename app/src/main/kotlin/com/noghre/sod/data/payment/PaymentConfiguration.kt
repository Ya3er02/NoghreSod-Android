package com.noghre.sod.data.payment

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Configuration for payment operations
 * 
 * Centralizes payment-related configuration including:
 * - Callback URLs for gateway redirects
 * - Payment descriptions and messages
 * - Gateway-specific settings
 * 
 * Benefits:
 * - Decouple from hardcoded strings
 * - Easy to change without code modifications
 * - Support for different environments (dev/staging/prod)
 */
@Singleton
class PaymentConfiguration @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * Get callback URL for payment gateway redirect
     * This URL is called by the gateway after payment completion
     * 
     * Syncs with:
     * - AndroidManifest.xml deep link configuration
     * - Navigation routes for payment callback handling
     * 
     * @return Deep link URI for payment callback
     */
    fun getCallbackUrl(): String {
        // Production: noghresod://payment/callback
        // Development can override via BuildConfig or environment variables
        return CALLBACK_SCHEME + "://" + CALLBACK_HOST + CALLBACK_PATH
    }
    
    /**
     * Build user-facing payment description
     * Localizes description for transaction details in payment gateway
     * 
     * @param orderId Order identifier for context
     * @return Localized payment description in Persian
     */
    fun getPaymentDescription(orderId: String): String {
        return "سفارش $orderId - فروشگاه نقره سÙود"
    }
    
    companion object {
        // Deep link configuration - sync with AndroidManifest.xml
        private const val CALLBACK_SCHEME = "noghresod"
        private const val CALLBACK_HOST = "payment"
        private const val CALLBACK_PATH = "/callback"
    }
}
