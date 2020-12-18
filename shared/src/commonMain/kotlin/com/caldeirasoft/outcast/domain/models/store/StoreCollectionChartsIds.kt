package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionChartsIds(
    val topPodcastsIds: List<Long>,
    val topEpisodesIds: List<Long>,
    val genreId: Int?,
    override val storeFront: String,
) : StoreCollection