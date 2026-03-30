package com.example.pract7_v8



import androidx.room.TypeConverter
import java.util.Date

class Converters {

    // ✅ Date → Long (timestamp)
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    // ✅ Long (timestamp) → Date
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}