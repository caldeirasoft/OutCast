package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionFeatured(override var items: List<StoreItem>) : StoreCollection {
}