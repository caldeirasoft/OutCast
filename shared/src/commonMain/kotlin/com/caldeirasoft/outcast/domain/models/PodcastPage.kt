@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.interfaces.StorePageWithCollection
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
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

    val artist: StoreRoom?
        get() =
            podcast.artistUrl?.let {
                StoreRoom(
                    id = podcast.artistId ?: 0L,
                    label = podcast.artistName,
                    url = podcast.artistUrl.orEmpty(),
                    storeFront = storeFront
                )
            }

    override val storeList: MutableList<StoreCollection> = otherPodcasts
    override val lookup: Map<Long, StoreItemWithArtwork> = hashMapOf()

    fun getArtworkUrl() = StoreItemWithArtwork.artworkUrl(artwork, 200, 200)
}