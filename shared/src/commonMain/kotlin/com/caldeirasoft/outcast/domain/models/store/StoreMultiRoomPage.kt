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
    var id: Long,
    var label: String,
    val url: String = "",
    var description: String? = null,
    override var artwork: Artwork? = null,
    override val storeFront: String,
    override val storeList: List<StoreCollection> = arrayListOf(),
    override var lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf(),
    override val timestamp: Instant,
) : StoreItemWithArtwork, StorePage, StoreDataWithCollections {
    override fun getArtworkUrl(): String =
        artworkUrl(400, 196, crop = "fa")
}