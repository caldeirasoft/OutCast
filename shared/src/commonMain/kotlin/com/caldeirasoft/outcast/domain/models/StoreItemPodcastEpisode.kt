@file:UseSerializers(InstantSerializer::class)

package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreItemPodcastEpisode(val id: Long,
                              val name: String,
                              val podcastId: Long,
                              val podcastName: String,
                              val artistName: String,
                              val artistId: Long? = null,
                              val releaseDateTime: Instant,
                              val genres: List<Genre>,
                              val feedUrl: String,
                              val description: String? = null,
                              val contentAdvisoryRating: String? = null,
                              val mediaUrl: String,
                              val mediaType: String,
                              val duration: Int,
                              val playbackPosition: Int? = null,
                              val podcastEpisodeSeason: Int? = null,
                              val podcastEpisodeNumber: Int? = null,
                              val podcastEpisodeWebsiteUrl: String? = null,
                              val podcastEpisodeType: String = "",
                              override val artwork: Artwork? = null,
                              ) : StoreItem {
    override fun getArtworkUrl():String =
        artworkUrl(200, 200)
}