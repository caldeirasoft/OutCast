package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.interfaces.*
import kotlinx.serialization.Serializable

@Serializable
class StoreMultiRoom(
    var id: Long,
    var label: String,
    val url: String = "",
    var description: String? = null,
    override var artwork: Artwork? = null,
    override val storeFront: String,
    override val storeList: List<StoreCollection> = arrayListOf(),
    override var lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf()
) : StoreItemWithArtwork, StorePage, StoreDataWithCollections {
    override fun getArtworkUrl(): String =
        artworkUrl(400, 196, crop = "fa")
}