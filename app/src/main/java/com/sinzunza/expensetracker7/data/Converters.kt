package com.sinzunza.expensetracker7.data

import androidx.room.TypeConverter
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromEpochMillis(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun instantToEpocMillis(instant: Instant?): Long? = instant?.toEpochMilli()
}