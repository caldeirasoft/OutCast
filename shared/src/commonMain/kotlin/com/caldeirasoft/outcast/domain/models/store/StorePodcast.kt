@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
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
) : StoreItemArtwork {

    override var featuredArtwork: Artwork? = null
    override var editorialArtwork: Artwork? = null

    var moreByArtist: List<Long>? = null
    var listenersAlsoBought: List<Long>? = null
    var topPodcastsInGenre: List<Long>? = null

    @Transient
    var episodes: List<Episode> = mutableListOf()

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
            newEpisodeAction = NewEpisodesAction.CLEAR,
        )

    override fun getArtworkUrl():String =
        StoreItemArtwork.artworkUrl(artwork, 200, 200)
}