package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionCharts(
    override val id: Long,
    val topPodcastsIds: List<Long>,
    val topEpisodesIds: List<Long>,
    val topPodcasts: MutableList<StorePodcast> = mutableListOf(),
    val topEpisodes: MutableList<StoreEpisode> = mutableListOf(),
    val genreId: Int?,
    override val storeFront: String,
) : StoreCollection