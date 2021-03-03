package com.caldeirasoft.outcast.di.adapters;

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import kotlinx.datetime.Instant

class InstantStringJsonAdapter : JsonAdapter<Instant>() {
    override fun fromJson(reader: JsonReader): Instant =
        Instant.parse(reader.nextString())

    override fun toJson(writer: JsonWriter, value: Instant?) {
        writer.value(value?.toString())
    }
}