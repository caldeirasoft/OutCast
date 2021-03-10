@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.interfaces.StorePageWithCollection
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.UseSerializers

class PodcastPage(
    val podcast: Podcast,
    override val storeFront: String,
    val episodes: List<Episode>,
    val otherPodcasts: MutableList<StoreCollection> = mutableListOf(),
    override val timestamp: Instant
) : StorePageWithCollection {
    val name = podcast.name
    val artistName = podcast.artistName
    val artwork = podcast.artwork
    val description = podcast.description
    val genre = podcast.genre

    override val storeList: MutableList<StoreCollection> = otherPodcasts
    override val lookup: Map<Long, StoreItemWithArtwork> = hashMapOf()

    fun getArtworkUrl() = StoreItemWithArtwork.artworkUrl(artwork, 200, 200)

    val storeEpisodeTrailer: Episode? = episodes.firstOrNull { it.podcastEpisodeType == "trailer" }
    val recentEpisodes: List<Episode> = episodes.sortedByDescending { it.releaseDateTime }.take(5)
}