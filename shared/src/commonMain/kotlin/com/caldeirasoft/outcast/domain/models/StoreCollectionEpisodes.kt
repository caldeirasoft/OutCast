package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreCollectionPodcastsEpisodes
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionEpisodes(
    var label: String,
    var url: String? = null,
    override val itemsIds: List<Long>) : StoreCollectionPodcastsEpisodes {

    override var items: List<StoreItem> = mutableListOf()
    val isEmpty = items.isEmpty()
}