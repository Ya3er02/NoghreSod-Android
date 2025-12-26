package com.noghre.sod.core.security

/**
 * Native keys loader for secure API key storage.
 * Loads sensitive keys from native C++ library to prevent reverse engineering.
 * 
 * Native library must be built from app/src/main/cpp/native_keys.cpp
 * 
 * @author Yaser
 * @version 1.0.0
 */
@Suppress("FunctionName", "KotlinJniMissing")
object NativeKeys {
    
    init {
        try {
            System.loadLibrary("noghresod_keys")
        } catch (e: UnsatisfiedLinkError) {
            // Fallback: Use BuildConfig values if native library not available
            System.err.println("Failed to load native library: ${e.message}")
        }
    }
    
    /**
     * Get API base URL from native C++ code.
     * Falls back to BuildConfig if native library unavailable.
     */
    external fun getApiUrl(): String
    
    /**
     * Get payment gateway API key from native storage.
     * Must be kept in native code for security.
     */
    external fun getPaymentGatewayKey(): String
    
    /**
     * Get Firebase config from native storage.
     */
    external fun getFirebaseKey(): String
    
    /**
     * Get encryption key for local data storage.
     */
    external fun getEncryptionKey(): String
    
    /**
     * Validate certificate pinning hashes.
     */
    external fun getCertificatePins(): Array<String>
    
    /**
     * Safe fallback method if native library fails.
     */
    fun getApiUrlSafe(): String {
        return try {
            getApiUrl()
        } catch (e: Exception) {
            com.noghre.sod.BuildConfig.API_BASE_URL
        }
    }
    
    /**
     * Safe fallback for payment gateway key.
     */
    fun getPaymentGatewayKeySafe(): String {
        return try {
            getPaymentGatewayKey()
        } catch (e: Exception) {
            "" // Return empty - payment should fail if key unavailable
        }
    }
}
