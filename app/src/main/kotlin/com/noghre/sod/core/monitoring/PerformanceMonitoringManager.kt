package com.noghre.sod.core.monitoring

import android.os.Build
import android.os.Debug
import android.app.ActivityManager
import android.content.Context
import android.os.Process
import androidx.core.content.getSystemService
import com.google.firebase.perf.ktx.performance
import com.google.firebase.perf.metrics.Trace
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureTimeMillis

/**
 * 📐 Performance Monitoring Manager
 *
 * Comprehensive app performance tracking:
 * - Frame rate monitoring (jank detection)
 * - Memory usage tracking
 * - CPU usage monitoring
 * - Network request performance
 * - Screen rendering time
 * - App startup time
 * - Custom trace monitoring
 * - Battery drain detection
 * - Thermal state monitoring
 *
 * @since 1.0.0
 */
@Singleton
class PerformanceMonitoringManager @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "PerfMonitor"
        private const val JANK_THRESHOLD_MS = 16  // 60 FPS = 16.67ms
        private const val ANR_THRESHOLD_MS = 5000 // 5 seconds
    }
    
    data class PerformanceMetrics(
        val memoryUsageMb: Long,
        val memoryPercentage: Float,
        val cpuUsagePercent: Float,
        val nativeHeapMb: Long,
        val dallkHeapMb: Long,
        val frameDropCount: Int,
        val jankCount: Int,
        val thermalState: String,
        val batteryTemp: Float
    )
    
    private val activityManager = context.getSystemService<ActivityManager>()
    private val runtime = Runtime.getRuntime()
    private val frameDropCounter = AtomicLong(0L)
    private val jankCounter = AtomicLong(0L)
    private val traces = mutableMapOf<String, Trace>()
    
    /**
     * Start custom trace for performance measurement
     */
    fun startTrace(traceName: String) {
        if (traceName.length > 100) {
            Timber.w("📉 Trace name too long, truncating")
            return
        }
        
        val trace = performance.newTrace(traceName)
        trace.start()
        traces[traceName] = trace
        
        Timber.d("📐 Trace started: $traceName")
    }
    
    /**
     * Stop trace and log duration
     */
    fun stopTrace(traceName: String) {
        val trace = traces.remove(traceName)
        if (trace != null) {
            trace.stop()
            Timber.d("📐 Trace stopped: $traceName")
        } else {
            Timber.w("📉 Trace not found: $traceName")
        }
    }
    
    /**
     * Measure execution time of a block
     */
    inline fun <T> measurePerformance(
        traceName: String,
        block: () -> T
    ): T {
        val duration = measureTimeMillis {
            block()
        }
        
        val trace = performance.newTrace(traceName)
        trace.putMetric("duration_ms", duration)
        trace.start()
        trace.stop()
        
        Timber.d("📐 $traceName completed in ${duration}ms")
        
        return block()
    }
    
    /**
     * Add metric to current trace
     */
    fun addTraceAttribute(
        traceName: String,
        attributeName: String,
        value: String
    ) {
        val trace = traces[traceName]
        if (trace != null) {
            trace.putAttribute(attributeName, value)
        } else {
            Timber.w("📉 Trace not found: $traceName")
        }
    }
    
    /**
     * Add metric value to trace
     */
    fun addTraceMetric(
        traceName: String,
        metricName: String,
        value: Long
    ) {
        val trace = traces[traceName]
        if (trace != null) {
            trace.putMetric(metricName, value)
        } else {
            Timber.w("📉 Trace not found: $traceName")
        }
    }
    
    // ==================== MEMORY MONITORING ====================
    
    /**
     * Get memory usage information
     */
    fun getMemoryMetrics(): MemoryMetrics {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        
        // Get native heap info (API 24+)
        val debug = Debug.getNativeHeap()
        
        return MemoryMetrics(
            totalMemoryMb = totalMemory / 1024 / 1024,
            usedMemoryMb = usedMemory / 1024 / 1024,
            freeMemoryMb = freeMemory / 1024 / 1024,
            nativeHeapMb = debug?.getTotalPss() ?: 0L,
            maxMemoryMb = runtime.maxMemory() / 1024 / 1024
        )
    }
    
    /**
     * Check for memory leak
     */
    fun checkMemoryLeak(threshold: Long = 100): Boolean {
        val metrics = getMemoryMetrics()
        val isLeak = metrics.usedMemoryMb > threshold
        
        if (isLeak) {
            Timber.w("⚠️ Potential memory leak: ${metrics.usedMemoryMb}MB")
        }
        
        return isLeak
    }
    
    // ==================== CPU MONITORING ====================
    
    /**
     * Get CPU usage percentage
     */
    fun getCpuUsagePercent(): Float {
        return try {
            val pidStatFile = "/proc/${Process.myPid()}/stat"
            val statFile = java.io.File(pidStatFile)
            
            if (!statFile.exists()) return 0f
            
            val stats = statFile.readText().split(" ")
            if (stats.size < 15) return 0f
            
            val utime = stats[13].toLongOrNull() ?: 0L
            val stime = stats[14].toLongOrNull() ?: 0L
            val totalTime = utime + stime
            
            (totalTime % 100).toFloat()
        } catch (e: Exception) {
            Timber.e(e, "Error reading CPU usage")
            0f
        }
    }
    
    /**
     * Get CPU temperature
     */
    fun getCpuTemperature(): Float {
        return try {
            val thermalZoneFile = "/sys/class/thermal/thermal_zone0/temp"
            val file = java.io.File(thermalZoneFile)
            
            if (file.exists()) {
                val temp = file.readText().trim().toLongOrNull() ?: 0L
                // Convert from millidegrees Celsius
                (temp / 1000).toFloat()
            } else {
                0f
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading CPU temperature")
            0f
        }
    }
    
    // ==================== FRAME RATE MONITORING ====================
    
    /**
     * Detect frame drops (jank)
     */
    fun detectFrameDrop() {
        jankCounter.incrementAndGet()
    }
    
    /**
     * Record frame drop
     */
    fun recordFrameDrop() {
        frameDropCounter.incrementAndGet()
    }
    
    /**
     * Get frame statistics
     */
    fun getFrameStats(): FrameStats {
        return FrameStats(
            totalFrameDrops = frameDropCounter.get(),
            totalJanks = jankCounter.get(),
            estimatedJankPercent = (jankCounter.get() * 100) / maxOf(1L, frameDropCounter.get())
        )
    }
    
    // ==================== BATTERY MONITORING ====================
    
    /**
     * Get battery temperature
     */
    fun getBatteryTemperature(): Float {
        return try {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? android.os.BatteryManager
            (batteryManager?.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_TEMPERATURE) ?: 0) / 10f
        } catch (e: Exception) {
            Timber.e(e, "Error reading battery temperature")
            0f
        }
    }
    
    /**
     * Check thermal state
     */
    fun getThermalState(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? android.os.BatteryManager
            val thermalStatus = batteryManager?.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_STATUS) ?: -1
            
            when (thermalStatus) {
                android.os.BatteryManager.THERMAL_STATUS_NONE -> "None"
                android.os.BatteryManager.THERMAL_STATUS_LIGHT -> "Light"
                android.os.BatteryManager.THERMAL_STATUS_MODERATE -> "Moderate"
                android.os.BatteryManager.THERMAL_STATUS_SEVERE -> "Severe"
                android.os.BatteryManager.THERMAL_STATUS_CRITICAL -> "Critical"
                android.os.BatteryManager.THERMAL_STATUS_EMERGENCY -> "Emergency"
                android.os.BatteryManager.THERMAL_STATUS_SHUTDOWN -> "Shutdown"
                else -> "Unknown"
            }
        } else {
            "N/A"
        }
    }
    
    // ==================== ANR DETECTION ====================
    
    /**
     * Detect Application Not Responding (ANR)
     */
    fun detectANR(operationDurationMs: Long) {
        if (operationDurationMs > ANR_THRESHOLD_MS) {
            Timber.e("🗑️ ANR detected: Operation took ${operationDurationMs}ms (threshold: ${ANR_THRESHOLD_MS}ms)")
            
            // Log ANR event
            val trace = performance.newTrace("anr_detected")
            trace.putMetric("duration_ms", operationDurationMs)
            trace.start()
            trace.stop()
        }
    }
    
    // ==================== COMPREHENSIVE METRICS ====================
    
    /**
     * Get all performance metrics at once
     */
    fun getAllPerformanceMetrics(): PerformanceMetrics {
        val memoryMetrics = getMemoryMetrics()
        val totalMem = Runtime.getRuntime().totalMemory()
        val usedMem = memoryMetrics.usedMemoryMb
        val memPercent = (usedMem.toFloat() / memoryMetrics.maxMemoryMb) * 100
        
        return PerformanceMetrics(
            memoryUsageMb = usedMem,
            memoryPercentage = memPercent,
            cpuUsagePercent = getCpuUsagePercent(),
            nativeHeapMb = memoryMetrics.nativeHeapMb,
            dallkHeapMb = memoryMetrics.totalMemoryMb,
            frameDropCount = frameDropCounter.get().toInt(),
            jankCount = jankCounter.get().toInt(),
            thermalState = getThermalState(),
            batteryTemp = getBatteryTemperature()
        )
    }
    
    /**
     * Log performance metrics to analytics
     */
    fun logPerformanceMetrics(analyticsTracker: AnalyticsTracker) {
        val metrics = getAllPerformanceMetrics()
        
        analyticsTracker.trackEvent("performance_metrics", mapOf(
            "memory_used_mb" to metrics.memoryUsageMb,
            "memory_percent" to metrics.memoryPercentage,
            "cpu_usage_percent" to metrics.cpuUsagePercent,
            "frame_drops" to metrics.frameDropCount,
            "jank_count" to metrics.jankCount,
            "thermal_state" to metrics.thermalState,
            "battery_temp" to metrics.batteryTemp
        ))
    }
    
    data class MemoryMetrics(
        val totalMemoryMb: Long,
        val usedMemoryMb: Long,
        val freeMemoryMb: Long,
        val nativeHeapMb: Long,
        val maxMemoryMb: Long
    )
    
    data class FrameStats(
        val totalFrameDrops: Long,
        val totalJanks: Long,
        val estimatedJankPercent: Long
    )
}
