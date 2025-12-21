package com.noghre.sod.utils

import android.os.Debug
import timber.log.Timber
import kotlin.system.measureTimeMillis

/**
 * Performance monitoring utility for tracking execution times and memory usage.
 */
object PerformanceMonitor {

    /**
     * Measure execution time of a function.
     */
    fun <T> measureTime(
        label: String,
        block: () -> T
    ): T {
        val timeMs = measureTimeMillis {
            block()
        }
        Timber.d("[$label] Execution time: ${timeMs}ms")
        return block()
    }

    /**
     * Measure async operation timing.
     */
    suspend fun <T> measureSuspendTime(
        label: String,
        block: suspend () -> T
    ): T {
        val startTime = System.currentTimeMillis()
        return try {
            block().also {
                val elapsedTime = System.currentTimeMillis() - startTime
                Timber.d("[$label] Async execution time: ${elapsedTime}ms")
            }
        } catch (e: Exception) {
            val elapsedTime = System.currentTimeMillis() - startTime
            Timber.e(e, "[$label] Failed after ${elapsedTime}ms")
            throw e
        }
    }

    /**
     * Get current memory usage.
     */
    fun getMemoryInfo(): MemoryInfo {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory() / (1024 * 1024) // MB
        val freeMemory = runtime.freeMemory() / (1024 * 1024) // MB
        val usedMemory = totalMemory - freeMemory
        val maxMemory = runtime.maxMemory() / (1024 * 1024) // MB

        return MemoryInfo(
            totalMemory = totalMemory,
            freeMemory = freeMemory,
            usedMemory = usedMemory,
            maxMemory = maxMemory,
            memoryPercentage = (usedMemory.toFloat() / maxMemory.toFloat() * 100).toInt()
        )
    }

    /**
     * Log memory info if memory usage is high.
     */
    fun logMemoryIfHigh(threshold: Int = 80) {
        val memInfo = getMemoryInfo()
        if (memInfo.memoryPercentage > threshold) {
            Timber.w("High memory usage: ${memInfo.memoryPercentage}% (${memInfo.usedMemory}/${memInfo.maxMemory} MB)")
        }
    }

    /**
     * Get native heap size.
     */
    fun getNativeHeapSize(): Long {
        return Debug.getNativeHeap().sumOf { it.size }
    }

    data class MemoryInfo(
        val totalMemory: Long,
        val freeMemory: Long,
        val usedMemory: Long,
        val maxMemory: Long,
        val memoryPercentage: Int
    )
}
