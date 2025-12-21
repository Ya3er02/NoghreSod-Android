package com.noghre.sod.data.remote

import com.squareup.okhttp3.CertificatePinner
import timber.log.Timber

/**
 * Utility for certificate pinning configuration.
 * Provides secure certificate pin setup for production environments.
 */
object CertificatePinningUtil {

    /**
     * Configure certificate pinning for OkHttp client.
     * 
     * To get certificate hashes:
     * openssl s_client -connect api.noghre.sod:443 </dev/null | \
     * openssl x509 -outform DER | \
     * openssl dgst -sha256 -binary | \
     * openssl enc -base64
     */
    fun getCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            // Main API domain
            .add(
                "api.noghre.sod",
                // Primary certificate
                "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
                // Backup certificate 1
                "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=",
                // Backup certificate 2
                "sha256/CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC="
            )
            // CDN domain
            .add(
                "cdn.noghre.sod",
                "sha256/DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD=",
                "sha256/EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE="
            )
            .build()
            .also {
                Timber.d("Certificate pinning configured")
            }
    }

    /**
     * Get certificate hashes from URL.
     * Use this for testing and development.
     */
    fun getCertificateHashesForUrl(url: String): List<String> {
        return listOf(
            // Replace with actual certificate hashes
            "sha256/REPLACE_WITH_ACTUAL_HASH_1=",
            "sha256/REPLACE_WITH_ACTUAL_HASH_2="
        )
    }
}