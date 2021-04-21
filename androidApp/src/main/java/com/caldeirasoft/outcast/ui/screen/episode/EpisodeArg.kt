@file:UseSerializers(InstantSerializer::class)

package com.caldeirasoft.outcast.ui.screen.episode

import android.os.Parcelable
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.UseSerializers


@Parcelize
data class EpisodeArg(
    val guid: String,
    val name: String,
    val url: String,
    val podcastName: String,
    val artistName: String,
    val feedUrl: String,
    val artworkUrl: String,
    val duration: Int,
) : Parcelable {
    fun toEpisode() = Episode(
        guid = guid,
        name = name,
        url = url,
        podcastId = null,
        podcastName = podcastName,
        artistName = artistName,
        artistId = null,
        releaseDateTime = Instant.DISTANT_PAST,
        feedUrl = feedUrl,
        description = null,
        isExplicit = false,
        artworkUrl = artworkUrl,
        mediaUrl = "",
        mediaType = "",
        duration = duration,
        podcastEpisodeSeason = null,
        podcastEpisodeNumber = null,
        podcastEpisodeWebsiteUrl = null,
        podcastEpisodeType = null,
        updatedAt = Instant.DISTANT_PAST,
        playbackPosition = null,
        isPlayed = false,
        isFavorite = false,
        playedAt = null,
    )

    companion object {
        fun Episode.toEpisodeArg() = EpisodeArg(
            guid = guid,
            name = name,
            url = url,
            podcastName = podcastName,
            artistName = artistName,
            feedUrl = feedUrl,
            artworkUrl = artworkUrl,
            duration = duration,
        )

        fun StoreEpisode.toEpisodeArg(): EpisodeArg = EpisodeArg(
            guid = guid,
            name = name,
            url = url,
            podcastName = podcastName,
            artistName = artistName,
            feedUrl = feedUrl,
            artworkUrl = artwork?.getArtworkPodcast().orEmpty(),
            duration = duration,
        )
    }
}