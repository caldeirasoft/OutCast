package com.caldeirasoft.outcast.di.adapters;

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import kotlinx.datetime.Instant

class InstantJsonAdapter : JsonAdapter<Instant>() {
    override fun fromJson(reader: JsonReader): Instant =
        Instant.fromEpochMilliseconds(reader.nextLong())

    override fun toJson(writer: JsonWriter, value: Instant?) {
        writer.value(value?.toEpochMilliseconds())
    }
}