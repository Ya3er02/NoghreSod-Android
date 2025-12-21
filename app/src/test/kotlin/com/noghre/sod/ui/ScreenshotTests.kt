package com.noghre.sod.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.noghre.sod.domain.common.NetworkException
import com.noghre.sod.ui.components.error.EmptyView
import com.noghre.sod.ui.components.error.ErrorView
import com.noghre.sod.ui.components.loading.ProductCardSkeleton
import org.junit.Rule
import org.junit.Test

/**
 * Screenshot tests for UI components.
 * Validates visual appearance across different device configurations.
 *
 * Run with:
 * ./gradlew verifyPaparazziDebug
 */
class ScreenshotTests {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        showSystemUi = false
    )

    @Test
    fun errorView_noInternet() {
        paparazzi.snapshot {
            ErrorView(
                error = NetworkException.NoInternetException(),
                onRetry = {}
            )
        }
    }

    @Test
    fun errorView_serverError() {
        paparazzi.snapshot {
            ErrorView(
                error = NetworkException.ServerException(500, "Internal Server Error"),
                onRetry = {}
            )
        }
    }

    @Test
    fun errorView_unauthorized() {
        paparazzi.snapshot {
            ErrorView(
                error = NetworkException.UnauthorizedException(),
                onRetry = {}
            )
        }
    }

    @Test
    fun errorView_notFound() {
        paparazzi.snapshot {
            ErrorView(
                error = NetworkException.NotFoundException(),
                onRetry = {}
            )
        }
    }

    @Test
    fun errorView_timeout() {
        paparazzi.snapshot {
            ErrorView(
                error = NetworkException.TimeoutException(),
                onRetry = {}
            )
        }
    }

    @Test
    fun emptyView_noItems() {
        paparazzi.snapshot {
            EmptyView(
                title = "سبد خرید خالی است",
                message = "هنوز محصولی به سبد خرید اضافه نکرده‌اید",
                icon = Icons.Default.ShoppingCart,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    @Test
    fun emptyView_noSearchResults() {
        paparazzi.snapshot {
            EmptyView(
                title = "نتیجه‌ای یافت نشد",
                message = "برای جستجو خود محصولی وجود ندارد"
            )
        }
    }

    @Test
    fun productCardSkeleton() {
        paparazzi.snapshot {
            ProductCardSkeleton()
        }
    }
}
