#include <jni.h>
#include <string>

// ============================================
// 🔐 Native Keys Management (C++)
// Sensitive data stored in native code
// ============================================

extern "C" {

/**
 * Get API Base URL from native code
 * @return API endpoint URL
 * NOTE: Replace with your actual backend URL
 */
JNIEXPORT jstring JNICALL
Java_com_noghre_sod_data_network_NativeKeys_getApiUrl(
    JNIEnv *env, jobject /* this */) {
    // TODO: Replace with actual API URL before production
    const char *api_url = "https://api.noghresod.ir/v1/";
    return env->NewStringUTF(api_url);
}

/**
 * Get Certificate Pinning SHA256 hashes
 * @return SHA256 hash for pinning
 */
JNIEXPORT jstring JNICALL
Java_com_noghre_sod_data_network_NativeKeys_getCertificatePinSha(
    JNIEnv *env, jobject /* this */) {
    const char *pin_sha = "sha256/Iv8Pkqkx7E0IxEBf9X9sLeJW6zIPg9TJd6K3mNfW5lQ=";
    return env->NewStringUTF(pin_sha);
}

/**
 * Get backup Certificate for pinning
 * @return Backup SHA256 hash
 */
JNIEXPORT jstring JNICALL
Java_com_noghre_sod_data_network_NativeKeys_getBackupCertificatePin(
    JNIEnv *env, jobject /* this */) {
    const char *backup_pin = "sha256/lFQwGWAd96P3xh8Sj7fVOBHmxZN0A8d/zJGz2fKJHNc=";
    return env->NewStringUTF(backup_pin);
}

/**
 * Get ZarinPal Merchant ID for payment processing
 * @return Merchant ID from ZarinPal dashboard
 * NOTE: Replace with actual credentials before production
 */
JNIEXPORT jstring JNICALL
Java_com_noghre_sod_data_network_NativeKeys_getPaymentGatewayKey(
    JNIEnv *env, jobject /* this */) {
    // TODO: Replace with actual ZarinPal Merchant ID
    // Get from: https://panel.zarinpal.com/app/settings
    const char *merchant_id = "00000000-0000-0000-0000-000000000000";
    return env->NewStringUTF(merchant_id);
}

/**
 * Get Firebase Project ID
 * @return Firebase project ID
 * NOTE: Replace with actual credentials before production
 */
JNIEXPORT jstring JNICALL
Java_com_noghre_sod_data_network_NativeKeys_getFirebaseKey(
    JNIEnv *env, jobject /* this */) {
    // TODO: Replace with actual Firebase credentials
    // Get from: google-services.json in your Firebase console
    const char *firebase_key = "AIzaSyDummyKeyForTesting123456789";
    return env->NewStringUTF(firebase_key);
}

/**
 * Get Encryption Key for sensitive data
 * @return Encryption key for local data encryption
 * NOTE: Replace with actual key before production
 */
JNIEXPORT jstring JNICALL
Java_com_noghre_sod_data_network_NativeKeys_getEncryptionKey(
    JNIEnv *env, jobject /* this */) {
    // TODO: Generate a strong encryption key
    // Use: openssl rand -base64 32
    const char *encryption_key = "YourStrongEncryptionKeyHere32BytesBase64";
    return env->NewStringUTF(encryption_key);
}

/**
 * Get API timeout duration
 * @return Timeout in seconds
 */
JNIEXPORT jint JNICALL
Java_com_noghre_sod_data_network_NativeKeys_getApiTimeout(
    JNIEnv *env, jobject /* this */) {
    return 30; // 30 seconds
}

/**
 * Get maximum retry attempts
 * @return Number of retries
 */
JNIEXPORT jint JNICALL
Java_com_noghre_sod_data_network_NativeKeys_getMaxRetries(
    JNIEnv *env, jobject /* this */) {
    return 3; // Max 3 retries
}

/**
 * Get initial retry delay
 * @return Delay in milliseconds
 */
JNIEXPORT jint JNICALL
Java_com_noghre_sod_data_network_NativeKeys_getRetryDelay(
    JNIEnv *env, jobject /* this */) {
    return 1000; // 1 second
}

} // extern "C"
