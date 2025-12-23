package com.noghre.sod.data.local

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converters for Room database.
 * Handles conversion of complex types to/from Room-compatible types.
 */
class Converters {

    /**
     * Converts a comma-separated string of image URLs to a List.
     * Used for ProductEntity.images field.
     */
    @TypeConverter
    fun fromImageStringToList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(",").map { it.trim() }
    }

    /**
     * Converts a List of image URLs to a comma-separated string.
     * Used for ProductEntity.images field.
     */
    @TypeConverter
    fun fromImageListToString(list: List<String>?): String {
        if (list.isNullOrEmpty()) return ""
        return list.joinToString(",")
    }

    /**
     * Converts a Long timestamp to a Date object.
     */
    @TypeConverter
    fun fromTimestampToDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    /**
     * Converts a Date object to a Long timestamp.
     */
    @TypeConverter
    fun fromDateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    /**
     * Converts a comma-separated string to a List of Strings.
     * Generic utility for any list field.
     */
    @TypeConverter
    fun fromStringToStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(",").map { it.trim() }
    }

    /**
     * Converts a List of Strings to a comma-separated string.
     * Generic utility for any list field.
     */
    @TypeConverter
    fun fromStringListToString(list: List<String>?): String {
        if (list.isNullOrEmpty()) return ""
        return list.joinToString(",")
    }
}
