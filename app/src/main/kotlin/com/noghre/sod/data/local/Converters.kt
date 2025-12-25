package com.noghre.sod.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Type converters for Room Database.
 * Converts complex types to/from strings for storage.
 */
class Converters {

    private val gson = Gson()
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // ===== String List Converters =====

    /**
     * Convert List<String> to String for storage.
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return if (value == null) null else gson.toJson(value)
    }

    /**
     * Convert String back to List<String>.
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return if (value == null) null else {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(value, type)
        }
    }

    // ===== Map Converters =====

    /**
     * Convert Map<String, String> to String for storage.
     */
    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        return if (value == null) null else gson.toJson(value)
    }

    /**
     * Convert String back to Map<String, String>.
     */
    @TypeConverter
    fun toStringMap(value: String?): Map<String, String>? {
        return if (value == null) null else {
            val type = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson(value, type)
        }
    }

    // ===== LocalDate Converters =====

    /**
     * Convert LocalDate to String for storage.
     */
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.format(dateFormatter)
    }

    /**
     * Convert String back to LocalDate.
     */
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return if (value == null) null else {
            try {
                LocalDate.parse(value, dateFormatter)
            } catch (e: Exception) {
                null
            }
        }
    }

    // ===== LocalDateTime Converters =====

    /**
     * Convert LocalDateTime to String for storage.
     */
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(dateTimeFormatter)
    }

    /**
     * Convert String back to LocalDateTime.
     */
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return if (value == null) null else {
            try {
                LocalDateTime.parse(value, dateTimeFormatter)
            } catch (e: Exception) {
                null
            }
        }
    }

    // ===== Boolean List Converters =====

    /**
     * Convert List<Boolean> to String for storage.
     */
    @TypeConverter
    fun fromBooleanList(value: List<Boolean>?): String? {
        return if (value == null) null else gson.toJson(value)
    }

    /**
     * Convert String back to List<Boolean>.
     */
    @TypeConverter
    fun toBooleanList(value: String?): List<Boolean>? {
        return if (value == null) null else {
            val type = object : TypeToken<List<Boolean>>() {}.type
            gson.fromJson(value, type)
        }
    }

    // ===== Integer List Converters =====

    /**
     * Convert List<Int> to String for storage.
     */
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return if (value == null) null else gson.toJson(value)
    }

    /**
     * Convert String back to List<Int>.
     */
    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return if (value == null) null else {
            val type = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(value, type)
        }
    }
}