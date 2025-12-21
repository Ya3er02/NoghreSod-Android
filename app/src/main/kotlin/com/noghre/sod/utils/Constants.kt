package com.noghre.sod.utils

object Constants {
    const val BASE_URL = "https://api.noghre-sod.com/"
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // Storage Keys
    const val PREF_AUTH_TOKEN = "auth_token"
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_IS_LOGGED_IN = "is_logged_in"

    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    const val INITIAL_PAGE = 1

    // Timeouts
    const val SPLASH_DELAY = 2000L

    // Error Messages
    const val NETWORK_ERROR = "Network error. Please check your connection."
    const val UNKNOWN_ERROR = "An unknown error occurred."
    const val INVALID_EMAIL = "Please enter a valid email address."
    const val INVALID_PASSWORD = "Password must be at least 6 characters."
}
