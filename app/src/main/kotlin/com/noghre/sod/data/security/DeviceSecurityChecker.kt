package com.noghre.sod.data.security

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import android.provider.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🛰️ Device Security Checker
 *
 * Multi-layer device security validation:
 * - Root detection (multiple methods)
 * - Emulator detection
 * - Debugger detection
 * - App tampering detection
 * - Hook framework detection (Xposed, Substrate)
 * - USB debugging enabled
 * - Developer mode detection
 * - Installation source validation
 *
 * @since 1.0.0
 */
@Singleton
class DeviceSecurityChecker @Inject constructor(
    private val context: Context,
    private val analyticsTracker: AnalyticsTracker
) {
    
    companion object {
        // Root indicators
        private val ROOT_PATHS = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        
        // Root management apps
        private val ROOT_APPS = arrayOf(
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk"
        )
        
        // Hook frameworks
        private val HOOK_FRAMEWORKS = arrayOf(
            "de.robv.android.xposed.installer",
            "com.saurik.substrate",
            "com.zachspong.frauddetector"
        )
    }
    
    data class SecurityCheckResult(
        val isSecure: Boolean,
        val threats: List<SecurityThreat>,
        val riskLevel: RiskLevel
    )
    
    enum class SecurityThreat {
        ROOT_DETECTED,
        EMULATOR_DETECTED,
        DEBUGGER_ATTACHED,
        APP_TAMPERED,
        HOOK_FRAMEWORK_DETECTED,
        USB_DEBUGGING_ENABLED,
        DEVELOPER_MODE_ENABLED,
        UNKNOWN_SOURCE_INSTALL
    }
    
    enum class RiskLevel {
        NONE,       // بلا مشکل
        LOW,        // ریسک کم - ارتفاعی
        MEDIUM,     // ریسک متوسط - هشدار
        HIGH,       // ریسک بالا - محدود کردن قابلیتها
        CRITICAL    // ریسک بحرانی - بستن اپلیکیشن
    }
    
    /**
     * Perform all security checks
     */
    suspend fun performSecurityCheck(): SecurityCheckResult = withContext(Dispatchers.IO) {
        val threats = mutableListOf<SecurityThreat>()
        
        // 1. Root Detection
        if (isDeviceRooted()) {
            threats.add(SecurityThreat.ROOT_DETECTED)
            Timber.w("⚠️ Root detected on device")
        }
        
        // 2. Emulator Detection
        if (isEmulator()) {
            threats.add(SecurityThreat.EMULATOR_DETECTED)
            Timber.w("⚠️ Running on emulator")
        }
        
        // 3. Debugger Detection
        if (isDebuggerAttached()) {
            threats.add(SecurityThreat.DEBUGGER_ATTACHED)
            Timber.w("⚠️ Debugger is attached")
        }
        
        // 4. App Tampering Detection
        if (isAppTampered()) {
            threats.add(SecurityThreat.APP_TAMPERED)
            Timber.w("⚠️ App signature tampered")
        }
        
        // 5. Hook Framework Detection
        if (isHookFrameworkInstalled()) {
            threats.add(SecurityThreat.HOOK_FRAMEWORK_DETECTED)
            Timber.w("⚠️ Hook framework detected")
        }
        
        // 6. Developer Settings
        if (isUsbDebuggingEnabled()) {
            threats.add(SecurityThreat.USB_DEBUGGING_ENABLED)
        }
        
        if (isDeveloperModeEnabled()) {
            threats.add(SecurityThreat.DEVELOPER_MODE_ENABLED)
        }
        
        // 7. Installation Source
        if (isInstalledFromUnknownSource()) {
            threats.add(SecurityThreat.UNKNOWN_SOURCE_INSTALL)
            Timber.w("⚠️ App installed from unknown source")
        }
        
        val riskLevel = calculateRiskLevel(threats)
        
        // Track to analytics
        trackSecurityCheckResult(threats, riskLevel)
        
        SecurityCheckResult(
            isSecure = threats.isEmpty(),
            threats = threats,
            riskLevel = riskLevel
        )
    }
    
    // ==================== ROOT DETECTION ====================
    
    private fun isDeviceRooted(): Boolean {
        return checkRootBinaries() ||
               checkRootApps() ||
               checkRootProperties() ||
               checkSuperuserApk() ||
               checkRWPaths()
    }
    
    private fun checkRootBinaries(): Boolean {
        return ROOT_PATHS.any { path ->
            try {
                File(path).exists()
            } catch (e: Exception) {
                false
            }
        }
    }
    
    private fun checkRootApps(): Boolean {
        val pm = context.packageManager
        return ROOT_APPS.any { packageName ->
            try {
                pm.getPackageInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }
    
    private fun checkRootProperties(): Boolean {
        return try {
            val buildTags = Build.TAGS
            buildTags != null && buildTags.contains("test-keys")
        } catch (e: Exception) {
            false
        }
    }
    
    private fun checkSuperuserApk(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("which su")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            reader.readLine() != null
        } catch (e: Exception) {
            false
        }
    }
    
    private fun checkRWPaths(): Boolean {
        val paths = arrayOf("/system", "/system/bin", "/system/sbin", "/system/xbin")
        return paths.any { path ->
            try {
                val mountFile = File(path)
                mountFile.canWrite()
            } catch (e: Exception) {
                false
            }
        }
    }
    
    // ==================== EMULATOR DETECTION ====================
    
    private fun isEmulator(): Boolean {
        return checkEmulatorProperties() ||
               checkEmulatorFiles() ||
               checkEmulatorBuild()
    }
    
    private fun checkEmulatorProperties(): Boolean {
        val qemu = System.getProperty("ro.kernel.qemu", "0")
        return qemu == "1"
    }
    
    private fun checkEmulatorFiles(): Boolean {
        val emulatorFiles = arrayOf(
            "/dev/socket/qemud",
            "/dev/qemu_pipe",
            "/system/lib/libc_malloc_debug_qemu.so"
        )
        
        return emulatorFiles.any { File(it).exists() }
    }
    
    private fun checkEmulatorBuild(): Boolean {
        val fingerprint = Build.FINGERPRINT
        return fingerprint.contains("generic") ||
               fingerprint.contains("unknown") ||
               Build.MODEL.contains("google_sdk") ||
               Build.MODEL.contains("Emulator") ||
               Build.MODEL.contains("Android SDK") ||
               Build.MANUFACTURER.contains("Genymotion") ||
               Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
               Build.PRODUCT == "google_sdk"
    }
    
    // ==================== DEBUGGER DETECTION ====================
    
    private fun isDebuggerAttached(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }
    
    // ==================== APP TAMPERING DETECTION ====================
    
    private fun isAppTampered(): Boolean {
        // In production, verify app signature against expected hash
        // This is a simplified version
        return !verifyAppSignature()
    }
    
    private fun verifyAppSignature(): Boolean {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            }
            
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners != null &&
                packageInfo.signingInfo!!.apkContentsSigners.isNotEmpty()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures != null && packageInfo.signatures.isNotEmpty()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to verify app signature")
            return false
        }
    }
    
    // ==================== HOOK FRAMEWORK DETECTION ====================
    
    private fun isHookFrameworkInstalled(): Boolean {
        val pm = context.packageManager
        return HOOK_FRAMEWORKS.any { packageName ->
            try {
                pm.getPackageInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        } || checkXposedInstalled()
    }
    
    private fun checkXposedInstalled(): Boolean {
        return try {
            throw Exception("Xposed check")
        } catch (e: Exception) {
            e.stackTrace.any { element ->
                element.className.contains("de.robv.android.xposed")
            }
        }
    }
    
    // ==================== DEVELOPER SETTINGS ====================
    
    private fun isUsbDebuggingEnabled(): Boolean {
        return try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }
    }
    
    private fun isDeveloperModeEnabled(): Boolean {
        return try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }
    }
    
    // ==================== INSTALLATION SOURCE ====================
    
    private fun isInstalledFromUnknownSource(): Boolean {
        return try {
            val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getInstallerPackageName(context.packageName)
            }
            
            // Trusted sources
            val trustedSources = listOf(
                "com.android.vending",  // Google Play
                "com.google.android.feedback"  // Google internal
            )
            
            installer !in trustedSources
        } catch (e: Exception) {
            false
        }
    }
    
    // ==================== HELPER METHODS ====================
    
    private fun calculateRiskLevel(threats: List<SecurityThreat>): RiskLevel {
        if (threats.isEmpty()) return RiskLevel.NONE
        
        val criticalThreats = listOf(
            SecurityThreat.ROOT_DETECTED,
            SecurityThreat.APP_TAMPERED,
            SecurityThreat.HOOK_FRAMEWORK_DETECTED
        )
        
        val highThreats = listOf(
            SecurityThreat.DEBUGGER_ATTACHED,
            SecurityThreat.EMULATOR_DETECTED
        )
        
        return when {
            threats.any { it in criticalThreats } -> RiskLevel.CRITICAL
            threats.any { it in highThreats } -> RiskLevel.HIGH
            threats.size >= 3 -> RiskLevel.MEDIUM
            threats.isNotEmpty() -> RiskLevel.LOW
            else -> RiskLevel.NONE
        }
    }
    
    private fun trackSecurityCheckResult(
        threats: List<SecurityThreat>,
        riskLevel: RiskLevel
    ) {
        analyticsTracker.trackEvent("security_check", mapOf(
            "threats" to threats.map { it.name },
            "risk_level" to riskLevel.name,
            "threat_count" to threats.size
        ))
    }
}
