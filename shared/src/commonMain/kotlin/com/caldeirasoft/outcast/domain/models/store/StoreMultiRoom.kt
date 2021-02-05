@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreMultiRoom(
    override var id: Long,
    override var label: String,
    override val url: String = "",
    override val description: String? = null,
    override val artwork: Artwork? = null,
    override val storeFront: String,
    override val storeList: MutableList<StoreCollection> = mutableListOf(),
) : StoreFeatured, StoreDataWithCollections {
    override fun getArtworkUrl(): String =
        StoreItemWithArtwork.artworkUrl(artwork, 400, 196, crop = "fa")

    fun getPage(timestamp: Instant, lookup: Map<Long, StoreItemWithArtwork>): StoreMultiRoomPage =
        StoreMultiRoomPage(
            storeRoom = this,
            storeFront = this.storeFront,
            timestamp = timestamp,
            lookup = lookup
        )
}