@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreItemPodcastFeatured(
    val id: Long,
    val name: String,
    val url: String,
    val artistName: String,
    val artistId: Long? = null,
    val artistUrl: String? = null,
    val description: String? = null,
    val feedUrl: String,
    val releaseDate: Instant,
    val releaseDateTime: Instant,
    override val artwork: Artwork?,
    val trackCount: Int,
    val podcastWebsiteUrl: String? = null,
    val copyright: String? = null,
    val contentAdvisoryRating: String? = null,
    val userRating: Float,
    val genre: Genre?
) : StoreItemFeatured {

    override fun getArtworkUrl():String =
        artworkUrl(1060, 520, crop = "fa")
}