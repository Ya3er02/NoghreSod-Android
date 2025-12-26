package com.noghre.sod.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber

/**
 * Type converters for Room database.
 * Converts complex types to primitives for database storage.
 * Uses Gson for JSON serialization.
 *
 * Conversions:
 * - List<String> <-> JSON string
 * - List<Int> <-> JSON string
 * - Long <-> timestamp
 *
 * @author Yaser
 * @version 1.0.0
 */
class DatabaseConverters {
    private val gson = Gson()

    // ============== STRING LIST CONVERTERS ==============

    /**
     * Convert List<String> to JSON string for database storage.
     *
     * @param value List of strings
     * @return JSON string or null
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let {
            try {
                gson.toJson(it)
            } catch (e: Exception) {
                Timber.e(e, "Failed to convert string list to JSON")
                null
            }
        }
    }

    /**
     * Convert JSON string to List<String> from database.
     *
     * @param value JSON string
     * @return List of strings or empty list
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            try {
                val type = object : TypeToken<List<String>>() {}.type
                gson.fromJson(it, type)
            } catch (e: Exception) {
                Timber.e(e, "Failed to convert JSON to string list")
                emptyList()
            }
        }
    }

    // ============== INTEGER LIST CONVERTERS ==============

    /**
     * Convert List<Int> to JSON string for database storage.
     *
     * @param value List of integers
     * @return JSON string or null
     */
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.let {
            try {
                gson.toJson(it)
            } catch (e: Exception) {
                Timber.e(e, "Failed to convert int list to JSON")
                null
            }
        }
    }

    /**
     * Convert JSON string to List<Int> from database.
     *
     * @param value JSON string
     * @return List of integers or empty list
     */
    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.let {
            try {
                val type = object : TypeToken<List<Int>>() {}.type
                gson.fromJson(it, type)
            } catch (e: Exception) {
                Timber.e(e, "Failed to convert JSON to int list")
                emptyList()
            }
        }
    }

    // ============== TIMESTAMP CONVERTERS ==============

    /**
     * Convert Long timestamp to Long (identity conversion).
     * Room requires explicit converters for all types.
     *
     * @param value Long timestamp
     * @return Same timestamp
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Long? {
        return value
    }

    /**
     * Convert Long to Long timestamp (identity conversion).
     * Room requires explicit converters for all types.
     *
     * @param value Long value
     * @return Same value
     */
    @TypeConverter
    fun toTimestamp(value: Long?): Long? {
        return value
    }

    // ============== DOUBLE LIST CONVERTERS ==============

    /**
     * Convert List<Double> to JSON string for database storage.
     *
     * @param value List of doubles
     * @return JSON string or null
     */
    @TypeConverter
    fun fromDoubleList(value: List<Double>?): String? {
        return value?.let {
            try {
                gson.toJson(it)
            } catch (e: Exception) {
                Timber.e(e, "Failed to convert double list to JSON")
                null
            }
        }
    }

    /**
     * Convert JSON string to List<Double> from database.
     *
     * @param value JSON string
     * @return List of doubles or empty list
     */
    @TypeConverter
    fun toDoubleList(value: String?): List<Double>? {
        return value?.let {
            try {
                val type = object : TypeToken<List<Double>>() {}.type
                gson.fromJson(it, type)
            } catch (e: Exception) {
                Timber.e(e, "Failed to convert JSON to double list")
                emptyList()
            }
        }
    }
}
