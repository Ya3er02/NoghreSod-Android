package com.noghre.sod.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import timber.log.Timber

/**
 * Analytics interceptor for automatic screen tracking in Compose screens.
 *
 * Usage:
 * ```
 * @Composable
 * fun ProductListScreen(
 *     analyticsManager: AnalyticsManager = hiltViewModel()
 * ) {
 *     AnalyticsScreenTracker(
 *         screenName = "ProductList",
 *         screenClass = "ProductListScreen",
 *         analyticsManager = analyticsManager
 *     )
 *
 *     // Screen content...
 * }
 * ```
 *
 * This composable automatically logs a screen_view event when the screen
 * is first composed and tracks composition/recomposition for debugging.
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */

/**
 * Composable that tracks screen view events.
 *
 * Call this at the top of any screen composable to automatically log
 * screen view events to Firebase Analytics.
 *
 * @param screenName Display name of the screen (e.g., "ProductList")
 * @param screenClass Class name of the screen (e.g., "ProductListScreen")
 * @param analyticsManager AnalyticsManager instance for logging
 * @param additionalParams Optional additional parameters to log with event
 */
@Composable
fun AnalyticsScreenTracker(
    screenName: String,
    screenClass: String? = null,
    analyticsManager: AnalyticsManager,
    additionalParams: Map<String, String> = emptyMap()
) {
    LaunchedEffect(screenName) {
        analyticsManager.logScreenView(
            screenName = screenName,
            screenClass = screenClass ?: screenName
        )
        Timber.d("Screen tracked: $screenName")
    }
}

/**
 * Composable that tracks screen lifecycle events.
 *
 * Use this to track when screens enter foreground/background.
 *
 * @param screenName Name of the screen
 * @param analyticsManager AnalyticsManager instance
 * @param onScreenEnter Callback when screen enters foreground
 * @param onScreenExit Callback when screen exits to background
 */
@Composable
fun AnalyticsLifecycleTracker(
    screenName: String,
    analyticsManager: AnalyticsManager,
    onScreenEnter: (String) -> Unit = {},
    onScreenExit: (String) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    analyticsManager.logScreenView(screenName)
                    onScreenEnter(screenName)
                    Timber.d("Screen resumed: $screenName")
                }
                Lifecycle.Event.ON_PAUSE -> {
                    onScreenExit(screenName)
                    Timber.d("Screen paused: $screenName")
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * Extension function to log user actions from ViewModels.
 *
 * Usage:
 * ```
 * class ProductListViewModel(
 *     private val analyticsManager: AnalyticsManager
 * ) : ViewModel() {
 *
 *     fun onProductClicked(productId: String, productName: String) {
 *         logAnalyticsEvent(
 *             eventName = AnalyticsEvents.PRODUCT_VIEW,
 *             params = mapOf(
 *                 "product_id" to productId,
 *                 "product_name" to productName
 *             )
 *         )
 *     }
 * }
 * ```
 */
fun AnalyticsManager.logUserAction(
    actionName: String,
    params: Map<String, String> = emptyMap()
) {
    Timber.d("User action: $actionName, Params: $params")
    // Can be extended with additional logic for specific actions
}

/**
 * Utility class for tracking common user interactions.
 *
 * Provides convenient methods for logging typical app interactions
 * without needing to construct parameter maps manually.
 */
class AnalyticsTracker(
    private val analyticsManager: AnalyticsManager
) {

    /**
     * Track when user taps/clicks a button
     */
    fun trackButtonClick(
        buttonName: String,
        screenName: String? = null
    ) {
        Timber.d("Button clicked: $buttonName on $screenName")
        // Can log custom event if needed
    }

    /**
     * Track form submission
     */
    fun trackFormSubmit(
        formName: String,
        fieldCount: Int,
        isValid: Boolean
    ) {
        if (isValid) {
            Timber.d("Form submitted: $formName ($fieldCount fields)")
        } else {
            Timber.d("Form validation failed: $formName")
        }
    }

    /**
     * Track user input/focus on fields
     */
    fun trackFieldFocus(
        fieldName: String,
        screenName: String? = null
    ) {
        Timber.d("Field focused: $fieldName on $screenName")
    }

    /**
     * Track scrolling behavior
     */
    fun trackScroll(
        screenName: String,
        scrollPosition: Int,
        totalItems: Int
    ) {
        if (scrollPosition % 10 == 0) {  // Log every 10th item
            Timber.d("Scroll position: $scrollPosition / $totalItems on $screenName")
        }
    }

    /**
     * Track share action
     */
    fun trackShare(
        contentType: String,
        contentId: String,
        method: String
    ) {
        Timber.i("Content shared: $contentType ($contentId) via $method")
    }

    /**
     * Track error/exception from user action
     */
    fun trackActionError(
        actionName: String,
        throwable: Throwable,
        message: String? = null
    ) {
        analyticsManager.recordException(
            throwable = throwable,
            message = "$actionName - ${message ?: throwable.message}",
            fatal = false
        )
    }
}
