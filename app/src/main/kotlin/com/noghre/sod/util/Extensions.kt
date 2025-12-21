package com.noghre.sod.util

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

@Composable
fun ShowToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    val context = LocalContext.current
    context.showToast(message, duration)
}

fun String.isValidEmail(): Boolean {
    return this.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))
}

fun String.isValidPassword(): Boolean {
    return this.length >= 6
}

fun Double.formatPrice(): String {
    return String.format("$%.2f", this)
}

fun String.truncate(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.substring(0, maxLength) + "..."
    } else {
        this
    }
}
