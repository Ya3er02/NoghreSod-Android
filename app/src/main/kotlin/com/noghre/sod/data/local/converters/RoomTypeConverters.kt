package com.noghre.sod.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Room TypeConverters for handling complex types.
 * 
 * Provides conversion between Room-supported types and domain types:
 * - BigDecimal ↔ String (for accurate financial calculations)
 * - List<String> ↔ JSON (for storing lists in database)
 * - LocalDateTime ↔ String (for timestamp storage)
 * 
 * @since 1.0.0
 */
class RoomTypeConverters {
    
    private val gson = Gson()
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    // ============== BigDecimal Converters ==============
    
    /**
     * Convert BigDecimal to String for storage.
     * Uses string representation to preserve precision.
     * 
     * @param value BigDecimal to convert
     * @return String representation of the decimal
     */
    @TypeConverter
    fun bigDecimalToString(value: BigDecimal?): String? {
        return value?.toPlainString()
    }
    
    /**
     * Convert String back to BigDecimal.
     * 
     * @param value String representation of decimal
     * @return BigDecimal parsed from string, or null if empty
     * @throws NumberFormatException if string is not a valid decimal
     */
    @TypeConverter
    fun stringToBigDecimal(value: String?): BigDecimal? {
        return if (value.isNullOrBlank()) null else BigDecimal(value)
    }
    
    // ============== List<String> Converters ==============
    
    /**
     * Convert List<String> to JSON string.
     * Used for storing image URLs and tags.
     * 
     * @param value List of strings to serialize
     * @return JSON string representation
     */
    @TypeConverter
    fun stringListToJson(value: List<String>?): String? {
        return if (value.isNullOrEmpty()) null else gson.toJson(value)
    }
    
    /**
     * Convert JSON string back to List<String>.
     * 
     * @param value JSON string representation
     * @return List of strings, or empty list if null
     */
    @TypeConverter
    fun jsonToStringList(value: String?): List<String> {
        return if (value.isNullOrBlank()) {
            emptyList()
        } else {
            try {
                val type = object : TypeToken<List<String>>() {}.type
                gson.fromJson(value, type)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    // ============== LocalDateTime Converters ==============
    
    /**
     * Convert LocalDateTime to ISO-8601 string.
     * 
     * @param value LocalDateTime to convert
     * @return ISO-8601 string format
     */
    @TypeConverter
    fun localDateTimeToString(value: LocalDateTime?): String? {
        return value?.format(dateFormatter)
    }
    
    /**
     * Convert ISO-8601 string back to LocalDateTime.
     * 
     * @param value ISO-8601 formatted string
     * @return LocalDateTime parsed from string
     */
    @TypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? {
        return if (value.isNullOrBlank()) null else LocalDateTime.parse(value, dateFormatter)
    }
    
    // ============== Map Converters ==============
    
    /**
     * Convert Map<String, String> to JSON string.
     * Useful for storing metadata or key-value pairs.
     * 
     * @param value Map to serialize
     * @return JSON string representation
     */
    @TypeConverter
    fun mapToJson(value: Map<String, String>?): String? {
        return if (value.isNullOrEmpty()) null else gson.toJson(value)
    }
    
    /**
     * Convert JSON string back to Map<String, String>.
     * 
     * @param value JSON string representation
     * @return Map of strings, or empty map if null
     */
    @TypeConverter
    fun jsonToMap(value: String?): Map<String, String> {
        return if (value.isNullOrBlank()) {
            emptyMap()
        } else {
            try {
                val type = object : TypeToken<Map<String, String>>() {}.type
                gson.fromJson(value, type)
            } catch (e: Exception) {
                emptyMap()
            }
        }
    }
    
    // ============== Boolean Converters ==============
    
    /**
     * Convert Boolean to Int for storage.
     * (Room has native Boolean support, but this is for custom handling)
     * 
     * @param value Boolean to convert
     * @return 1 for true, 0 for false
     */
    @TypeConverter
    fun booleanToInt(value: Boolean?): Int? {
        return when (value) {
            true -> 1
            false -> 0
            null -> null
        }
    }
    
    /**
     * Convert Int back to Boolean.
     * 
     * @param value Int value (1 = true, 0 = false)
     * @return Boolean representation
     */
    @TypeConverter
    fun intToBoolean(value: Int?): Boolean? {
        return when (value) {
            1 -> true
            0 -> false
            else -> null
        }
    }
    
    // ============== Enum Converters ==============
    
    /**
     * Convert OrderStatus enum to String.
     */
    @TypeConverter
    fun orderStatusToString(value: OrderStatus?): String? {
        return value?.name
    }
    
    /**
     * Convert String back to OrderStatus enum.
     */
    @TypeConverter
    fun stringToOrderStatus(value: String?): OrderStatus? {
        return if (value.isNullOrBlank()) null else OrderStatus.valueOf(value)
    }
}

/**
 * Order status enumeration.
 */
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURNED
}
