@file:UseSerializers(InstantSerializer::class)

package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import android.os.Parcelable
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.UseSerializers


@Parcelize
data class StorePodcastArg(
    val id: Long,
    val name: String,
    val url: String,
    val feedUrl: String,
    val artistName: String,
    val artistId: Long? = null,
    val artistUrl: String? = null,
    val artworkUrl: String,
    val artworkDominantColor: String? = null,
    val storeFront: String,
) : Parcelable {
    fun toPodcast() =
        Podcast(
            podcastId = id,
            name = name,
            url = url,
            artistName = artistName,
            artistId = artistId,
            artistUrl = artistUrl,
            description = null,
            feedUrl = feedUrl,
            releaseDateTime = Clock.System.now(),
            artworkUrl = artworkUrl,
            artworkDominantColor = artworkDominantColor,
            trackCount = 0,
            copyright = "",
            isExplicit = false,
            category = null,
            newFeedUrl = null,
            isComplete = false,
            isSubscribed = false,
            podcastWebsiteURL = null,
            userRating = 0.0,
            newEpisodeAction = NewEpisodesAction.CLEAR,
            updatedAt = Instant.DISTANT_PAST,
        )

    companion object {
        fun Podcast.toStorePodcastArg() = StorePodcastArg(
            id = 0L,
            name = name,
            url = url,
            feedUrl = feedUrl,
            artistId = artistId,
            artistName = artistName,
            artistUrl = artistUrl,
            artworkUrl = artworkUrl,
            artworkDominantColor = artworkDominantColor,
            storeFront = "",
        )

        fun StorePodcast.toStorePodcastArg() = StorePodcastArg(
            id = id,
            name = name,
            url = url,
            feedUrl = feedUrl,
            artistId = artistId,
            artistName = artistName,
            artistUrl = artistUrl,
            artworkUrl = artwork?.getArtworkPodcast().orEmpty(),
            artworkDominantColor = artwork?.bgColor,
            storeFront = storeFront,
        )
    }
}