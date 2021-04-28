package com.caldeirasoft.outcast.data.util.local

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.caldeirasoft.outcast.domain.models.store.StoreData
import kotlinx.datetime.Instant
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class StoreDataSerializer(val json: Json) : Serializer<StoreData> {
    override val defaultValue = StoreData(id = 0, label = "", storeFront = "")

    override suspend fun readFrom(input: InputStream): StoreData {
        try {
            return json.decodeFromString(
                StoreData.serializer(), input.readBytes().decodeToString())
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read StorePage", serialization)
        }
    }

    override suspend fun writeTo(t: StoreData, output: OutputStream) {
        output.write(json.encodeToString(StoreData.serializer(), t).encodeToByteArray())
    }
}