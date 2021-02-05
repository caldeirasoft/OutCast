@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreMultiRoomPage(
    val storeRoom: StoreMultiRoom,
    override val storeFront: String,
    override val lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf(),
    override val timestamp: Instant,
) : StoreFeaturedPage(storeRoom), StorePageWithCollection {
    override val storeList: MutableList<StoreCollection>
        get() = storeRoom.storeList
}