package com.noghre.sod.data.local

from androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.noghre.sod.data.model.Address
import com.noghre.sod.data.model.OrderItem

/**
 * Type converters for Room database.
 */
class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromAddressList(value: List<Address>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toAddressList(value: String?): List<Address>? {
        return value?.let {
            val type = object : TypeToken<List<Address>>() {}.type
            gson.fromJson(it, type)
        }
    }

    @TypeConverter
    fun fromOrderItemList(value: List<OrderItem>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toOrderItemList(value: String?): List<OrderItem>? {
        return value?.let {
            val type = object : TypeToken<List<OrderItem>>() {}.type
            gson.fromJson(it, type)
        }
    }
}
