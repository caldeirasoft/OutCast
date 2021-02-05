package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionRooms(
    override val id: Long,
    var label: String,
    override val storeFront: String,
    var items: List<StoreItemWithArtwork>
) : StoreCollection {
}