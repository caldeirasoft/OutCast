package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionFeatured(
    override val id: Long,
    var items: List<StoreItemArtwork>,
    override val storeFront: String,
) : StoreCollection