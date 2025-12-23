package com.noghre.sod.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Format timestamp to readable date string
 */
fun Long.formatToDate(): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        sdf.format(Date(this))
    } catch (e: Exception) {
        "Unknown Date"
    }
}

/**
 * Format date string from API
 */
fun String.formatDate(inputFormat: String = "yyyy-MM-dd'T'HH:mm:ss", outputFormat: String = "dd MMM yyyy"): String {
    return try {
        val input = SimpleDateFormat(inputFormat, Locale.getDefault())
        val output = SimpleDateFormat(outputFormat, Locale.getDefault())
        val date = input.parse(this)
        date?.let { output.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

/**
 * Get relative time (e.g., "2 hours ago")
 */
fun Long.formatRelativeTime(): String {
    val currentTime = System.currentTimeMillis()
    val diffInSeconds = (currentTime - this) / 1000

    return when {
        diffInSeconds < 60 -> "Just now"
        diffInSeconds < 3600 -> "${diffInSeconds / 60} minutes ago"
        diffInSeconds < 86400 -> "${diffInSeconds / 3600} hours ago"
        diffInSeconds < 604800 -> "${diffInSeconds / 86400} days ago"
        else -> this.formatToDate()
    }
}
