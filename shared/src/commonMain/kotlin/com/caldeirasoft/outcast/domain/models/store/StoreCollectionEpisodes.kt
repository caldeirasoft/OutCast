package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import kotlinx.serialization.Serializable

@Serializable
data class StoreCollectionEpisodes(
    override val id: Long,
    var label: String,
    var url: String? = null,
    override val storeFront: String,
    val itemsIds: List<Long> = emptyList(),
    val items: MutableList<StoreEpisode> = mutableListOf(),
    val sortByPopularity: Boolean = false,
) : StoreCollection {
    val room: StoreRoom
        get() = StoreRoom(
            id = 0,
            label = label,
            storeIds = itemsIds,
            url = url.orEmpty(),
            storeFront = storeFront,
            isIndexed = sortByPopularity,
            itemType = StoreItemType.EPISODE,
        )

}