package com.noghre.sod.data.remote.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.security.cert.X509Certificate
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

/**
 * Certificate Pinning Interceptor
 * 
 * Validates that server certificates match expected pins.
 * Prevents MITM (Man-In-The-Middle) attacks by ensuring
 * only trusted certificates are accepted.
 * 
 * Security levels:
 * - Primary pin: Current certificate
 * - Backup pin: Certificate for rotation scenarios
 * 
 * @since 1.0.0
 */
class CertificatePinningInterceptor : Interceptor {
    
    companion object {
        private const val TAG = "CertPinning"
        
        // Valid certificate pins (SHA-256)
        private val VALID_PINS = listOf(
            "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",  // Primary
            "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="   // Backup
        )
        
        // Alternative backup pins (for certificate updates)
        private val ALTERNATIVE_PINS = listOf(
            "sha256/CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC="
        )
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.host
        
        // Skip pinning for localhost/debug environments
        if (isDebugDomain(url)) {
            return chain.proceed(request)
        }
        
        try {
            val response = chain.proceed(request)
            
            // Verify certificate after SSL handshake
            val handshake = response.handshake
            
            if (handshake == null) {
                throw SSLPeerUnverifiedException("No SSL handshake found")
            }
            
            // Check pinning
            verifyCertificatePinning(handshake.peerCertificates)
            
            return response
            
        } catch (e: SSLHandshakeException) {
            // Log security event (potential MITM attack)
            logSecurityEvent(
                eventType = "ssl_handshake_failed",
                domain = url,
                message = e.message ?: "Unknown SSL error"
            )
            throw e
            
        } catch (e: SSLPeerUnverifiedException) {
            // Certificate verification failed
            logSecurityEvent(
                eventType = "certificate_pinning_failed",
                domain = url,
                message = "Certificate does not match expected pins"
            )
            throw e
        }
    }
    
    /**
     * Verify certificate pins against expected values
     * 
     * @param certificates List of certificates in chain
     * @throws SSLPeerUnverifiedException if pin validation fails
     */
    private fun verifyCertificatePinning(certificates: List<java.security.cert.Certificate>) {
        if (certificates.isEmpty()) {
            throw SSLPeerUnverifiedException("No certificates in chain")
        }
        
        val pinnedCerts = VALID_PINS + ALTERNATIVE_PINS
        
        for (cert in certificates) {
            if (cert is X509Certificate) {
                try {
                    // Generate SHA-256 pin of this certificate
                    val pin = generateSHA256Pin(cert)
                    
                    // Check if this pin is in our valid list
                    if (pinnedCerts.contains(pin)) {
                        Log.d(TAG, "✅ Certificate pin verified: $pin")
                        return
                    }
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error generating pin: ${e.message}")
                }
            }
        }
        
        // No valid pin found
        throw SSLPeerUnverifiedException(
            "None of the certificates in chain match expected pins. " +
            "This may indicate a MITM attack or an expired certificate."
        )
    }
    
    /**
     * Generate SHA-256 pin for certificate
     * 
     * Pin format: sha256/<base64-encoded-hash>
     * 
     * @param cert X509Certificate to pin
     * @return SHA-256 pin string
     */
    private fun generateSHA256Pin(cert: X509Certificate): String {
        try {
            val publicKeyBytes = cert.publicKey.encoded
            val md = java.security.MessageDigest.getInstance("SHA-256")
            val digest = md.digest(publicKeyBytes)
            val base64 = android.util.Base64.encodeToString(
                digest,
                android.util.Base64.NO_WRAP
            )
            return "sha256/$base64"
        } catch (e: Exception) {
            throw SSLPeerUnverifiedException("Cannot generate certificate pin: ${e.message}")
        }
    }
    
    /**
     * Check if domain is debug environment
     */
    private fun isDebugDomain(host: String): Boolean {
        return host.contains("localhost") ||
               host.contains("127.0.0.1") ||
               host.contains("192.168") ||
               host.contains("10.0.0")
    }
    
    /**
     * Log security event for monitoring
     * 
     * These should be sent to your analytics/crash reporting service
     */
    private fun logSecurityEvent(
        eventType: String,
        domain: String,
        message: String
    ) {
        Log.w(
            TAG,
            "🚨 SECURITY EVENT: $eventType | Domain: $domain | Message: $message"
        )
        
        // TODO: Send to Crashlytics/Firebase Analytics
        // FirebaseAnalytics.getInstance().logEvent(
        //     "security_event",
        //     bundleOf(
        //         "event_type" to eventType,
        //         "domain" to domain,
        //         "message" to message,
        //         "timestamp" to System.currentTimeMillis()
        //     )
        // )
    }
}
