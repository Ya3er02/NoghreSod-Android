package com.noghre.sod.core.security

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * 🔒 Screenshot Prevention for Sensitive Screens
 * 
 * Prevents screenshot capture and screen recording on payment screens.
 * Applied to:
 * - Payment/Checkout screens
 * - User profile pages
 * - Saved payment methods
 * - Order details with sensitive info
 */

/**
 * Composable that prevents screenshots on the current screen
 * 
 * Usage:
 * ```kotlin
 * @Composable
 * fun PaymentScreen() {
 *     SecureScreenEffect()  // Add this at the top of the composable
 *     
 *     // Rest of the screen content
 * }
 * ```
 * 
 * The screenshot protection is automatically removed when the
 * user navigates away from this composable.
 */
@Composable
fun SecureScreenEffect() {
    val context = LocalContext.current
    
    DisposableEffect(Unit) {
        // Enable secure mode when composable is shown
        (context as? Activity)?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        
        // Remove secure mode when composable is hidden/disposed
        onDispose {
            (context as? Activity)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}

/**
 * Extension function to easily enable secure mode on activities
 * 
 * Usage:
 * ```kotlin
 * class PaymentActivity : AppCompatActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         enableSecureMode()  // Add this line
 *     }
 * }
 * ```
 */
fun Activity.enableSecureMode() {
    window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
}

/**
 * Extension function to disable secure mode
 */
fun Activity.disableSecureMode() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
}

/**
 * Check if secure mode is currently enabled
 */
fun Activity.isSecureModeEnabled(): Boolean {
    return (window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE) != 0
}

/**
 * Secure Screen Manager for better control
 * 
 * Useful for screens that toggle between secure and non-secure modes
 */
class SecureScreenManager(private val activity: Activity) {
    
    /**
     * Enable screenshot prevention
     */
    fun enableSecure() {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
    
    /**
     * Disable screenshot prevention
     */
    fun disableSecure() {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
    
    /**
     * Toggle secure mode
     */
    fun toggleSecure(): Boolean {
        return if (activity.isSecureModeEnabled()) {
            disableSecure()
            false
        } else {
            enableSecure()
            true
        }
    }
    
    /**
     * Check current state
     */
    fun isSecure(): Boolean = activity.isSecureModeEnabled()
}
