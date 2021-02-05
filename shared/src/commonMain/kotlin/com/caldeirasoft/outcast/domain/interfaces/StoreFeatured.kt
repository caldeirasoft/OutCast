package com.caldeirasoft.outcast.domain.interfaces

import com.caldeirasoft.outcast.domain.interfaces.StoreItemFeatured
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.models.Artwork
import kotlinx.serialization.Serializable

interface StoreFeatured : StoreItemWithArtwork, StoreItemFeatured {
    val label: String
    val url: String
    val description: String?

    override fun getArtworkUrl(): String =
        StoreItemWithArtwork.artworkUrl(artwork, 400, 196, crop = "fa")
}