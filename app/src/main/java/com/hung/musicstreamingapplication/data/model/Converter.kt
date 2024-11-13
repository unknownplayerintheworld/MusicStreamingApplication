package com.hung.musicstreamingapplication.data.model

import androidx.room.TypeConverter
import com.google.firebase.Timestamp

class Converters {

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.split(",")
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun fromTimestamp(value: Timestamp?): Long? {
        return value?.seconds // Chuyển đổi Timestamp thành Long
    }

    @TypeConverter
    fun toTimestamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(it,0) } // Chuyển đổi Long thành Timestamp
    }
}