@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.interfaces.StoreItemFeatured
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.models.PodcastPage
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers

@Serializable
data class StorePodcast(
    override val id: Long,
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
) : StoreItemWithArtwork, StoreItemFeatured {

    @Transient
    val podcast: Podcast =
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
            contentAdvisoryRating = contentAdvisoryRating,
            copyright = copyright,
            description = description,
            feedUrl = feedUrl,
            podcastWebsiteURL = podcastWebsiteUrl,
            releaseDateTime = releaseDateTime,
            trackCount = trackCount.toLong(),
            updatedAt = releaseDateTime,
            userRating = userRating.toDouble(),
            isSubscribed = false,
            newEpisodeAction = NewEpisodesAction.CLEAR
        )

    @Transient
    val page: PodcastPage =
        PodcastPage(
            podcast = this.podcast,
            storeFront = this.storeFront,
            timestamp = Clock.System.now(),
            episodes = listOf(),
        )

    override fun getArtworkUrl():String =
        StoreItemWithArtwork.artworkUrl(artwork, 200, 200)
}