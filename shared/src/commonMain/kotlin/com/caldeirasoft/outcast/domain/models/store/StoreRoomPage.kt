@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreFeatured
import com.caldeirasoft.outcast.domain.interfaces.StoreFeaturedPage
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreRoomPage(
    val storeRoom: StoreRoom,
    override val storeFront: String,
    override val lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf(),
    override val timestamp: Instant,
) : StoreFeaturedPage(storeRoom) {
    val isIndexed get() = storeRoom.isIndexed
}