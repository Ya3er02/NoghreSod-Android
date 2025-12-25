package com.noghre.sod.core.memory

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Detects and monitors memory leaks and memory usage.
 */
@Singleton
class MemoryLeakDetector @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : DefaultLifecycleObserver {

    private companion object {
        const val TAG = "MemoryLeakDetector"
        const val MEMORY_THRESHOLD_PERCENT = 80 // Warn at 80% usage
    }

    private val activityManager: ActivityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    /**
     * Get current memory info.
     */
    suspend fun getMemoryInfo(): MemoryInfo = withContext(dispatcher) {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory() / (1024 * 1024) // MB
        val totalMemory = runtime.totalMemory() / (1024 * 1024) // MB
        val freeMemory = runtime.freeMemory() / (1024 * 1024) // MB
        val usedMemory = totalMemory - freeMemory
        val usagePercent = ((usedMemory.toLong() * 100) / maxMemory).roundToInt()

        MemoryInfo(
            maxMemory = maxMemory.toInt(),
            totalMemory = totalMemory.toInt(),
            usedMemory = usedMemory.toInt(),
            freeMemory = freeMemory.toInt(),
            usagePercent = usagePercent,
            nativeHeap = getNativeHeapSize(),
            isWarning = usagePercent >= MEMORY_THRESHOLD_PERCENT
        )
    }

    /**
     * Get native heap size.
     */
    private fun getNativeHeapSize(): Int {
        return try {
            (Debug.getNativeHeap().sumOf { it.size_bytes } / (1024 * 1024)).toInt()
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Check if memory is low.
     */
    suspend fun isMemoryLow(): Boolean = withContext(dispatcher) {
        val memory = getMemoryInfo()
        memory.usagePercent >= MEMORY_THRESHOLD_PERCENT
    }

    /**
     * Force garbage collection and get freed memory.
     */
    suspend fun performGarbageCollection(): Long = withContext(dispatcher) {
        val beforeMemory = getMemoryInfo().usedMemory
        System.gc()
        System.runFinalization()
        val afterMemory = getMemoryInfo().usedMemory
        (beforeMemory - afterMemory).toLong() * 1024 * 1024
    }

    /**
     * Detect potential memory leaks by monitoring heap growth.
     */
    suspend fun detectMemoryLeaks(): List<MemoryLeakReport> = withContext(dispatcher) {
        val reports = mutableListOf<MemoryLeakReport>()
        val memoryInfo = getMemoryInfo()

        // Report if memory usage is abnormally high
        if (memoryInfo.usagePercent > 80) {
            reports.add(
                MemoryLeakReport(
                    type = LeakType.HIGH_MEMORY_USAGE,
                    description = "Memory usage at ${memoryInfo.usagePercent}%",
                    usedMemory = memoryInfo.usedMemory,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // Report if native heap is growing
        if (memoryInfo.nativeHeap > 50) { // > 50MB
            reports.add(
                MemoryLeakReport(
                    type = LeakType.LARGE_NATIVE_HEAP,
                    description = "Native heap size: ${memoryInfo.nativeHeap}MB",
                    usedMemory = memoryInfo.nativeHeap,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        reports
    }

    /**
     * Lifecycle observer for memory management.
     */
    override fun onPause(owner: LifecycleOwner) {
        // Trigger garbage collection on pause
        System.gc()
    }
}

/**
 * Memory information data class.
 */
data class MemoryInfo(
    val maxMemory: Int, // MB
    val totalMemory: Int, // MB
    val usedMemory: Int, // MB
    val freeMemory: Int, // MB
    val usagePercent: Int,
    val nativeHeap: Int, // MB
    val isWarning: Boolean
)

/**
 * Memory leak report.
 */
data class MemoryLeakReport(
    val type: LeakType,
    val description: String,
    val usedMemory: Int, // MB
    val timestamp: Long
)

/**
 * Types of potential memory leaks.
 */
enum class LeakType {
    HIGH_MEMORY_USAGE,
    LARGE_NATIVE_HEAP,
    UNCLOSED_RESOURCES,
    BITMAP_LEAK,
    LISTENER_LEAK,
    UNKNOWN
}