package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionCharts(
    val topPodcasts: List<StorePodcast>,
    val topEpisodes: List<StoreEpisode>,
    val genreId: Int,
    override val storeFront: String,
) : StoreCollection