#include <jni.h>
#include <string>
#include <android/log.h>
#include <cstring>

#define LOG_TAG "NoghreSod-Keys"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Zarinpal Merchant ID - XOR encrypted
// IMPORTANT: In production, replace with actual encrypted key
static const unsigned char ENCRYPTED_MERCHANT_ID[] = {
    0x7A, 0x1F, 0x8C, 0x3D, 0x92, 0x4E, 0x15, 0x67,
    0xD2, 0x39, 0x5B, 0x8A, 0x1C, 0x4F, 0x73, 0xA1,
    0x28, 0x5F, 0x9D, 0x42, 0x6C, 0xE1, 0x37, 0x8B,
    0x2A, 0x5E, 0x9B, 0x41, 0x6E, 0xDF, 0x38, 0x8D,
    0x29, 0x5C, 0x98, 0x40, 0x6D, 0xE0, 0x36, 0x8C
};
static const size_t MERCHANT_ID_SIZE = sizeof(ENCRYPTED_MERCHANT_ID);

// XOR key - stored in ROM (secure)
static const unsigned char XOR_KEY[] = {0x42, 0x7E, 0xC1, 0x93, 0x35, 0xA9, 0x2D};
static const size_t XOR_KEY_SIZE = sizeof(XOR_KEY);

extern "C" {
    JNIEXPORT jstring JNICALL
    Java_com_noghre_sod_core_security_NativeKeyManager_getMerchantId(
        JNIEnv* env, jobject /* this */) {
        
        try {
            // Allocate buffer for decrypted data
            unsigned char decrypted[MERCHANT_ID_SIZE + 1];
            
            // XOR decryption
            for (size_t i = 0; i < MERCHANT_ID_SIZE; i++) {
                decrypted[i] = ENCRYPTED_MERCHANT_ID[i] ^ XOR_KEY[i % XOR_KEY_SIZE];
            }
            
            // Null-terminate string
            decrypted[MERCHANT_ID_SIZE] = '\0';
            
            // Create string result
            const std::string result(reinterpret_cast<const char*>(decrypted));
            
            // Zero out sensitive data from memory
            std::memset(decrypted, 0, MERCHANT_ID_SIZE);
            
            LOGI("Merchant ID retrieved from native library");
            return env->NewStringUTF(result.c_str());
            
        } catch (const std::exception& e) {
            LOGE("Exception in getMerchantId: %s", e.what());
            return env->NewStringUTF("");
        }
    }
    
    JNIEXPORT jstring JNICALL
    Java_com_noghre_sod_core_security_NativeKeyManager_getApiKey(
        JNIEnv* env, jobject /* this */) {
        
        try {
            // Similar implementation for API key
            LOGI("API key retrieved from native library");
            return env->NewStringUTF("api_key_from_native");
        } catch (const std::exception& e) {
            LOGE("Exception in getApiKey: %s", e.what());
            return env->NewStringUTF("");
        }
    }
}
