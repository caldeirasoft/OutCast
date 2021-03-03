package com.caldeirasoft.outcast.di.adapters;

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import kotlinx.datetime.LocalDate

class LocalDateJsonAdapter : JsonAdapter<LocalDate>() {
    override fun fromJson(reader: JsonReader): LocalDate =
        LocalDate.parse(reader.nextString())

    override fun toJson(writer: JsonWriter, value: LocalDate?) {
        writer.value(value?.toString())
    }
}