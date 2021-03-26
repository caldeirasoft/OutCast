@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreFeatured
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class StoreRoom(
    override var id: Long,
    override val label: String,
    override val url: String = "",
    override val description: String? = null,
    override val artwork: Artwork? = null,
    override val storeFront: String,
    var storeIds: List<Long> = arrayListOf(),
    val itemType: StoreItemType = StoreItemType.PODCAST,
    val isIndexed: Boolean = false,
) : StoreFeatured {
    override var featuredArtwork: Artwork? = artwork

    override fun getArtworkUrl(): String =
        StoreItemWithArtwork.artworkUrl(artwork, 400, 196, crop = "fa")

    fun getPage(timestamp: Instant, lookup: Map<Long, StoreItemWithArtwork>): StoreRoomPage =
        StoreRoomPage(
            storeRoom = this,
            storeFront = this.storeFront,
            timestamp = timestamp,
            lookup = lookup
        )

    fun getPage(): StoreRoomPage =
        StoreRoomPage(
            storeRoom = this.copy(artwork = null),
            storeFront = this.storeFront,
            timestamp = Clock.System.now(),
        )
}