package com.noghre.sod

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.noghre.sod.ui.theme.NoghreSodTheme
import com.noghre.sod.ui.navigation.NoghreSodNavigation
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Main Activity for Noghre Sod e-commerce app.
 *
 * Responsibilities:
 * - Set up edge-to-edge display with proper system bars handling
 * - Handle configuration changes (orientation, locale)
 * - Process deep links and navigate accordingly
 * - Manage activity lifecycle
 *
 * Features:
 * - RTL/LTR support for Persian language
 * - System bars transparency and theming
 * - Deep link routing to product/category screens
 * - Orientation change handling
 *
 * @author Yaser
 * @version 1.0.0
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup edge-to-edge display with proper system bars handling
        setupEdgeToEdge()
        
        // Handle deep link from intent
        handleDeepLink(intent)
        
        setContent {
            NoghreSodTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NoghreSodNavigation()
                }
            }
        }
    }
    
    /**
     * Called when a new intent is received while the activity is already running.
     * Used for handling deep links from notifications or external links.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Timber.d("New intent received")
        handleDeepLink(intent)
    }
    
    /**
     * Called when device configuration changes (orientation, locale, etc.).
     * Used to handle RTL/LTR switching and layout updates.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        
        // Log configuration changes
        Timber.d(
            "Configuration changed: orientation=${newConfig.orientation}, "
            + "locale=${newConfig.locales[0]}, density=${newConfig densityDpi}"
        )
        
        // Update layout direction based on locale (RTL/LTR)
        val layoutDirection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (newConfig.locales[0].language == "fa") {
                View.LAYOUT_DIRECTION_RTL
            } else {
                View.LAYOUT_DIRECTION_LTR
            }
        } else {
            @Suppress("DEPRECATION")
            if (newConfig.locale.language == "fa") {
                View.LAYOUT_DIRECTION_RTL
            } else {
                View.LAYOUT_DIRECTION_LTR
            }
        }
        window.decorView.layoutDirection = layoutDirection
    }
    
    /**
     * Configure edge-to-edge display with proper insets handling.
     * Makes the app extend to the edges of the screen including behind system bars.
     */
    private fun setupEdgeToEdge() {
        // Enable edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Set system bars appearance based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 12+
            window.insetsController?.apply {
                // Make status bar and navigation bar transparent
                window.statusBarColor = Color.TRANSPARENT
                window.navigationBarColor = Color.TRANSPARENT
                
                // Set light/dark mode for system bars based on theme
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Android 11 and below
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
            window.statusBarColor = Color.TRANSPARENT
            @Suppress("DEPRECATION")
            window.navigationBarColor = Color.TRANSPARENT
        }
        
        Timber.d("Edge-to-edge display configured")
    }
    
    /**
     * Handle deep link navigation.
     * Routes to appropriate screens based on URI path.
     *
     * Supported deep links:
     * - noghresod.ir/product/{productId} -> Product detail screen
     * - noghresod.ir/category/{categoryId} -> Category screen
     * - www.noghresod.ir/product/{productId} -> Product detail screen
     * - www.noghresod.ir/category/{categoryId} -> Category screen
     *
     * @param intent The intent containing the deep link URI
     */
    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            Timber.d("Deep link received: $uri")
            
            when {
                uri.path?.startsWith("/product") == true -> {
                    val productId = uri.lastPathSegment
                    Timber.d("Navigate to product: $productId")
                    // TODO: Navigate to product detail screen with productId
                    // Example:
                    // navController.navigate("product/$productId")
                }
                uri.path?.startsWith("/category") == true -> {
                    val categoryId = uri.lastPathSegment
                    Timber.d("Navigate to category: $categoryId")
                    // TODO: Navigate to category screen with categoryId
                    // Example:
                    // navController.navigate("category/$categoryId")
                }
                else -> {
                    Timber.w("Unknown deep link path: ${uri.path}")
                }
            }
        }
    }
}
