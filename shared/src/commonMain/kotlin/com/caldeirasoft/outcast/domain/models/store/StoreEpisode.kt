@file:UseSerializers(InstantSerializer::class)

package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.interfaces.StoreItemFeatured
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers

@Serializable
class StoreEpisode(override val id: Long,
                   val name: String,
                   val url: String,
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
                   override val storeFront: String = "",
                   override val artwork: Artwork? = null,
                   val isComplete: Boolean = false,
                   val podcast: StorePodcast,
) : StoreItemWithArtwork, StoreItemFeatured {
    override var featuredArtwork: Artwork? = null

    override fun getArtworkUrl(): String =
        StoreItemWithArtwork.artworkUrl(artwork, 200, 200)

    @Transient
    val episode: Episode =
        Episode(
            episodeId = this.id,
            name = this.name,
            url = this.url,
            podcastId = this.podcastId,
            podcastName = this.podcastName,
            artistName = this.artistName,
            artistId = this.artistId,
            description = this.description,
            genreId = this.genres.first().id,
            feedUrl = this.feedUrl,
            releaseDateTime = this.releaseDateTime,
            artwork = this.artwork,
            contentAdvisoryRating = this.contentAdvisoryRating,
            mediaUrl = this.mediaUrl,
            mediaType = this.mediaType,
            duration = this.duration,
            podcastEpisodeNumber = this.podcastEpisodeNumber,
            podcastEpisodeSeason = this.podcastEpisodeSeason,
            podcastEpisodeType = this.podcastEpisodeType,
            podcastEpisodeWebsiteUrl = this.podcastEpisodeWebsiteUrl,
            updatedAt = Clock.System.now(),
            playbackPosition = null,
            isPlayed = false,
            isFavorite = false,
            playedAt = null,
        )
}