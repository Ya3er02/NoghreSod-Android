package com.noghre.sod.data.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.noghre.sod.data.model.OrderItemEntity

/**
 * Room Database Type Converters
 * Converts complex types to/from SQLite-compatible formats
 */
class Converters {
    private val gson = Gson()

    // ==================== List<String> Converters ====================

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return if (value == null) {
            null
        } else {
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson(value, listType)
        }
    }

    // ==================== List<OrderItemEntity> Converters ====================

    @TypeConverter
    fun fromOrderItemList(value: List<OrderItemEntity>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toOrderItemList(value: String?): List<OrderItemEntity>? {
        return if (value == null) {
            null
        } else {
            val listType = object : TypeToken<List<OrderItemEntity>>() {}.type
            gson.fromJson(value, listType)
        }
    }

    // ==================== Map<String, Any> Converters ====================

    @TypeConverter
    fun fromMap(value: Map<String, Any>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMap(value: String?): Map<String, Any>? {
        return if (value == null) {
            null
        } else {
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(value, mapType)
        }
    }

    // ==================== Long Timestamp Converters ====================

    @TypeConverter
    fun fromTimestamp(value: Long?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toTimestamp(value: String?): Long? {
        return value?.toLongOrNull()
    }

    // ==================== Boolean Converters ====================

    @TypeConverter
    fun fromBoolean(value: Boolean?): Int? {
        return if (value == null) null else if (value) 1 else 0
    }

    @TypeConverter
    fun toBoolean(value: Int?): Boolean? {
        return when (value) {
            1 -> true
            0 -> false
            else -> null
        }
    }
}
