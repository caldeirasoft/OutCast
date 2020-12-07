package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreCollectionPodcastsEpisodes
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionPodcasts(
    var label: String,
    var url: String? = null,
    override val storeFront: String,
    override val itemsIds: List<Long>) : StoreCollectionPodcastsEpisodes {

    override var items: List<StoreItemWithArtwork> = mutableListOf()
    val isEmpty = items.isEmpty()
}