package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import kotlinx.serialization.Serializable

@Serializable
data class StoreCollectionPodcasts(
    override val id: Long,
    var label: String,
    var url: String? = null,
    override val storeFront: String,
    val itemsIds: List<Long> = emptyList(),
    val sortByPopularity: Boolean = false,
) : StoreCollection {
    val items: MutableList<StorePodcast> = mutableListOf()

    val room: StoreRoom
        get() = StoreRoom(
            id = 0,
            label = label,
            storeIds = itemsIds,
            url = url.orEmpty(),
            storeFront = storeFront,
            isIndexed = sortByPopularity
        )

}