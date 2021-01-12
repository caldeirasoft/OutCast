@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class StorePodcastPage(
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
    val genre: Genre?,
    override val storeFront: String,
    val episodes: List<StoreEpisode> = listOf(),
    val podcastsByArtist: StoreCollectionPodcastIds? = null,
    val podcastsListenersAlsoFollow: StoreCollectionPodcastIds? = null,
) : StoreItemWithArtwork {
    override fun getArtworkUrl():String =
        StoreItemWithArtwork.artworkUrl(artwork, 200, 200)
}