@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class StoreRoom(
    var id: Long,
    var label: String,
    val url: String = "",
    override var artwork: Artwork? = null,
    override val storeFront: String,
    var storeIds: List<Long> = arrayListOf(),
) : StoreItemWithArtwork {
    override fun getArtworkUrl(): String =
        artworkUrl(400, 196, crop = "fa")

    val page: StoreRoomPage =
        StoreRoomPage(
            id = id,
            label = label,
            url = url,
            artwork = artwork,
            storeFront = storeFront,
            storeIds = storeIds,
            timestamp = Clock.System.now()
        )
}