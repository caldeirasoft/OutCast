package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.interfaces.StoreItemFeatured
import kotlinx.serialization.Serializable

@Serializable
class StoreRoomFeatured(var label: String,
                        var url: String,
                        override var artwork: Artwork?)
    : StoreItemFeatured {
    override fun getArtworkUrl(): String =
        artworkUrl(1060, 520, crop = "fa")
}