package com.noghre.sod.core.security

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Detects if the device is rooted and alerts the user.
 * Prevents app execution on compromised devices.
 */
@Singleton
class RootDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Check if device is rooted using multiple methods.
     * @return true if device appears to be rooted
     */
    fun isDeviceRooted(): Boolean {
        return checkRootManagementApps() ||
                checkSuBinary() ||
                checkBusyBox() ||
                checkRWPaths() ||
                checkDangerousProps()
    }

    /**
     * Check for common root management applications.
     */
    private fun checkRootManagementApps(): Boolean {
        val rootApps = listOf(
            "com.topjohnwu.magisk",      // Magisk
            "eu.chainfire.supersu",      // SuperSU
            "com.koushikdutta.superuser",// Koush Superuser
            "com.zachspong.temprootremovejb",
            "com.ramdroid.appquarantine",
            "com.noshufou.android.su",
            "com.mhuang.overpower.proccontrol",
            "me.phh.superuser",
            "stericson.busybox",
            "com.thecodeFactory.RootTools"
        )

        for (app in rootApps) {
            if (isPackageInstalled(app)) {
                return true
            }
        }
        return false
    }

    /**
     * Check for su binary in common locations.
     */
    private fun checkSuBinary(): Boolean {
        val suPaths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/su/bin/su",
            "/data/adb/magisk/su",
            "/data/local/su",
            "/data/local/bin/su",
            "/data/local/xbin/su",
            "/system/app/SuperSU.apk",
            "/system/app/SuperSu.apk",
            "/system/app/Superuser.apk"
        )

        for (path in suPaths) {
            if (java.io.File(path).exists()) {
                return true
            }
        }
        return false
    }

    /**
     * Check for BusyBox in common locations.
     */
    private fun checkBusyBox(): Boolean {
        val busyBoxPaths = arrayOf(
            "/system/bin/busybox",
            "/system/xbin/busybox",
            "/sbin/busybox",
            "/data/adb/magisk/busybox",
            "/data/local/busybox",
            "/data/local/bin/busybox",
            "/data/local/xbin/busybox"
        )

        for (path in busyBoxPaths) {
            if (java.io.File(path).exists()) {
                return true
            }
        }
        return false
    }

    /**
     * Check for write permissions on system directories.
     */
    private fun checkRWPaths(): Boolean {
        val roPaths = arrayOf(
            "/system",
            "/system/bin",
            "/system/sbin",
            "/system/xbin",
            "/vendor/bin",
            "/sbin",
            "/etc"
        )

        for (path in roPaths) {
            try {
                val file = java.io.File(path)
                if (file.canWrite()) {
                    return true
                }
            } catch (e: Exception) {
                // Permission denied = good
            }
        }
        return false
    }

    /**
     * Check dangerous system properties.
     */
    private fun checkDangerousProps(): Boolean {
        val dangerousProps = mapOf(
            "ro.secure" to "0",
            "ro.debuggable" to "1",
            "ro.build.selinux" to "0"
        )

        for ((prop, value) in dangerousProps) {
            try {
                val actualValue = getSystemProperty(prop)
                if (actualValue == value) {
                    return true
                }
            } catch (e: Exception) {
                // Property not found
            }
        }
        return false
    }

    /**
     * Get system property value using reflection.
     */
    private fun getSystemProperty(name: String): String? {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val method = clazz.getMethod("get", String::class.java)
            method.invoke(null, name) as? String
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Check if package is installed on device.
     */
    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Execute command and check for su output.
     */
    private fun executeCommand(command: String): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(command)
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            bufferedReader.readLine() != null
        } catch (e: Exception) {
            false
        }
    }
}