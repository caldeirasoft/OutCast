@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class StoreRoomPage(
    var id: Long,
    var label: String,
    val url: String = "",
    var description: String? = null,
    override var artwork: Artwork? = null,
    override val storeFront: String,
    var storeIds: List<Long> = arrayListOf(),
    override var lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf(),
    override val timestamp: Instant,
) : StoreItemWithArtwork, StorePage {
    override fun getArtworkUrl(): String =
        artworkUrl(400, 196, crop = "fa")
}