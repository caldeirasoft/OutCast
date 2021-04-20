package com.caldeirasoft.outcast.data.db.typeconverters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import java.util.Date

/**
 * Database converters that will convert string to instant and vise versa when inserting/pulling from the database.
 */
internal class InstantConverter {
    @TypeConverter
    fun fromString(value: Long?): Instant? = value?.let { Instant.fromEpochSeconds(it) }

    @TypeConverter
    fun instantToString(instant: Instant?): Long? = instant?.epochSeconds
}