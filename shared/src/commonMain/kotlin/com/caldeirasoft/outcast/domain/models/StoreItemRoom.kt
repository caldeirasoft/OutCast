package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

@Serializable
class StoreItemRoom(var label: String,
                    var url: String,
                    override var artwork: Artwork?) : StoreItem {
    override fun getArtworkUrl(): String =
        artworkUrl(400, 196, crop = "fa")
}