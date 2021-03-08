@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.interfaces.StorePageWithCollection
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StorePodcastPage(
    val storeData: StorePodcast,
    override val storeFront: String,
    val episodes: List<StoreEpisode>,
    val otherPodcasts: MutableList<StoreCollection> = mutableListOf(),
    override val timestamp: Instant
) : StorePage, StorePageWithCollection {
    override val lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf()

    val name = storeData.name
    val artistName = storeData.artistName
    val artwork = storeData.artwork
    val description = storeData.description
    val genre = storeData.genre
    override val storeList: MutableList<StoreCollection> = otherPodcasts

    fun getArtworkUrl() = storeData.getArtworkUrl()

    val storeEpisodeTrailer: StoreEpisode? = episodes.firstOrNull { it.podcastEpisodeType == "trailer" }
    val recentEpisodes: List<StoreEpisode> = episodes.sortedByDescending { it.releaseDateTime }.take(5)
}