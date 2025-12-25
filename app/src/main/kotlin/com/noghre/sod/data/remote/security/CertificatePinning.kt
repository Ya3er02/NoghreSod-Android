package com.noghre.sod.data.remote.security

import com.squareup.okhttp3.CertificatePinner
import com.squareup.okhttp3.OkHttpClient
import timber.log.Timber

/**
 * Certificate pinning configuration for secure API communication.
 * Pins SSL certificates to prevent man-in-the-middle attacks.
 */
object CertificatePinningConfig {

    /**
     * Configure certificate pinner for OkHttp client.
     * Add your API domain and certificate SHA256 hashes.
     */
    fun createCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            // Production API
            .add(
                "api.noghresod.com",
                "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
                "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="
            )
            // Backup certificate
            .add(
                "api.noghresod.com",
                "sha256/CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC="
            )
            .build()
    }

    /**
     * Apply certificate pinning to OkHttpClient.
     * Use this in your network module.
     */
    fun applyPinning(clientBuilder: OkHttpClient.Builder): OkHttpClient.Builder {
        return try {
            clientBuilder.certificatePinner(createCertificatePinner())
                .also {
                    Timber.d("Certificate pinning applied successfully")
                }
        } catch (e: Exception) {
            Timber.e(e, "Error applying certificate pinning")
            clientBuilder
        }
    }

    /**
     * Generate certificate SHA256 hash from certificate file.
     * Run this command to get certificate pins:
     *
     * openssl s_client -servername api.noghresod.com -connect api.noghresod.com:443 < /dev/null | \
     * openssl x509 -noout -pubkey | \
     * openssl pkey -pubin -outform der | \
     * openssl dgst -sha256 -binary | \
     * openssl enc -base64
     *
     * Or use:
     * keytool -printcert -sslserver api.noghresod.com -rfc | \
     * openssl x509 -noout -pubkey | \
     * openssl pkey -pubin -outform der | \
     * openssl dgst -sha256 -binary | \
     * openssl enc -base64
     */
}
