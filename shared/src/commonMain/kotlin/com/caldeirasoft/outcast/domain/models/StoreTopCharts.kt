package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import kotlinx.serialization.Serializable

@Serializable
class StoreTopCharts(
    var id: Long,
    var label: String,
    override val storeFront: String,
    var storePodcastsIds: List<Long> = arrayListOf(),
    var storeEpisodesIds: List<Long> = arrayListOf(),
    override var lookup: Map<Long, StoreItemWithArtwork> = mutableMapOf()
) : StorePage {
}