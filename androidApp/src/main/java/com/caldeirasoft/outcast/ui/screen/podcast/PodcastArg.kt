@file:UseSerializers(InstantSerializer::class)

package com.caldeirasoft.outcast.ui.screen.podcast

import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers


@Serializable
data class PodcastArg(
    val id: Long,
    val name: String,
    val url: String,
    val artistName: String,
    val artistId: Long? = null,
    val artistUrl: String? = null,
    val description: String? = null,
    @Serializable(with = InstantSerializer::class)
    val releaseDateTime: Instant,
    val artwork: Artwork?,
    val trackCount: Int,
    val genre: Genre?,
) {
    fun toPodcast() =
        Podcast(
            podcastId = id,
            name = name,
            artistName = artistName,
            url = url,
            genreId = genre?.id,
            genre = genre,
            artwork = artwork,
            artistId = artistId,
            artistUrl = artistUrl,
            contentAdvisoryRating = null,
            copyright = null,
            description = description,
            feedUrl = "",
            podcastWebsiteURL = null,
            releaseDateTime = releaseDateTime,
            trackCount = trackCount.toLong(),
            updatedAt = releaseDateTime,
            userRating = null,
            isSubscribed = false,
            newEpisodeAction = NewEpisodesAction.CLEAR,
        )

    companion object {
        fun Podcast.toPodcastArg() = PodcastArg(
            id = podcastId,
            name = name,
            artistName = artistName,
            url = url,
            genre = genre,
            artwork = artwork,
            artistId = artistId,
            artistUrl = artistUrl,
            description = description,
            releaseDateTime = releaseDateTime,
            trackCount = trackCount.toInt(),
        )
    }
}