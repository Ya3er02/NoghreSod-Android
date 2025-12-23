package com.noghre.sod.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type converters for Room Database.
 * Converts complex types to/from database storage format.
 */
object Converters {

    private val gson = Gson()

    /**
     * Convert List<String> to JSON string.
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    /**
     * Convert JSON string to List<String>.
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }
    }

    /**
     * Convert Map<String, String> to JSON string.
     */
    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    /**
     * Convert JSON string to Map<String, String>.
     */
    @TypeConverter
    fun toStringMap(value: String?): Map<String, String>? {
        return value?.let {
            val type = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson(it, type)
        }
    }
}
