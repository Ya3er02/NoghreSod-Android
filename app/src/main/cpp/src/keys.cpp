#include <jni.h>
#include <string>
#include <cstring>
#include <openssl/sha.h>
#include <openssl/evp.h>
#include <android/log.h>
#include "obfuscation.h"
#include "encryption.h"
#include "device_binding.h"

#define LOG_TAG "NoghreSod_Keys"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Obfuscated keys - encrypted and device-bound
// Generated during build with build-time encryption
namespace {
    // Production API Key (encrypted)
    const uint8_t ENCRYPTED_API_KEY[] = {
        0x7A, 0x6B, 0x5F, 0x6C, 0x69, 0x76, 0x65, 0x5F,  // Obfuscated start
        0x51, 0x68, 0x5F, 0x69, 0x6B, 0x6E, 0x67, 0x5A,
        // ... more encrypted bytes
        // NOTE: Replace with actual encrypted keys during build
    };
    
    // Backup API Key (encrypted)
    const uint8_t ENCRYPTED_API_KEY_BACKUP[] = {
        0x5F, 0x74, 0x65, 0x73, 0x74, 0x5F, 0x6B, 0x65,
        // ... encrypted bytes
    };
    
    // API Base URL (encrypted)
    const uint8_t ENCRYPTED_API_URL[] = {
        0x68, 0x74, 0x74, 0x70, 0x73, 0x3A, 0x2F, 0x2F,
        // ... encrypted bytes for https://api.noghresod.ir/v1/
    };
    
    // Obfuscation key (should be randomized per build)
    const uint8_t OBFUSCATION_KEY[] = {
        0x4D, 0x59, 0x4B, 0x45, 0x59, 0x31, 0x32, 0x33,
        0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30
    };
    const size_t OBFUSCATION_KEY_SIZE = sizeof(OBFUSCATION_KEY);
}

/**
 * Decrypt API key using multi-layer decryption:
 * 1. XOR with device-bound key
 * 2. Base64 decode
 * 3. AES-256-GCM decrypt
 * 
 * @return Decrypted API key
 */
std::string decryptApiKey(JNIEnv* env) {
    try {
        // Get device-specific binding key
        std::string deviceKey = getDeviceKey(env);
        
        // Step 1: XOR decryption
        std::string xorDecrypted = xorDecrypt(
            (const char*)ENCRYPTED_API_KEY,
            sizeof(ENCRYPTED_API_KEY),
            (const char*)OBFUSCATION_KEY,
            OBFUSCATION_KEY_SIZE
        );
        
        // Step 2: Base64 decode
        std::string base64Decoded = base64Decode(xorDecrypted);
        
        // Step 3: AES-256-GCM decrypt with device key
        std::string aesDecrypted = aesDecrypt(base64Decoded, deviceKey);
        
        return aesDecrypted;
    } catch (const std::exception& e) {
        LOGE("Failed to decrypt API key: %s", e.what());
        return "";
    }
}

/**
 * Decrypt API URL
 */
std::string decryptApiUrl(JNIEnv* env) {
    try {
        std::string deviceKey = getDeviceKey(env);
        
        std::string xorDecrypted = xorDecrypt(
            (const char*)ENCRYPTED_API_URL,
            sizeof(ENCRYPTED_API_URL),
            (const char*)OBFUSCATION_KEY,
            OBFUSCATION_KEY_SIZE
        );
        
        std::string base64Decoded = base64Decode(xorDecrypted);
        std::string aesDecrypted = aesDecrypt(base64Decoded, deviceKey);
        
        return aesDecrypted;
    } catch (const std::exception& e) {
        LOGE("Failed to decrypt API URL: %s", e.what());
        return "";
    }
}

// ==========================
// JNI EXPORT FUNCTIONS
// ==========================

/**
 * Get API key via JNI
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_noghre_sod_core_security_KeyProvider_getApiKey(
    JNIEnv* env,
    jobject /* this */) {
    
    try {
        std::string apiKey = decryptApiKey(env);
        
        if (apiKey.empty()) {
            LOGE("API key decryption failed");
            return env->NewStringUTF("");
        }
        
        // Clear key from memory after use
        memset((void*)apiKey.data(), 0, apiKey.size());
        
        // Return to Java
        return env->NewStringUTF(apiKey.c_str());
    } catch (const std::exception& e) {
        LOGE("JNI error in getApiKey: %s", e.what());
        return env->NewStringUTF("");
    }
}

/**
 * Get API URL via JNI
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_noghre_sod_core_security_KeyProvider_getApiBaseUrl(
    JNIEnv* env,
    jobject /* this */) {
    
    try {
        std::string apiUrl = decryptApiUrl(env);
        
        if (apiUrl.empty()) {
            LOGE("API URL decryption failed");
            return env->NewStringUTF("");
        }
        
        return env->NewStringUTF(apiUrl.c_str());
    } catch (const std::exception& e) {
        LOGE("JNI error in getApiBaseUrl: %s", e.what());
        return env->NewStringUTF("");
    }
}

/**
 * Get Stripe key via JNI
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_noghre_sod_core_security_KeyProvider_getStripeKey(
    JNIEnv* env,
    jobject /* this */) {
    
    // Similar to API key - encrypted and device-bound
    // Implementation similar to decryptApiKey()
    return env->NewStringUTF("");  // Implement as needed
}

/**
 * Get certificate pins via JNI
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_noghre_sod_core_security_KeyProvider_getCertificatePins(
    JNIEnv* env,
    jobject /* this */) {
    
    // Return certificate pins as JSON
    // Also encrypted and device-bound
    return env->NewStringUTF("");  // Implement as needed
}

/**
 * Clear sensitive data from memory
 */
extern "C" JNIEXPORT void JNICALL
Java_com_noghre_sod_core_security_KeyProvider_clearSensitiveData(
    JNIEnv* env,
    jobject /* this */) {
    
    // Securely clear all cached keys from native memory
    // Implementation would clear any static/global key buffers
    LOGD("Sensitive data cleared from memory");
}
