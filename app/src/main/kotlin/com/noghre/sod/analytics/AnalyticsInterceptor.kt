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
 * @version 1.1.0
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
    additionalParams: Map<String, String> = emptyMap(),
) {
    LaunchedEffect(screenName) {
        try {
            analyticsManager.logScreenView(
                screenName = screenName,
                screenClass = screenClass ?: screenName,
            )
            // Log additional parameters if provided
            additionalParams.forEach { (key, value) ->
                analyticsManager.setUserProperty(key, value)
            }
            Timber.d("Screen tracked: $screenName with params: $additionalParams")
        } catch (e: Exception) {
            Timber.e(e, "Error tracking screen: $screenName")
        }
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
    onScreenExit: (String) -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            try {
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
            } catch (e: Exception) {
                Timber.e(e, "Error tracking lifecycle for screen: $screenName")
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            try {
                lifecycleOwner.lifecycle.removeObserver(observer)
            } catch (e: Exception) {
                Timber.e(e, "Error removing lifecycle observer")
            }
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
 *         analyticsManager.logUserAction(
 *             actionName = AnalyticsEvents.PRODUCT_VIEW,
 *             params = mapOf(
 *                 AnalyticsEvents.Params.PRODUCT_ID to productId,
 *                 AnalyticsEvents.Params.PRODUCT_NAME to productName
 *             )
 *         )
 *     }
 * }
 * ```
 */
fun AnalyticsManager.logUserAction(
    actionName: String,
    params: Map<String, String> = emptyMap(),
) {
    try {
        Timber.d("User action: $actionName, Params: $params")
        // Log user properties from params if needed
        params.forEach { (key, value) ->
            this.setUserProperty(key, value)
        }
    } catch (e: Exception) {
        Timber.e(e, "Error logging user action: $actionName")
    }
}

/**
 * Utility class for tracking common user interactions.
 *
 * Provides convenient methods for logging typical app interactions
 * without needing to construct parameter maps manually.
 *
 * Thread-safe implementation using injected AnalyticsManager.
 */
class AnalyticsTracker(
    private val analyticsManager: AnalyticsManager,
) {

    /**
     * Track when user taps/clicks a button
     */
    fun trackButtonClick(
        buttonName: String,
        screenName: String? = null,
    ) {
        try {
            Timber.d("Button clicked: $buttonName on $screenName")
            analyticsManager.logUserAction(
                actionName = "button_click",
                params = mapOfNotNull(
                    "button_name" to buttonName,
                    "screen_name" to screenName,
                ).toMap(),
            )
        } catch (e: Exception) {
            Timber.e(e, "Error tracking button click: $buttonName")
        }
    }

    /**
     * Track form submission
     */
    fun trackFormSubmit(
        formName: String,
        fieldCount: Int,
        isValid: Boolean,
    ) {
        try {
            if (isValid) {
                Timber.d("Form submitted: $formName ($fieldCount fields)")
                analyticsManager.logUserAction(
                    actionName = "form_submit",
                    params = mapOf(
                        "form_name" to formName,
                        "field_count" to fieldCount.toString(),
                    ),
                )
            } else {
                Timber.d("Form validation failed: $formName")
                analyticsManager.logUserAction(
                    actionName = "form_validation_failed",
                    params = mapOf(
                        "form_name" to formName,
                        "field_count" to fieldCount.toString(),
                    ),
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Error tracking form submit: $formName")
        }
    }

    /**
     * Track user input/focus on fields
     */
    fun trackFieldFocus(
        fieldName: String,
        screenName: String? = null,
    ) {
        try {
            Timber.d("Field focused: $fieldName on $screenName")
            analyticsManager.logUserAction(
                actionName = "field_focus",
                params = mapOfNotNull(
                    "field_name" to fieldName,
                    "screen_name" to screenName,
                ).toMap(),
            )
        } catch (e: Exception) {
            Timber.e(e, "Error tracking field focus: $fieldName")
        }
    }

    /**
     * Track scrolling behavior
     */
    fun trackScroll(
        screenName: String,
        scrollPosition: Int,
        totalItems: Int,
    ) {
        try {
            if (scrollPosition % 10 == 0) {  // Log every 10th item
                Timber.d("Scroll position: $scrollPosition / $totalItems on $screenName")
                analyticsManager.setUserProperty(
                    propertyName = "scroll_position_$screenName",
                    value = scrollPosition.toString(),
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Error tracking scroll on: $screenName")
        }
    }

    /**
     * Track share action
     */
    fun trackShare(
        contentType: String,
        contentId: String,
        method: String,
    ) {
        try {
            Timber.i("Content shared: $contentType ($contentId) via $method")
            analyticsManager.logUserAction(
                actionName = "content_shared",
                params = mapOf(
                    "content_type" to contentType,
                    "content_id" to contentId,
                    "share_method" to method,
                ),
            )
        } catch (e: Exception) {
            Timber.e(e, "Error tracking share: $contentType")
        }
    }

    /**
     * Track error/exception from user action
     */
    fun trackActionError(
        actionName: String,
        throwable: Throwable,
        message: String? = null,
    ) {
        try {
            val errorMessage = "$actionName - ${message ?: throwable.message}"
            analyticsManager.recordException(
                throwable = throwable,
                message = errorMessage,
                fatal = false,
            )
        } catch (e: Exception) {
            Timber.e(e, "Error tracking action error: $actionName")
        }
    }
}

/**
 * Helper function to build non-null map from nullable parameters
 */
private fun mapOfNotNull(vararg pairs: Pair<String, String?>): Map<String, String> {
    return pairs.filter { it.second != null }
        .associate { it.first to it.second!! }
}
