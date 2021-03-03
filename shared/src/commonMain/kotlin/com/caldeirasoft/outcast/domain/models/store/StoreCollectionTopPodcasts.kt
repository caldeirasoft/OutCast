package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionTopPodcasts(
    override val id: Long,
    val label: String,
    val genreId: Int? = null,
    var url: String? = null,
    val itemsIds: List<Long> = emptyList(),
    override val storeFront: String,
) : StoreCollection {
    val items: MutableList<StorePodcast> = mutableListOf()

    val room: StoreRoom
        get() = StoreRoom(
            id = 0,
            label = label,
            storeIds = itemsIds,
            storeFront = storeFront,
            itemType = StoreItemType.PODCAST,
            isIndexed = true
        )
}