package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionFeatured(val items: List<StoreItem>) : StoreCollection {
}