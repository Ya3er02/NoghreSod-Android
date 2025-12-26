package com.noghre.sod.security

import android.content.Context
import android.content.pm.PackageManager
import java.io.File

/**
 * Root detection utility for identifying compromised devices.
 * Checks multiple indicators of root/jailbreak status.
 *
 * @author Yaser
 * @version 1.0.0
 */
object RootDetector {

    /**
     * Check if device is rooted.
     * Performs multiple checks to detect root indicators.
     *
     * @param context Android context
     * @return true if device appears to be rooted
     */
    fun isDeviceRooted(context: Context): Boolean {
        return checkRootFiles() || checkRootProcess() || checkRootApps(context) ||
                hasTestKeys() || checkDangerousProperties()
    }

    /**
     * Check for common root files on device.
     * Looks for su executable and Superuser APK.
     *
     * @return true if root files detected
     */
    private fun checkRootFiles(): Boolean {
        val rootFiles = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su",
            "/system/bin/.ext/su",
            "/system/xbin/daemonsu",
            "/system/bin/su.backup"
        )
        return rootFiles.any { File(it).exists() }
    }

    /**
     * Check for root using 'which su' command.
     * Attempts to find 'su' binary via system PATH.
     *
     * @return true if 'su' process found
     */
    private fun checkRootProcess(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/bin/which", "su"))
            val inputStream = process.inputStream
            val available = inputStream.available()
            inputStream.close()
            available > 0
        } catch (e: Exception) {
            false
        } finally {
            try {
                process?.destroy()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    /**
     * Check for root management apps installed on device.
     * Detects common rooting applications.
     *
     * @param context Android context
     * @return true if root apps detected
     */
    private fun checkRootApps(context: Context): Boolean {
        val rootApps = arrayOf(
            // Rooting apps
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk",
            "com.koushikdutta.rommanager",
            "com.koushikdutta.bokeh",
            "com.dimonvideo.luckypatcher",
            "de.robv.android.xposed.installer",
            "com.frostwire.android"
        )

        val packageManager = context.packageManager
        return rootApps.any { packageName ->
            try {
                packageManager.getPackageInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    /**
     * Check if device has test keys (custom ROM indicator).
     * Test keys indicate the device is not an official build.
     *
     * @return true if test keys detected
     */
    fun hasTestKeys(): Boolean {
        val buildTags = android.os.Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    /**
     * Check for dangerous system properties.
     * Indicates debuggable or insecure device state.
     *
     * @return true if dangerous properties detected
     */
    fun checkDangerousProperties(): Boolean {
        val dangerousProps = mapOf(
            "ro.debuggable" to "1",
            "ro.secure" to "0"
        )
        return dangerousProps.any { (key, value) ->
            try {
                val actualValue = System.getProperty(key) ?: return@any false
                actualValue == value
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Get detailed root status information.
     * Useful for logging and debugging.
     *
     * @param context Android context
     * @return String describing root status
     */
    fun getRootStatusInfo(context: Context): String {
        return buildString {
            append("Root Status: ")
            append(if (isDeviceRooted(context)) "ROOTED" else "SECURE")
            append("\n")
            append("Test Keys: ").append(hasTestKeys()).append("\n")
            append("Dangerous Properties: ").append(checkDangerousProperties()).append("\n")
            append("Root Files: ").append(checkRootFiles()).append("\n")
            append("Root Process: ").append(checkRootProcess()).append("\n")
            append("Root Apps: ").append(checkRootApps(context)).append("\n")
            append("Device: ").append(android.os.Build.DEVICE).append("\n")
            append("Product: ").append(android.os.Build.PRODUCT).append("\n")
        }
    }
}
