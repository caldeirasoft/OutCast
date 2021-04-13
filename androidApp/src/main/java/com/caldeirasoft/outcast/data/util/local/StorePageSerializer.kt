package com.caldeirasoft.outcast.data.util.local

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePage
import kotlinx.datetime.Instant
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class StorePageSerializer(val json: Json) : Serializer<StorePage> {
    override val defaultValue = StorePage(
        storeData = StoreData(id = 0, label = "", storeFront = ""),
        storeFront = "",
        timestamp = Instant.DISTANT_PAST
    )

    override suspend fun readFrom(input: InputStream): StorePage {
        try {
            return json.decodeFromString(
                StorePage.serializer(), input.readBytes().decodeToString())
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read StorePage", serialization)
        }
    }

    override suspend fun writeTo(t: StorePage, output: OutputStream) {
        output.write(json.encodeToString(StorePage.serializer(), t).encodeToByteArray())
    }
}