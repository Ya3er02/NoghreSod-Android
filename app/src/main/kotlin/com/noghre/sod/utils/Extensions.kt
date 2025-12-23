package com.noghre.sod.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

// Number formatting
fun Long.formatAsCurrency(): String {
    val formatter = NumberFormat.getInstance(Locale("fa", "IR"))
    return "₹${formatter.format(this)}"
}

fun Double.formatAsWeight(): String {
    return String.format("%.2f", this)
}

// String extensions
fun String.isValidEmail(): Boolean {
    return InputValidators.isValidEmail(this)
}

fun String.isValidPhone(): Boolean {
    return InputValidators.isValidPhone(this)
}

fun String.maskPhoneNumber(): String {
    if (length != 11) return this
    return "${substring(0, 3)}****${substring(7)}"
}

fun String.maskCardNumber(): String {
    if (length < 12) return this
    return "${substring(0, 4)}****${substring(length - 4)}"
}

// Time formatting
fun Long.formatAsTime(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    return String.format("%02d:%02d", hours, minutes)
}

fun Long.formatAsDate(): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@formatAsDate
    }
    return when (val daysAgo = daysBetween(System.currentTimeMillis(), this)) {
        0L -> "امروز"
        1L -> "دیروز"
        in 2..7 -> "$daysAgo روز پیش"
        else -> calendar.displayName(Calendar.MONTH, Calendar.LONG, Locale("fa", "IR")) +
                " ${calendar.get(Calendar.DAY_OF_MONTH)}"
    }
}

private fun daysBetween(start: Long, end: Long): Long {
    return (start - end) / (1000 * 60 * 60 * 24)
}

// Modifier extensions
@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    return composed {
        clickable(
            interactionSource = MutableInteractionSource(),
            indication = null,
            onClick = onClick
        )
    }
}

@Composable
fun Modifier.clickableWithRipple(onClick: () -> Unit): Modifier {
    return composed {
        clickable(
            interactionSource = MutableInteractionSource(),
            indication = rememberRipple(),
            onClick = onClick
        )
    }
}
