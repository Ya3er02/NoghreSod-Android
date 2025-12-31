package com.noghre.sod.core.logger

import android.content.Context
import android.os.Environment
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Timber Tree implementation that persists logs to device storage.
 *
 * Features:
 * - Logs to external cache directory
 * - Daily log rotation
 * - Maximum file size management
 * - JSON-formatted log entries
 *
 * Usage:
 * ```
 * Timber.plant(FileLoggingTree(context))
 * ```
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
class FileLoggingTree(
    context: Context
) : Timber.Tree() {

    private val logsDir = File(context.externalCacheDir, "logs").apply {
        if (!exists()) mkdirs()
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val maxFileSize = 5 * 1024 * 1024  // 5 MB

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        try {
            val currentDate = fileNameFormat.format(Date())
            val logFile = File(logsDir, "log-$currentDate.txt")

            // Check file size and rotate if needed
            if (logFile.exists() && logFile.length() > maxFileSize) {
                val timestamp = System.currentTimeMillis()
                val archivedFile = File(logsDir, "log-$currentDate-$timestamp.txt")
                logFile.renameTo(archivedFile)
            }

            // Format log entry
            val timestamp = dateFormat.format(Date())
            val priorityStr = getPriorityString(priority)
            val logTag = tag ?: "NO-TAG"
            val logEntry = "[$timestamp] [$priorityStr] [$logTag] $message\n"

            // Write to file
            FileWriter(logFile, true).use { writer ->
                writer.append(logEntry)
                if (t != null) {
                    writer.append("EXCEPTION: ${t.message}\n")
                    writer.append(getStackTraceString(t))
                    writer.append("\n---\n")
                }
            }
        } catch (e: Exception) {
            // Prevent logging errors from breaking the app
            Timber.w(e, "Error writing to log file")
        }
    }

    /**
     * Convert priority int to string.
     */
    private fun getPriorityString(priority: Int): String = when (priority) {
        android.util.Log.VERBOSE -> "V"
        android.util.Log.DEBUG -> "D"
        android.util.Log.INFO -> "I"
        android.util.Log.WARN -> "W"
        android.util.Log.ERROR -> "E"
        android.util.Log.ASSERT -> "A"
        else -> "?"
    }

    /**
     * Get formatted stack trace string.
     */
    private fun getStackTraceString(t: Throwable): String {
        return t.stackTraceToString()
    }
}
