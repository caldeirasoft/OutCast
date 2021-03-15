@file:UseSerializers(InstantSerializer::class)

package com.caldeirasoft.outcast.ui.screen.episode

import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.EpisodeSummary
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers


@Serializable
data class EpisodeArg(
    val episodeId: Long,
    val name: String,
    val url: String,
    val podcastId: Long,
    val podcastName: String,
    val artistName: String,
    val artistId: Long?,
    @Serializable(with = InstantSerializer::class)
    val releaseDateTime: Instant,
    val genre: List<Int>,
    val feedUrl: String,
    val description: String?,
    val artwork: Artwork?,
    val mediaUrl: String,
    val mediaType: String,
    val duration: Long,
) {
    fun toEpisode() = Episode(
        episodeId = episodeId,
        name = name,
        url = url,
        podcastId = podcastId,
        podcastName = podcastName,
        artistName = artistName,
        artistId = artistId,
        releaseDateTime = releaseDateTime,
        genre = genre,
        feedUrl = feedUrl,
        description = description,
        contentAdvisoryRating = null,
        artwork = artwork,
        mediaUrl = mediaUrl,
        mediaType = mediaType,
        duration = duration,
        podcastEpisodeSeason = null,
        podcastEpisodeNumber = null,
        podcastEpisodeWebsiteUrl = null,
        podcastEpisodeType = null,
        updatedAt = Instant.DISTANT_PAST
    )

    companion object {
        fun EpisodeSummary.toEpisodeArg(): EpisodeArg = EpisodeArg(
            episodeId = episodeId,
            name = name,
            url = url,
            podcastId = podcastId,
            podcastName = podcastName,
            artistName = "",
            artistId = 0L,
            releaseDateTime = releaseDateTime,
            genre = listOf(),
            feedUrl = "",
            description = description,
            artwork = artwork,
            mediaUrl = "",
            mediaType = "",
            duration = duration,
        )

        fun StoreEpisode.toEpisodeArg(): EpisodeArg = EpisodeArg(
            episodeId = id,
            name = name,
            url = url,
            podcastId = podcastId,
            podcastName = podcastName,
            artistName = "",
            artistId = 0L,
            releaseDateTime = releaseDateTime,
            genre = listOf(),
            feedUrl = "",
            description = description,
            artwork = artwork,
            mediaUrl = "",
            mediaType = "",
            duration = duration.toLong(),
        )
    }
}