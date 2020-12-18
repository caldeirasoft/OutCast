package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionRooms(
    var label: String,
    override val storeFront: String,
    var items: List<StoreItemWithArtwork>) : StoreCollection {
}