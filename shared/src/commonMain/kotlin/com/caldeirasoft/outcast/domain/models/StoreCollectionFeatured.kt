package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionFeatured(override var items: List<StoreItem>) : StoreCollection {
}