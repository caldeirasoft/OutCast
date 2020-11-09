@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreDataPodcast(
    val id: Long,
    val artwork: Artwork? = null,
    val name: String,
    val description: String? = null,
    val url: String,
    val artistName: String,
    val artistId: Long? = null,
    val artistUrl: String? = null,
    val feedUrl: String,
    val releaseDate: Instant,
    val releaseDateTime: Instant,
    val trackCount: Int = 0,
    val podcastWebsiteUrl: String? = null,
    val copyright: String? = null,
    val contentAdvisoryRating: String? = null,
    val userRating: Float,
    val genre: Genre? = null,
    val episodes: List<StoreItemPodcastEpisode>,
    val podcastsByArtist: StoreIdsPodcasts,
    val podcastsListenersAlsoFollow: StoreIdsPodcasts
) : StoreData {
    
}