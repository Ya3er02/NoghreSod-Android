package com.noghre.sod.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Room type converters for complex data types.
 * Converts between database types and Kotlin types.
 */
class DatabaseConverters {

    private val gson = Gson()

    // ============== String List Converters ==============

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            gson.fromJson(it, object : TypeToken<List<String>>() {}.type)
        }
    }

    // ============== Map Converters ==============

    @TypeConverter
    fun fromStringMap(value: Map<String, Any>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringMap(value: String?): Map<String, Any>? {
        return value?.let {
            gson.fromJson(it, object : TypeToken<Map<String, Any>>() {}.type)
        }
    }

    // ============== Date/Time Converters ==============

    @TypeConverter
    fun fromTimestamp(value: Long?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun dateToTimestamp(value: String?): Long? {
        return value?.toLongOrNull()
    }

    // ============== Double Converters ==============

    @TypeConverter
    fun fromDouble(value: Double?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toDouble(value: String?): Double? {
        return value?.toDoubleOrNull()
    }

    // ============== Boolean Converters ==============

    @TypeConverter
    fun fromBoolean(value: Boolean?): Int? {
        return value?.let { if (it) 1 else 0 }
    }

    @TypeConverter
    fun toBoolean(value: Int?): Boolean? {
        return value?.let { it != 0 }
    }
}
