package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.interfaces.StoreItemFeatured
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import kotlinx.serialization.Serializable

@Serializable
class StoreRoomFeatured(var label: String,
                        var url: String,
                        override val storeFront: String,
                        override var artwork: Artwork?)
    : StoreItemWithArtwork, StoreItemFeatured {
    override fun getArtworkUrl(): String =
        artworkUrl(1060, 520, crop = "fa")
}