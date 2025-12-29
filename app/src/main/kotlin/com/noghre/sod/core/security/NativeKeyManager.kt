package com.noghre.sod.core.security

import timber.log.Timber
import javax.inject.Singleton

@Singleton
object NativeKeyManager {
    private var isLibraryLoaded = false
    
    init {
        try {
            System.loadLibrary("noghresod_secure")
            isLibraryLoaded = true
            Timber.d("✅ Native key library loaded successfully")
        } catch (e: UnsatisfiedLinkError) {
            Timber.e(e, "❌ Failed to load native library - using fallback")
            isLibraryLoaded = false
        } catch (e: Exception) {
            Timber.e(e, "❌ Unexpected error loading native library")
            isLibraryLoaded = false
        }
    }
    
    /**
     * بازیابی Zarinpal Merchant ID از native library
     * - هرگز در BuildConfig قرار نمی‌گیرد
     * - هرگز در APK به صورت plaintext نیست
     * - استخراج شده با XOR decryption
     * 
     * @return Merchant ID یا empty string اگر library load نشود
     */
    external fun getMerchantId(): String
    
    /**
     * بازیابی API Key از native library
     * @return API Key یا empty string
     */
    external fun getApiKey(): String
    
    /**
     * محافظت شده توسط NDK + runtime memory
     */
    fun getPaymentGatewayCredentials(): PaymentCredentials {
        if (!isLibraryLoaded) {
            Timber.e("Native library not loaded - credentials unavailable")
            return PaymentCredentials("", "")
        }
        
        return try {
            PaymentCredentials(
                merchantId = getMerchantId(),
                apiKey = getApiKey()
            )
        } catch (e: Exception) {
            Timber.e(e, "Error retrieving payment credentials")
            PaymentCredentials("", "")
        }
    }
    
    fun isLibraryAvailable(): Boolean = isLibraryLoaded
}

data class PaymentCredentials(
    val merchantId: String,
    val apiKey: String
)
