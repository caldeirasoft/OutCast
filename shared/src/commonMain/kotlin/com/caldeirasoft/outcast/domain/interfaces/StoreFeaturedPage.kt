@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.interfaces

import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.UseSerializers

interface StoreFeaturedPage : StoreItemWithArtwork, StorePage {
    var label: String
    val url: String
    var description: String?
    override var artwork: Artwork?
    override val storeFront: String
    override var lookup: Map<Long, StoreItemWithArtwork>
    override val timestamp: Instant
    override fun getArtworkUrl(): String =
        StoreItemWithArtwork.artworkUrl(artwork, 400, 196, crop = "fa")
}