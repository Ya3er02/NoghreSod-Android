package com.noghre.sod.core.network

import okhttp3.CertificatePinner

/**
 * Certificate Pinning configuration for network security.
 * Prevents man-in-the-middle attacks by pinning SSL certificates.
 */
object CertificatePinningConfig {

    /**
     * Create certificate pinner for API domains.
     * SHA256 certificates should be obtained from your server.
     */
    fun createCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            // Main API domain
            .add(
                "api.noghresod.ir",
                // Primary certificate (replace with actual SHA256)
                "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
                // Backup certificate (replace with actual SHA256)
                "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="
            )
            // CDN for images
            .add(
                "cdn.noghresod.ir",
                "sha256/CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC=",
                "sha256/DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD="
            )
            // Firebase domains
            .add(
                "firebaseremoteconfig.googleapis.com",
                "sha256/EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE="
            )
            .build()
    }

    /**
     * Get SHA256 hash of certificate:
     * 
     * openssl s_client -connect api.noghresod.ir:443 < /dev/null |
     * openssl x509 -outform DER |
     * openssl dgst -sha256 -binary |
     * openssl enc -base64
     */
}