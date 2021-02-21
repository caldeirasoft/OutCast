package com.caldeirasoft.outcast.domain.interfaces

interface StoreFeatured : StoreItemWithArtwork, StoreItemFeatured {
    val label: String
    val url: String
    val description: String?

    override fun getArtworkUrl(): String =
        StoreItemWithArtwork.artworkUrl(artwork, 400, 196, crop = "fa")
}