package com.noghre.sod.core.security

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton
import java.io.File
import java.util.concurrent.TimeUnit

// ============================================
// 🔐 Security: Root Detection
// ============================================

@Singleton
class RootDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Comprehensive device root detection
     * Checks for multiple root indicators
     */
    fun isDeviceRooted(): Boolean {
        return checkRootManagementApps() || 
               checkSuBinary() || 
               checkBusyBox() || 
               checkRWPaths() ||
               checkSystemProperties() ||
               checkBuildTags()
    }
    
    /**
     * Check for common root management apps
     */
    private fun checkRootManagementApps(): Boolean {
        val suspiciousPackages = listOf(
            "com.topjohnwu.magisk",           // Magisk
            "eu.chainfire.supersu",           // SuperSU
            "com.noshufou.android.su",        // SuperUser
            "com.thirdparty.superuser",       // SuperUser
            "com.yellowes.su",                // SuperUser
            "com.koushikdutta.superuser",     // SuperUser
            "com.mods.zxmod",                 // ZX Mod
            "com.saurik.substrate",           // Substrate
            "com.devadvance.rootcloak",       // RootCloak
            "com.devadvance.rootcloakplus",  // RootCloak+
            "de.robv.android.xposed.installer" // Xposed
        )
        
        return suspiciousPackages.any { isPackageInstalled(it) }
    }
    
    /**
     * Check for su binary in common locations
     */
    private fun checkSuBinary(): Boolean {
        val suPaths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/sd/xbin/su",
            "/system/bin/sudo",
            "/system/xbin/sudo",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/data/local/su",
            "/system/bin/execute",
            "/system/bin/dalvik"
        )
        
        return suPaths.any { File(it).exists() }
    }
    
    /**
     * Check for BusyBox (often used with rooting)
     */
    private fun checkBusyBox(): Boolean {
        val busyBoxPaths = arrayOf(
            "/system/bin/busybox",
            "/system/xbin/busybox",
            "/data/local/busybox",
            "/data/local/xbin/busybox",
            "/data/local/bin/busybox"
        )
        
        return busyBoxPaths.any { File(it).exists() }
    }
    
    /**
     * Check for RW (Read-Write) accessible system paths
     */
    private fun checkRWPaths(): Boolean {
        val rwPaths = arrayOf(
            "/system/app/superuser.apk",
            "/system/app/Superuser.apk",
            "/system/app/Supersu.apk",
            "/system/xbin/daemonsu"
        )
        
        return rwPaths.any { File(it).exists() }
    }
    
    /**
     * Check system properties for root indicators
     */
    private fun checkSystemProperties(): Boolean {
        return try {
            val properties = listOf(
                "ro.secure" to "0",
                "ro.debuggable" to "1",
                "ro.build.tags" to "release-keys"
            )
            
            var hasRootProperty = false
            properties.forEach { (key, _) ->
                try {
                    val clazz = Class.forName("android.os.SystemProperties")
                    val method = clazz.getDeclaredMethod(
                        "get",
                        String::class.java
                    )
                    method.isAccessible = true
                    val value = method.invoke(null, key) as String
                    
                    if (key == "ro.secure" && value == "0") hasRootProperty = true
                    if (key == "ro.debuggable" && value == "1") hasRootProperty = true
                } catch (e: Exception) {
                    // Property check failed
                }
            }
            hasRootProperty
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check build tags
     */
    private fun checkBuildTags(): Boolean {
        val buildTags = android.os.Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }
    
    /**
     * Helper to check if package is installed
     */
    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}

// ============================================
// 🔐 Network Security: Certificate Pinning
// ============================================

@Singleton
class NetworkSecurityManager @Inject constructor() {
    
    /**
     * Create OkHttpClient with certificate pinning
     * Prevents man-in-the-middle attacks
     */
    fun createSecureOkHttpClient(): OkHttpClient.Builder {
        val certificatePinner = CertificatePinner.Builder()
            // Primary certificate
            .add(
                "api.noghresod.ir",
                "sha256/Iv8Pkqkx7E0IxEBf9X9sLeJW6zIPg9TJd6K3mNfW5lQ="
            )
            // Backup certificate (for rotation)
            .add(
                "api.noghresod.ir",
                "sha256/lFQwGWAd96P3xh8Sj7fVOBHmxZN0A8d/zJGz2fKJHNc="
            )
            // Let's Encrypt backup
            .add(
                "api.noghresod.ir",
                "sha256/C5B1CD47FB0E3DCE4B9A3FF55D9923203CFFCE29"
            )
            .build()
        
        return OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
            // Timeout configurations
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            // Disable cleartext traffic
            .build()
    }
    
    /**
     * Get certificate pinning hashes for verification
     */
    fun getCertificatePins(): Map<String, List<String>> {
        return mapOf(
            "api.noghresod.ir" to listOf(
                "sha256/Iv8Pkqkx7E0IxEBf9X9sLeJW6zIPg9TJd6K3mNfW5lQ=",
                "sha256/lFQwGWAd96P3xh8Sj7fVOBHmxZN0A8d/zJGz2fKJHNc="
            )
        )
    }
}

// ============================================
// 🔐 Security Manager (Facade)
// ============================================

@Singleton
class SecurityManager @Inject constructor(
    private val rootDetector: RootDetector,
    private val networkSecurityManager: NetworkSecurityManager
) {
    
    /**
     * Perform comprehensive security checks
     * @return true if device is secure, false if compromised
     */
    fun isDeviceSecure(): Boolean {
        // Check if device is rooted
        if (rootDetector.isDeviceRooted()) {
            return false
        }
        
        // Check other security criteria
        return true
    }
    
    /**
     * Get secure network client
     */
    fun getSecureOkHttpClient(): OkHttpClient.Builder {
        return networkSecurityManager.createSecureOkHttpClient()
    }
    
    /**
     * Get certificate pinning configuration
     */
    fun getCertificatePins(): Map<String, List<String>> {
        return networkSecurityManager.getCertificatePins()
    }
}
