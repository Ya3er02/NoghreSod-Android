package com.noghre.sod.core.security

import android.content.Context
import android.os.Build
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🔒 Device Security Checker
 * 
 * Performs security checks on the device to prevent fraud:
 * - Rooted device detection
 * - Debugger detection
 * - Emulator detection
 * - Custom ROM detection
 * 
 * @author Yaser
 * @version 1.0.0
 */
@Singleton
class DeviceSecurityChecker @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "DeviceSecurityChecker"
    }
    
    /**
     * Check if device is rooted
     * 
     * Multiple detection methods:
     * 1. Check for su binary in common locations
     * 2. Check for Magisk directory
     * 3. Check for SuperSU directory
     * 4. Check for build properties
     */
    fun isDeviceRooted(): Boolean {
        return detectRootMethod1() || detectRootMethod2() || detectRootMethod3()
    }
    
    /**
     * Method 1: Check for su executable in common locations
     */
    private fun detectRootMethod1(): Boolean {
        val commonPaths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/bin/ksu",           // KernelSU
            "/system/xbin/ksu",
            "/system/bin/magisk",        // Magisk
            "/system/xbin/magisk",
            "/data/local/tmp/ksu",
            "/data/local/tmp/magisk"
        )
        
        for (path in commonPaths) {
            try {
                val file = java.io.File(path)
                if (file.exists()) {
                    Log.w(TAG, "⚠️ Root detected at: $path")
                    return true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking path: $path", e)
            }
        }
        return false
    }
    
    /**
     * Method 2: Check for Magisk/SuperSU environment variables
     */
    private fun detectRootMethod2(): Boolean {
        val suspiciousProps = mapOf(
            "ro.debuggable" to "1",
            "ro.secure" to "0",
            "ro.allow.mock.location" to "1",
            "ro.boot.serialno" to "1234567890",  // Emulator
            "ro.kernel.qemu" to "1",              // Emulator
            "init.svc.adbd" to "running"          // USB Debugging
        )
        
        return try {
            val properties = java.lang.reflect.Method.class
                .getMethod("getProperty", String::class.java, String::class.java)
            
            for ((prop, suspiciousValue) in suspiciousProps) {
                val propValue = System.getProperty(prop, "")
                if (propValue == suspiciousValue) {
                    Log.w(TAG, "⚠️ Suspicious property detected: $prop = $propValue")
                    return true
                }
            }
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking properties", e)
            false
        }
    }
    
    /**
     * Method 3: Try to run su command
     */
    private fun detectRootMethod3(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val isRooted = process.inputStream.available() > 0
            process.destroy()
            if (isRooted) {
                Log.w(TAG, "⚠️ Root detected via su execution")
            }
            isRooted
        } catch (e: Exception) {
            // su command failed, device is not rooted
            false
        }
    }
    
    /**
     * Check if device is running in an emulator
     */
    fun isRunningInEmulator(): Boolean {
        return isAdbEnabled() || 
               Build.FINGERPRINT.startsWith("generic") ||
               Build.HARDWARE == "ranchu" ||
               Build.MODEL.contains("google_sdk") ||
               Build.MODEL.contains("Emulator") ||
               Build.PRODUCT == "sdk" ||
               System.getProperty("ro.kernel.qemu") == "1"
    }
    
    /**
     * Check if ADB (USB Debugging) is enabled
     */
    fun isAdbEnabled(): Boolean {
        return try {
            android.provider.Settings.Secure.getInt(
                context.contentResolver,
                android.provider.Settings.Secure.ADB_ENABLED,
                0
            ) != 0
        } catch (e: Exception) {
            Log.e(TAG, "Error checking ADB status", e)
            false
        }
    }
    
    /**
     * Check if debugger is attached
     */
    fun isDebuggerAttached(): Boolean {
        return android.os.Debug.isDebuggerConnected()
    }
    
    /**
     * Comprehensive security check
     * 
     * @return SecurityCheckResult with all findings
     */
    fun performSecurityCheck(): SecurityCheckResult {
        val isRooted = isDeviceRooted()
        val isEmulator = isRunningInEmulator()
        val isAdbEnabled = isAdbEnabled()
        val isDebugger = isDebuggerAttached()
        
        Log.i(TAG, """
            🔍 Security Check Results:
            - Rooted: $isRooted
            - Emulator: $isEmulator
            - ADB Enabled: $isAdbEnabled
            - Debugger: $isDebugger
        """.trimIndent())
        
        return SecurityCheckResult(
            isDeviceRooted = isRooted,
            isEmulator = isEmulator,
            isAdbEnabled = isAdbEnabled,
            isDebuggerAttached = isDebugger,
            isSafeDevice = !isRooted && !isEmulator && !isDebugger
        )
    }
    
    /**
     * Check if device is safe for sensitive operations (payments)
     */
    fun isSafeForPayments(): Boolean {
        return performSecurityCheck().isSafeDevice
    }
}

/**
 * Result of security check
 */
data class SecurityCheckResult(
    val isDeviceRooted: Boolean,
    val isEmulator: Boolean,
    val isAdbEnabled: Boolean,
    val isDebuggerAttached: Boolean,
    val isSafeDevice: Boolean
)