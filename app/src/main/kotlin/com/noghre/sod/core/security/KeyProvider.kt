package com.noghre.sod.core.security

/**
 * Secure key provider using NDK (Native C++) for API key storage.
 * 
 * Security features:
 * - Keys stored in native code (harder to reverse engineer)
 * - Multi-layer encryption (XOR + Base64 + AES)
 * - Device-bound encryption (unique per device)
 * - Obfuscation at compile time
 * 
 * Keys are loaded from libnoghresod_keys.so (compiled C++ library)
 * 
 * @since 1.0.0
 */
object KeyProvider {
    
    init {
        // Load native library containing encrypted keys
        System.loadLibrary("noghresod_keys")
    }
    
    /**
     * Get API key from native code.
     * Key is encrypted and device-bound.
     * 
     * @return Decrypted API key
     */
    external fun getApiKey(): String
    
    /**
     * Get API base URL from native code.
     * 
     * @return API base URL
     */
    external fun getApiBaseUrl(): String
    
    /**
     * Get Stripe key from native code (if using Stripe payments).
     * 
     * @return Stripe publishable key
     */
    external fun getStripeKey(): String
    
    /**
     * Get certificate pinning pins from native code.
     * 
     * @return Certificate public key pins as JSON
     */
    external fun getCertificatePins(): String
    
    /**
     * Safely clear sensitive data from memory.
     * Call when app goes to background or on logout.
     */
    external fun clearSensitiveData()
}
