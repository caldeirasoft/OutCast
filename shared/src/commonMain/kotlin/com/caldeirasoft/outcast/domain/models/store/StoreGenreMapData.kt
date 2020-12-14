@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreGenreMapData(
    val root: StoreGenre,
    val genreMap: Map<Int, StoreGenre> = hashMapOf(),
    val genreChildren: Map<Int, List<StoreGenre>> = hashMapOf(),
)