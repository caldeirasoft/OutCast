package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionData(
    override val id: Long,
    var label: String,
    override val storeFront: String,
    var items: List<StoreItemArtwork>,
) : StoreCollection {
}