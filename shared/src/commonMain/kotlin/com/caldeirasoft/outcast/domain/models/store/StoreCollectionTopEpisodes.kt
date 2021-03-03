package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionTopEpisodes(
    override val id: Long,
    val label: String,
    val genreId: Int? = null,
    var url: String? = null,
    val itemsIds: List<Long> = emptyList(),
    override val storeFront: String,
) : StoreCollection {
    val items: MutableList<StoreEpisode> = mutableListOf()

    val room: StoreRoom
        get() = StoreRoom(
            id = 0,
            label = label,
            storeIds = itemsIds,
            storeFront = storeFront,
            itemType = StoreItemType.EPISODE,
            isIndexed = true
        )
}