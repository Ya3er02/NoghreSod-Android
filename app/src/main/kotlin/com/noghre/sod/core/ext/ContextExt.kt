package com.noghre.sod.core.ext

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Extension functions for Android Context.
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */

/**
 * Check if device is in dark mode.
 */
fun Context.isDarkMode(): Boolean {
    return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
}

/**
 * Check if device is in light mode.
 */
fun Context.isLightMode(): Boolean = !isDarkMode()

/**
 * Get screen width in pixels.
 */
fun Context.getScreenWidth(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        wm.currentWindowMetrics.bounds.width()
    } else {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        wm.defaultDisplay.getMetrics(metrics)
        metrics.widthPixels
    }
}

/**
 * Get screen height in pixels.
 */
fun Context.getScreenHeight(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        wm.currentWindowMetrics.bounds.height()
    } else {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        wm.defaultDisplay.getMetrics(metrics)
        metrics.heightPixels
    }
}

/**
 * Get screen density scale factor.
 */
fun Context.getScreenDensity(): Float = resources.displayMetrics.density

/**
 * Convert dp to pixels.
 */
fun Context.dpToPx(dp: Int): Int {
    return (dp * getScreenDensity()).toInt()
}

/**
 * Convert pixels to dp.
 */
fun Context.pxToDp(px: Int): Int {
    return (px / getScreenDensity()).toInt()
}

/**
 * Check if device is tablet.
 */
fun Context.isTablet(): Boolean {
    return (resources.configuration.screenLayout and
            Configuration.SCREENLAYOUT_SIZE_MASK) >=
            Configuration.SCREENLAYOUT_SIZE_LARGE
}

/**
 * Check if device is portrait.
 */
fun Context.isPortrait(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

/**
 * Check if device is landscape.
 */
fun Context.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

/**
 * Get app version code.
 */
fun Context.getAppVersionCode(): Long {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageManager.getPackageInfo(packageName, 0).longVersionCode
    } else {
        @Suppress("DEPRECATION")
        packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
    }
}

/**
 * Get app version name.
 */
fun Context.getAppVersionName(): String {
    return packageManager.getPackageInfo(packageName, 0).versionName ?: "1.0.0"
}

/**
 * Get app name/label.
 */
fun Context.getAppName(): String {
    return resources.getString(applicationInfo.labelRes)
}

/**
 * Check if running on Android API level or higher.
 */
fun Context.isAtLeastApi(level: Int): Boolean = Build.VERSION.SDK_INT >= level

/**
 * Check if running on specific Android API level.
 */
fun Context.isApi(level: Int): Boolean = Build.VERSION.SDK_INT == level

/**
 * Get locale language code (e.g., "fa", "en").
 */
fun Context.getLanguageCode(): String {
    return resources.configuration.locales[0].language
}

/**
 * Check if current language is Persian/Farsi.
 */
fun Context.isPersian(): Boolean {
    return getLanguageCode() == "fa"
}

/**
 * Check if current language is RTL.
 */
fun Context.isRtl(): Boolean {
    return resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
}

/**
 * Composable to get current context.
 */
@Composable
fun getApplicationContext(): Context = LocalContext.current

// Import View for LAYOUT_DIRECTION_RTL
import android.view.View
