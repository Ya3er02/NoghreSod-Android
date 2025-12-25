#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_noghre_sod_data_network_NativeKeys_getApiUrl(JNIEnv* env, jobject) {
    // API Base URL
    return env->NewStringUTF("https://api.noghresod.ir/v1/");
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_noghre_sod_data_network_NativeKeys_getApiKey(JNIEnv* env, jobject) {
    // API Key (obfuscated in production)
    return env->NewStringUTF("YOUR_API_KEY_HERE");
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_noghre_sod_data_network_NativeKeys_getGooglePlayKey(JNIEnv* env, jobject) {
    // Google Play Services Key
    return env->NewStringUTF("YOUR_PLAY_KEY_HERE");
}