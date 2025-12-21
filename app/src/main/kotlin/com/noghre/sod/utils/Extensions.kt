package com.noghre.sod.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension functions for common operations.
 */

fun Long.formatDate(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    return try {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.format(Date(this))
    } catch (e: Exception) {
        ""
    }
}

fun Double.formatPrice(currency: String = "\$"): String {
    return "$currency%.2f".format(this)
}

fun String.isValidEmail(): Boolean {
    return this.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))
}

fun String.isValidPassword(): Boolean {
    return this.length >= 8
}

@Composable
fun getCurrentContext(): Context = LocalContext.current
