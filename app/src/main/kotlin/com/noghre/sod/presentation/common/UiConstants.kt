package com.noghre.sod.presentation.common

/**
 * UI constants for consistency across the app
 */
object UiConstants {
    const val DEBOUNCE_DELAY_MS = 300L
    const val ANIMATION_DURATION_MS = 300
    const val SHIMMER_ANIMATION_DURATION_MS = 1500L
    const val REFRESH_TIMEOUT_MS = 30000L

    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    const val INITIAL_PAGE = 1

    // Touch
    const val MIN_TOUCH_TARGET_SIZE_DP = 48

    // OTP
    const val OTP_LENGTH = 6
    const val OTP_TIMEOUT_SECONDS = 120
    const val OTP_RESEND_DELAY_SECONDS = 30
}
