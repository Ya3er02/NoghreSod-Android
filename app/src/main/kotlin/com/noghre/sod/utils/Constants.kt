package com.noghre.sod.utils

object Constants {
    // API Configuration
    const val API_BASE_URL = "https://api.noghre.sod/"
    const val API_TIMEOUT_SECONDS = 30L

    // Database
    const val DATABASE_NAME = "noghre_sod_database"

    // Preferences
    const val PREFERENCE_NAME = "noghre_sod_prefs"
    const val AUTH_TOKEN_KEY = "auth_token"
    const val USER_ID_KEY = "user_id"
    const val LAST_SYNC_KEY = "last_sync"

    // Pagination
    const val PAGE_SIZE = 20
    const val INITIAL_PAGE = 1

    // UI
    const val ANIMATION_DURATION_MS = 300
    const val DEBOUNCE_DURATION_MS = 500

    // Error Messages
    const val GENERIC_ERROR = "Something went wrong. Please try again."
    const val NETWORK_ERROR = "Network error. Please check your connection."
    const val TIMEOUT_ERROR = "Request timed out. Please try again."
}
