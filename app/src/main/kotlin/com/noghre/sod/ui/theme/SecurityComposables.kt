package com.noghre.sod.ui.theme

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Disables screenshots on the current screen.
 * Use this for sensitive screens like payment, authentication, etc.
 *
 * Example usage:
 * ```
 * @Composable
 * fun PaymentScreen() {
 *     SecureScreenEffect()
 *     // Screen content
 * }
 * ```
 *
 * Note: Screenshots are re-enabled when the screen is disposed.
 */
@Composable
fun SecureScreenEffect() {
    val activity = LocalContext.current as? Activity
    
    DisposableEffect(Unit) {
        // Enable screenshot prevention when entering the screen
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        
        // Re-enable screenshots when leaving the screen
        onDispose {
            activity?.window?.clearFlags(
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
    }
}

/**
 * Disables screenshots and recording for the entire session.
 * Use for payment flow, sensitive data, etc.
 *
 * Should be called in the Activity onCreate() or Composable.
 */
fun Activity.enableSecureMode() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_SECURE,
        WindowManager.LayoutParams.FLAG_SECURE
    )
}

/**
 * Re-enables normal screenshot behavior.
 */
fun Activity.disableSecureMode() {
    window.clearFlags(
        WindowManager.LayoutParams.FLAG_SECURE
    )
}