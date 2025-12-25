package com.noghre.sod.core.security

import androidx.annotation.Keep

/**
 * Wrapper for native API key storage using JNI.
 * Prevents API keys from being easily extracted via reverse engineering.
 */
@Keep
object NativeKeys {
    init {
        System.loadLibrary("noghresod-native")
    }

    /**
     * Get API Base URL from native library.
     * @return API Base URL string
     */
    external fun getApiUrl(): String

    /**
     * Get API Key from native library.
     * @return API Key string
     */
    external fun getApiKey(): String

    /**
     * Get Google Play Services Key from native library.
     * @return Google Play Key string
     */
    external fun getGooglePlayKey(): String
}