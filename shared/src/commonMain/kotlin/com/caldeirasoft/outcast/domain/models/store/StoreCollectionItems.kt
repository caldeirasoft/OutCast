package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import kotlinx.serialization.Serializable

@Serializable
data class StoreCollectionItems(
    override val id: Long,
    var label: String,
    var url: String? = null,
    override val storeFront: String,
    val itemsIds: List<Long> = emptyList(),
    var items: List<StoreItemArtwork> = mutableListOf(),
    val sortByPopularity: Boolean = false,
) : StoreCollection {
    val room: StoreData
        get() = StoreData(
            id = 0,
            label = label,
            storeIds = itemsIds,
            url = url.orEmpty(),
            storeFront = storeFront,
            sortByPopularity = sortByPopularity
        )

}