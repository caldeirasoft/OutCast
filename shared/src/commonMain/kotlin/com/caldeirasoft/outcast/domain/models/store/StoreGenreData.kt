@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreGenreData(
    val root: StoreGenre,
    val genres: List<StoreGenre> = mutableListOf(),
)