package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import kotlinx.serialization.Serializable

@Serializable
data class StoreRoom(
    var id: Long,
    var label: String,
    val url: String = "",
    var description: String? = null,
    override var artwork: Artwork? = null,
    override val storeFront: String,
    var storeIds: List<Long> = arrayListOf(),
    override var lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf()
) : StoreItemWithArtwork, StorePage {
    override fun getArtworkUrl(): String =
        artworkUrl(400, 196, crop = "fa")
}