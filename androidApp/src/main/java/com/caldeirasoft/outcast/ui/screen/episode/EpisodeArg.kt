@file:UseSerializers(InstantSerializer::class)

package com.caldeirasoft.outcast.ui.screen.episode

import android.os.Parcelable
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastArg
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastArg.Companion.toPodcastArg
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.UseSerializers


@Parcelize
data class EpisodeArg(
    val guid: String,
    val name: String,
    val url: String,
    val podcastName: String,
    val podcastId: Long? = null,
    val feedUrl: String,
    val artistName: String,
    val artworkUrl: String,
    val duration: Int,
    val podcastArg: PodcastArg? = null
) : Parcelable {
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
            podcastArg = storePodcast.toPodcastArg()
        )
    }
}