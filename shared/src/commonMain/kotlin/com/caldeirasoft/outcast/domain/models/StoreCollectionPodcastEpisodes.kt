package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionPodcastEpisodes(
    var label: String,
    var url: String? = null,
    val itemsIds: List<Long>) : StoreCollection {

    override var items: List<StoreItem> = mutableListOf()
    val isEmpty = items.isEmpty()
}