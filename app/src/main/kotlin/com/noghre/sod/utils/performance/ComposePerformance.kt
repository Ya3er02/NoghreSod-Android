package com.noghre.sod.utils.performance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import timber.log.Timber

/**
 * Logs recomposition count for debugging purposes.
 * Use only during development.
 *
 * Example:
 * ```
 * @Composable
 * fun MyScreen() {
 *     LogCompositions("MyScreen")
 *     // Screen content
 * }
 * ```
 */
@Composable
fun LogCompositions(tag: String) {
    val ref = remember { Ref(0) }
    SideEffect {
        ref.value++
        Timber.tag("Recomposition").d("$tag recomposed ${ref.value} times")
    }
}

private class Ref(var value: Int)

/**
 * Measures and logs the time taken to compute a value.
 * Use for identifying slow computations.
 */
@Composable
fun <T> rememberMeasured(
    key: String,
    calculation: @DisallowComposableCalls () -> T
): T {
    val startTime = System.nanoTime()
    val result = remember(key) { calculation() }
    val duration = (System.nanoTime() - startTime) / 1_000_000.0
    Timber.tag("Performance").d("$key took $duration ms")
    return result
}
