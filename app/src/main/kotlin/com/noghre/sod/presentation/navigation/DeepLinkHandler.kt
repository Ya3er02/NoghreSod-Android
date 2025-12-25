package com.noghre.sod.presentation.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import timber.log.Timber

/**
 * Handles deep linking for the application.
 */
class DeepLinkHandler(private val context: Context) {

    /**
     * Parse and navigate to deep link.
     */
    fun handleDeepLink(intent: Intent?, navController: NavController) {
        val deepLink = intent?.data
        if (deepLink != null) {
            Timber.d("Deep link received: $deepLink")
            navigateByDeepLink(deepLink, navController)
        }
    }

    /**
     * Navigate based on deep link URI.
     */
    private fun navigateByDeepLink(deepLink: Uri, navController: NavController) {
        when (deepLink.host) {
            "noghresod.com" -> {
                when (deepLink.pathSegments.firstOrNull()) {
                    "product" -> {
                        val productId = deepLink.getQueryParameter("id")
                        if (productId != null) {
                            Timber.d("Navigating to product detail: $productId")
                            navController.navigate(
                                "product_detail/$productId"
                            )
                        }
                    }
                    "order" -> {
                        val orderId = deepLink.getQueryParameter("id")
                        if (orderId != null) {
                            Timber.d("Navigating to order detail: $orderId")
                            navController.navigate(
                                "order_detail/$orderId"
                            )
                        }
                    }
                    "category" -> {
                        val categoryId = deepLink.getQueryParameter("id")
                        if (categoryId != null) {
                            Timber.d("Navigating to category: $categoryId")
                            navController.navigate(
                                "category/$categoryId"
                            )
                        }
                    }
                    "search" -> {
                        val query = deepLink.getQueryParameter("q")
                        if (query != null) {
                            Timber.d("Navigating to search: $query")
                            navController.navigate(
                                "search/$query"
                            )
                        }
                    }
                    else -> {
                        Timber.w("Unknown deep link path: ${deepLink.path}")
                    }
                }
            }
            else -> {
                Timber.w("Unknown deep link host: ${deepLink.host}")
            }
        }
    }
}

/**
 * Deep link URIs for the application.
 * Use these for testing and documentation.
 */
object DeepLinks {
    // Product deep links
    const val PRODUCT_DETAIL = "noghresod://noghresod.com/product?id={id}"
    
    // Order deep links
    const val ORDER_DETAIL = "noghresod://noghresod.com/order?id={id}"
    
    // Category deep links
    const val CATEGORY = "noghresod://noghresod.com/category?id={id}"
    
    // Search deep links
    const val SEARCH = "noghresod://noghresod.com/search?q={query}"
    
    // Home deep link
    const val HOME = "noghresod://noghresod.com/home"
}
